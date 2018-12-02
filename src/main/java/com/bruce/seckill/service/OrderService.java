package com.bruce.seckill.service;

import com.bruce.seckill.dao.OrderDao;
import com.bruce.seckill.domain.OrderInfo;
import com.bruce.seckill.domain.SeckillOrder;
import com.bruce.seckill.domain.SeckillUser;
import com.bruce.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    public SeckillOrder getSeckillOrderByUserIdGoodsId(Long userId, long goodsId) {

        return orderDao.getSeckillOrderByUserIdGoodsId(userId, goodsId);
    }

    @Transactional
    public OrderInfo createOrder(SeckillUser seckillUser, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(seckillUser.getId());
        long orderId = orderDao.insert(orderInfo);
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(orderId);
        seckillOrder.setUserId(seckillUser.getId());
        orderDao.insertSeckillOrder(seckillOrder);
        return orderInfo;
    }
}
