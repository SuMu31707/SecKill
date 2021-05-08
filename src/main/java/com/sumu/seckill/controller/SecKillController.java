package com.sumu.seckill.controller;

import com.sumu.seckill.pojo.SeckillMessage;
import com.sumu.seckill.pojo.SeckillOrder;
import com.sumu.seckill.pojo.User;
import com.sumu.seckill.rabbitmq.MQSender;
import com.sumu.seckill.service.IGoodsService;
import com.sumu.seckill.service.ISeckillOrderService;
import com.sumu.seckill.utils.JsonUtil;
import com.sumu.seckill.vo.GoodsVo;
import com.sumu.seckill.vo.RespBean;
import com.sumu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品秒杀
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;

    // 用于内存标记
    private Map<Long,Boolean> emptyStockMap = new HashMap<>();

    /**
     * 秒杀
     * 优化前：
     * Win QPS：2285.6
     * Linux QPS：644.3
     * 页面优化后：
     * Win QPS：2294.4
     * Linux QPS：
     * 接口优化后：
     * Win QPS：3501.8
     * Linux QPS：
     *
     * @param user
     * @param goodsId
     * @return
     */
    @PostMapping("/doSeckill")
    @ResponseBody
    public RespBean doSeckill(User user, Long goodsId) {

        // 判断用户是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        // 判断订单（查询用户是否有重复抢购行为）
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        // 通过内存标记减少Redis的访问
        if (emptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        // 库存不足
        if (stock < 0) {
            // 将内存标记中的库存设置为空
            emptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodsId(goodsId);
        mqSender.sendSecKillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);

        /*GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        // 判断库存
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 判断订单（查询用户是否有重复抢购行为）
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));

        Order order = seckillOrderService.seckill(user, goods);
        return RespBean.success(order);*/
    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return 查询orderId：有记录返回（秒杀成功），秒杀失败：-1，排队中：0
     */
    @GetMapping("/result")
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        // 判断用户是否存在
        if (user ==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 秒杀
     * 优化前：
     * Win QPS：2285.6
     * Linux QPS：644.3
     * 优化后：
     * Win QPS：
     * Linux QPS：
     * 已不用这个方法
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    /*@RequestMapping("/doSeckill2")
    public String doSeckill2(Model model, User user, Long goodsId) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        // 判断库存
        if (goods.getStockCount() < 1) {
            model.addAttribute("errMsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        // 判断订单（查询用户是否有重复抢购行为）
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errMsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = seckillOrderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }*/

    /**
     * 系统初始化是将秒杀商品库存信息存入Redis
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.getGoodsVo();
        // 判断是否存在秒杀商品
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 将秒杀商品库存信息存入redis
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            // 刚初始化库存，将内存库存标记设置为false，即库存不为空
            emptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
