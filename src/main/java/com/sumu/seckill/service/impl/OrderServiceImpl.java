package com.sumu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sumu.seckill.exception.GlobalException;
import com.sumu.seckill.mapper.OrderMapper;
import com.sumu.seckill.pojo.Order;
import com.sumu.seckill.service.IGoodsService;
import com.sumu.seckill.service.IOrderService;
import com.sumu.seckill.vo.GoodsVo;
import com.sumu.seckill.vo.OrderDetailVo;
import com.sumu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author SuMu
 * @since 2021-05-06
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private IGoodsService goodsService;
    /**
     * 订单详情信息
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoodsVo(goodsVo);
        return orderDetailVo;
    }
}
