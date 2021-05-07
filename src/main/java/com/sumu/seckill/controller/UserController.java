package com.sumu.seckill.controller;


import com.sumu.seckill.pojo.User;
import com.sumu.seckill.vo.RespBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author SuMu
 * @since 2021-05-06
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 用户信息（测试）
     * 优化前：
     *      Win：1152.1 QBS
     *      Linux：906.3 QBS
     * 优化后：
     *      Win：
     *      Linux：
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }

}
