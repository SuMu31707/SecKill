package com.sumu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sumu.seckill.pojo.Order;
import com.sumu.seckill.pojo.SeckillOrder;
import com.sumu.seckill.pojo.User;
import com.sumu.seckill.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author SuMu
 * @since 2021-05-06
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {
    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVo goods);
}
