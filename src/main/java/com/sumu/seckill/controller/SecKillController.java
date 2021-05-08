package com.sumu.seckill.controller;

import com.sumu.seckill.pojo.SeckillMessage;
import com.sumu.seckill.pojo.SeckillOrder;
import com.sumu.seckill.pojo.User;
import com.sumu.seckill.rabbitmq.MQSender;
import com.sumu.seckill.service.IGoodsService;
import com.sumu.seckill.service.IOrderService;
import com.sumu.seckill.service.ISeckillOrderService;
import com.sumu.seckill.utils.JsonUtil;
import com.sumu.seckill.vo.GoodsVo;
import com.sumu.seckill.vo.RespBean;
import com.sumu.seckill.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 商品秒杀
 */
@Slf4j
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> redisScript;

    // 用于内存标记
    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

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
    @PostMapping("/{path}/doSeckill")
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId) {

        // 判断用户是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // 校验接口地址
        Boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
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
        // 库存预减，lua脚本执行
//        Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);

        // 库存不足
        if (stock < 0) {
            // 将内存标记中的库存设置为空
            emptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
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
     *
     * @param user
     * @param goodsId
     * @return 查询orderId：有记录返回（秒杀成功），秒杀失败：-1，排队中：0
     */
    @GetMapping("/result")
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        // 判断用户是否存在
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 获取秒杀地址
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/path")
    @ResponseBody
    public RespBean getSecKillPath(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        // 创建秒杀地址
        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    @GetMapping("/captcha")
    public void captcha(User user, Long goodsId, HttpServletResponse response) {
        // 设置请求头为输出图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        // 算术类型
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);

        // 输出图片流
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败" + e.getMessage());
        }
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
