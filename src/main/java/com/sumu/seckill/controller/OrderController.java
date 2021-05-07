package com.sumu.seckill.controller;


import com.sumu.seckill.pojo.User;
import com.sumu.seckill.service.IOrderService;
import com.sumu.seckill.vo.OrderDetailVo;
import com.sumu.seckill.vo.RespBean;
import com.sumu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author SuMu
 * @since 2021-05-06
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo orderDetailVo = orderService.detail(orderId);
        return RespBean.success(orderDetailVo);
    }
}
