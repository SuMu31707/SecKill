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
import org.springframework.data.redis.core.ValueOperations;
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
     * 查询秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder !=null) {
            return seckillOrder.getOrderId();
        } else if (redisTemplate.hasKey("isStockEmpty:"+goodsId)){
            return -1L;
        }else {
            return 0L;
        }
    }
}
