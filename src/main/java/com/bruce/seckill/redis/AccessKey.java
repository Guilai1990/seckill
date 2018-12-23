package com.bruce.seckill.redis;

public class AccessKey extends BasePrefix {

    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey WithExpire(int expireSeconds) {
        return new AccessKey(expireSeconds, "access");
    }

    public static AccessKey access = new AccessKey(5, "access");
}
