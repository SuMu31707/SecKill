package com.sumu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sumu.seckill.exception.GlobalException;
import com.sumu.seckill.mapper.UserMapper;
import com.sumu.seckill.pojo.User;
import com.sumu.seckill.service.IUserService;
import com.sumu.seckill.utils.CookieUtil;
import com.sumu.seckill.utils.MD5Util;
import com.sumu.seckill.utils.UUIDUtil;
import com.sumu.seckill.utils.ValidatorUtil;
import com.sumu.seckill.vo.LoginVo;
import com.sumu.seckill.vo.RespBean;
import com.sumu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author SuMu
 * @since 2021-05-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录
     *
     * @param loginVo
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        /*// 参数校验
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if (!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }*/
        // 根据id（手机号码）查询用户
        User user = userMapper.selectById(mobile);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 判断密码是否正确
        if (!MD5Util.fromPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 生成cookie
        String ticket = UUIDUtil.uuid();
        // 将用户信息存入Redis中
        redisTemplate.opsForValue().set("user:" + ticket, user);
        // 将用户信息存入Session中
//        request.getSession().setAttribute(ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success();
    }

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @return
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)){
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user != null) {
            CookieUtil.setCookie(request,response,"userTicket", userTicket);
        }
        return user;
    }
}
