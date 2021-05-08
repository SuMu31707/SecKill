package com.sumu.seckill.controller;


import com.sumu.seckill.pojo.User;
import com.sumu.seckill.rabbitmq.MQSender;
import com.sumu.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MQSender mqSender;

    /**
     * 用户信息（测试）
     *
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }

//    /**
//     * 测试发送MQ消息
//     */
//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq() {
//        mqSender.send("helle mq");
//    }
//
//    /**
//     * fanout模式
//     */
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public void fanout() {
//        mqSender.send("hello mq fanout!");
//    }
//
//    /**
//     * direct模式
//     */
//    @RequestMapping("/mq/direct")
//    @ResponseBody
//    public void direct() {
//        mqSender.send01("hello mq direct red!");
//        mqSender.send02("hello mq direct green!");
//    }
//
//    /**
//     * topic模式
//     */
//
//    @RequestMapping("/mq/topic01")
//    @ResponseBody
//    public void topic01() {
//        mqSender.send03("hello queue.su.mu");
//    }
//
//    @RequestMapping("/mq/topic02")
//    @ResponseBody
//    public void topic02() {
//        mqSender.send04("hello hello.queue.su.mu");
//    }
//
//    /**
//     * headers模式
//     */
//    @RequestMapping("/mq/headers01")
//    public void headers01() {
//        mqSender.send05("hello whereAny");
//    }
//
//    @RequestMapping("/mq/headers02")
//    public void headers02() {
//        mqSender.send06("hello whereAll");
//    }
}
