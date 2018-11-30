package com.bruce.seckill.redis;

public class OrderKey extends KeyPrefixImpl {

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
