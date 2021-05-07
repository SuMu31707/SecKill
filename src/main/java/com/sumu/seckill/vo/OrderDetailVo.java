package com.sumu.seckill.vo;

import com.sumu.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单详情Vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {
    private Order order;
    private GoodsVo goodsVo;
}
