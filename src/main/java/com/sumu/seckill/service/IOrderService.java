package com.sumu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sumu.seckill.pojo.Order;
import com.sumu.seckill.pojo.User;
import com.sumu.seckill.vo.GoodsVo;
import com.sumu.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author SuMu
 * @since 2021-05-06
 */
public interface IOrderService extends IService<Order> {

    /**
     * 订单详情信息
     * @param orderId
     * @return
     */
    OrderDetailVo detail(Long orderId);

    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVo goods);

    /**
     * 创建秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    String createPath(User user, Long goodsId);

    /**
     * 校验秒杀地址
     * @param user
     * @param path
     * @return
     */
    Boolean checkPath(User user, Long goodsId, String path);

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    Boolean checkCaptcha(User user, Long goodsId, String captcha);
}
