package com.sumu.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 */
@Service
@Slf4j
public class MQReceiver {

    @RabbitListener(queues = "queue")
    public void receiver(Object msg) {
        log.info("接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_fanout01")
    public void receiver01(Object msg) {
        log.info("接收Queue01消息：" + msg);
    }

    @RabbitListener(queues = "queue_fanout02")
    public void receiver02(Object msg) {
        log.info("接收Queue02消息：" + msg);
    }
}
