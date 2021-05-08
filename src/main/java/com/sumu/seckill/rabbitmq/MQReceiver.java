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
        log.info("接收queue_fanout01消息：" + msg);
    }

    @RabbitListener(queues = "queue_fanout02")
    public void receiver02(Object msg) {
        log.info("接收queue_fanout02消息：" + msg);
    }

    @RabbitListener(queues = "queue_direct01")
    public void receiver03(Object msg) {
        log.info("接收queue_direct01消息：" + msg);
    }

    @RabbitListener(queues = "queue_direct02")
    public void receiver04(Object msg) {
        log.info("接收queue_direct02消息：" + msg);
    }

    @RabbitListener(queues = "queue_direct03")
    public void receiver05(Object msg) {
        log.info("接收queue_direct03消息：" + msg);
    }

    @RabbitListener(queues = "queue_topic01")
    public void receiver06(Object msg) {
        log.info("接收queue_topic01消息：" + msg);
    }

    @RabbitListener(queues = "queue_topic02")
    public void receiver07(Object msg) {
        log.info("接收queue_topic02消息：" + msg);
    }

    @RabbitListener(queues = "queue_header01")
    public void receiver08(Object msg) {
        log.info("接收queue_header01消息：" + msg);
    }

    @RabbitListener(queues = "queue_header02")
    public void receiver09(Object msg) {
        log.info("接收queue_header02消息：" + msg);
    }
}
