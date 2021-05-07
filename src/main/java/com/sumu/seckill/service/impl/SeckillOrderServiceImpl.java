package com.sumu.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sumu.seckill.mapper.OrderMapper;
import com.sumu.seckill.mapper.SeckillOrderMapper;
import com.sumu.seckill.pojo.Order;
import com.sumu.seckill.pojo.SeckillGoods;
import com.sumu.seckill.pojo.SeckillOrder;
import com.sumu.seckill.pojo.User;
import com.sumu.seckill.service.ISeckillGoodsService;
import com.sumu.seckill.service.ISeckillOrderService;
import com.sumu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author SuMu
 * @since 2021-05-06
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        // 秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() -1);
        boolean seckillResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count-1").eq("goods_id", goods.getId()).gt("stock_count", 0));
        if (!seckillResult) {
            return null;
        }
        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrderMapper.insert(seckillOrder);
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goods.getId(), seckillOrder);
        return order;
    }
}
