package com.sumu.seckill.rabbitmq;

import com.sumu.seckill.pojo.SeckillMessage;
import com.sumu.seckill.pojo.SeckillOrder;
import com.sumu.seckill.pojo.User;
import com.sumu.seckill.service.IGoodsService;
import com.sumu.seckill.service.IOrderService;
import com.sumu.seckill.service.ISeckillOrderService;
import com.sumu.seckill.utils.JsonUtil;
import com.sumu.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 */
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;
    /**
     * 接收秒杀消息，进行下单操作
     *
     * @param msg
     */
    @RabbitListener(queues = "seckillQueue")
    public void seckillReceiver(String msg) {
        log.info("接收消息：" + msg);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
        User user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodsId();
        // 根据goodsId获取秒杀商品信息
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        // 判断库存信息
        if (goodsVo.getStockCount() < 1) {
            return;
        }

        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        // 判断是否重复抢购
        if (seckillOrder != null) {
            return;
        }
        // 下单操作
        orderService.seckill(user, goodsVo);
    }


//    @RabbitListener(queues = "queue")
//    public void receiver(Object msg) {
//        log.info("接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receiver01(Object msg) {
//        log.info("接收queue_fanout01消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receiver02(Object msg) {
//        log.info("接收queue_fanout02消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receiver03(Object msg) {
//        log.info("接收queue_direct01消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receiver04(Object msg) {
//        log.info("接收queue_direct02消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct03")
//    public void receiver05(Object msg) {
//        log.info("接收queue_direct03消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic01")
//    public void receiver06(Object msg) {
//        log.info("接收queue_topic01消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void receiver07(Object msg) {
//        log.info("接收queue_topic02消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_header01")
//    public void receiver08(Object msg) {
//        log.info("接收queue_header01消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_header02")
//    public void receiver09(Object msg) {
//        log.info("接收queue_header02消息：" + msg);
//    }
}
