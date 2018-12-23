package com.bruce.seckill.access;

import com.bruce.seckill.domain.SeckillUser;


public class UserContext {

    private static ThreadLocal<SeckillUser> userholder = new ThreadLocal<SeckillUser>();

    public static void setUser(SeckillUser user) {
        userholder.set(user);
    }

    public static SeckillUser getUser() {
        return userholder.get();
    }
}
