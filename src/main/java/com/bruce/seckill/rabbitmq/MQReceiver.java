package com.bruce.seckill.rabbitmq;

import com.bruce.seckill.domain.SeckillOrder;
import com.bruce.seckill.domain.SeckillUser;
import com.bruce.seckill.redis.RedisService;
import com.bruce.seckill.service.GoodsService;
import com.bruce.seckill.service.OrderService;
import com.bruce.seckill.service.SeckillService;
import com.bruce.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @RabbitListener(queues=MQConfig.SECKILL_QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);
        SeckillMessage sm = RedisService.stringToBean(message, SeckillMessage.class);
        SeckillUser user = sm.getUser();
        long goodsId = sm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getGoodsStock();
        if (stock < 0) {
            return;
        }

        // check if user got the seckill goods
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (seckillOrder != null) {
            return;
        }

        // decrease stock, place order, and generate seckill order
        seckillService.seckill(user, goods);

    }

//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message) {
//        log.info("receive message: " + message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message) {
//        log.info("topic queue1 message: " + message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message) {
//        log.info("topic queue2 message: " + message);
//    }
//
//    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
//    public void receiveHeader(byte[] message) {
//        log.info("header queue message: " + new String(message));
//    }
}
