package com.bruce.seckill.service;

import com.bruce.seckill.dao.SeckillUserDao;
import com.bruce.seckill.domain.SeckillUser;
import com.bruce.seckill.exception.GlobalException;
import com.bruce.seckill.redis.RedisService;
import com.bruce.seckill.redis.SeckillUserKey;
import com.bruce.seckill.result.CodeMsg;
import com.bruce.seckill.util.MD5Util;
import com.bruce.seckill.util.UUIDUtil;
import com.bruce.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    SeckillUserDao seckillUserDao;

    @Autowired
    RedisService redisService;

    public SeckillUser getById(long id) {
        return seckillUserDao.getById(id);
    }


    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        // check telephone number existence
       SeckillUser user = getById(Long.parseLong(mobile));
       if (user == null) {
           throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
       }
       // verify the password
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calculatedPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calculatedPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //generate cookie
        String token = UUIDUtil.uuid();
        redisService.set(SeckillUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
        //add cookie

        addCookie(response,token, user);
        return true;
    }

    private void addCookie(HttpServletResponse response, String token, SeckillUser seckillUser) {
        redisService.set(SeckillUserKey.token, token, seckillUser);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public SeckillUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        SeckillUser user = redisService.get(SeckillUserKey.token, token, SeckillUser.class);
        // extend the expire timing
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }
}
