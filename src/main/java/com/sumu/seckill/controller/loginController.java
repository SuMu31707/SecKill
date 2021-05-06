package com.sumu.seckill.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器
 */

@RestController
@RequestMapping("/login")
@Slf4j
public class loginController {

    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

}
