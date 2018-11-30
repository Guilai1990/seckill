package com.bruce.seckill.redis;

public abstract class KeyPrefixImpl implements KeyPrefix {

    private int expireSeconds;

    private String prefix;

    public KeyPrefixImpl(String prefix) {//default value 0 means never expire
        this(0, prefix);
    }

    @Override
    public int expireSeconds() {//default value 0 means never expire
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();

        return className + ":" + prefix;
    }

    public KeyPrefixImpl(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }
}
