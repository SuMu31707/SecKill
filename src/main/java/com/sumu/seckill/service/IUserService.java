package com.sumu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sumu.seckill.pojo.User;
import com.sumu.seckill.vo.LoginVo;
import com.sumu.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author SuMu
 * @since 2021-05-06
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @return
     */
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
}
