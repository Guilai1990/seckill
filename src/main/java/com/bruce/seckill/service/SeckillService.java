package com.bruce.seckill.service;

import com.bruce.seckill.domain.OrderInfo;
import com.bruce.seckill.domain.SeckillOrder;
import com.bruce.seckill.domain.SeckillUser;
import com.bruce.seckill.redis.RedisService;
import com.bruce.seckill.redis.SeckillKey;
import com.bruce.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo seckill(SeckillUser seckillUser, GoodsVo goodsVo) {
        // decrease order, place order, update seckill order
        boolean success = goodsService.reduceStock(goodsVo);
        if (success) {
            // create order
            return orderService.createOrder(seckillUser, goodsVo);
        } else {
            setGoodsOver(goodsVo.getId());
            return null;
        }

    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver,""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver,""+goodsId);
    }

    public long getSeckillResult(Long userId, long goodsId) {
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        if (seckillOrder != null) { // seckill done with success
            return seckillOrder.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }

    }
}
