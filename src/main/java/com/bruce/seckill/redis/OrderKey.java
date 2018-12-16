package com.bruce.seckill.redis;

public class OrderKey extends KeyPrefixImpl {

    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getSeckillOrderByUidGid = new OrderKey("moug");
}
