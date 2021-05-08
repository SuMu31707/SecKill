package com.sumu.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息发送者
 */
@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * fanout模式
     * @param msg
     */
    public void send(Object msg) {
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
    }

    /**
     * direct模式
     * @param msg
     */
    public void send01(Object msg) {
        log.info("发送red消息：" + msg);
        rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
    }

    public void send02(Object msg) {
        log.info("发送green消息：" + msg);
        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
    }

    /**
     * topic模式
     */
    public void send03(Object msg) {
        log.info("发送消息："+msg);
        rabbitTemplate.convertAndSend("topicExchange", "queue.su.mu", msg);
    }

    public void send04(Object msg) {
        log.info("发送消息："+msg);
        rabbitTemplate.convertAndSend("topicExchange", "hello.queue.su.mu", msg);
    }

}
