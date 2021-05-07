package com.sumu.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sumu.seckill.pojo.Order;
import com.sumu.seckill.pojo.SeckillOrder;
import com.sumu.seckill.pojo.User;
import com.sumu.seckill.service.IGoodsService;
import com.sumu.seckill.service.ISeckillOrderService;
import com.sumu.seckill.vo.GoodsVo;
import com.sumu.seckill.vo.RespBean;
import com.sumu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 商品秒杀
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀
     * 优化前：
     * Win QPS：2285.6
     * Linux QPS：644.3
     * 优化后：
     * Win QPS：2294.4
     * Linux QPS：
     *
     * @param user
     * @param goodsId
     * @return
     */
    @PostMapping("/doSeckill")
    @ResponseBody
    public RespBean doSeckill(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        // 判断库存
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 判断订单（查询用户是否有重复抢购行为）
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        SeckillOrder  seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        System.out.println("秒杀订单："+seckillOrder);
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        Order order = seckillOrderService.seckill(user, goods);
        return RespBean.success(order);
    }

    /**
     * 秒杀
     * 优化前：
     * Win QPS：2285.6
     * Linux QPS：644.3
     * 优化后：
     * Win QPS：
     * Linux QPS：
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill2")
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
    }
}
