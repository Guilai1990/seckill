package com.bruce.seckill.service;

import com.bruce.seckill.domain.OrderInfo;
import com.bruce.seckill.domain.SeckillUser;
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

    @Transactional
    public OrderInfo seckill(SeckillUser seckillUser, GoodsVo goodsVo) {
        //decrease order, place order, update seckill order
        goodsService.reduceStock(goodsVo);
        return orderService.createOrder(seckillUser, goodsVo);
    }
}
