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

    @RabbitListener(queues = "queue_direct01")
    public void receiver03(Object msg) {
        log.info("接收direct01消息：" + msg);
    }

    @RabbitListener(queues = "queue_direct02")
    public void receiver04(Object msg) {
        log.info("接收direct02消息：" + msg);
    }

    @RabbitListener(queues = "queue_direct03")
    public void receiver05(Object msg) {
        log.info("接收direct03消息：" + msg);
    }

    @RabbitListener(queues = "queue_topic01")
    public void receiver06(Object msg) {
        log.info("接收topic01消息：" + msg);
    }

    @RabbitListener(queues = "queue_topic02")
    public void receiver07(Object msg) {
        log.info("接收topic02消息：" + msg);
    }
}
