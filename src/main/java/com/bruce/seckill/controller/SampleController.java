package com.bruce.seckill.controller;


import com.bruce.seckill.domain.User;
import com.bruce.seckill.rabbitmq.MQSender;
import com.bruce.seckill.redis.RedisService;
import com.bruce.seckill.redis.UserKey;
import com.bruce.seckill.result.CodeMsg;
import com.bruce.seckill.result.Result;
import com.bruce.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello world!";
    }

//    @RequestMapping("/mq/header")
//    @ResponseBody
//    public Result<String> header() {
//        mqSender.sendHeader("hello, this is header pattern");
//        return Result.success("hello, header");
//
//    }
//
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public Result<String> fanout() {
//        mqSender.sendFanout("hello, this is fanout pattern");
//        return Result.success("hello, fanout");
//
//    }
//
//    @RequestMapping("/mq/topic")
//    @ResponseBody
//    public Result<String> topic() {
//        mqSender.sendTopic("hello, this is topic pattern");
//        return Result.success("hello, topic");
//
//    }
//
//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> mq() {
//        mqSender.send("hello, this is rabbitmq");
//        return Result.success("hello, bruce");
//        //return new Result(0, "success", "hello,bruce");
//    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("hello, bruce");
        //return new Result(0, "success", "hello,bruce");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError() {
        return Result.error(CodeMsg.SERVER_ERROR);

    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "Bruce Chen");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> Tx() {
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.getById, ""+1, User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user = new User();
        user.setId(1);
        user.setName("Bruce");
        redisService.set(UserKey.getById, ""+1, user);//UserKey:id1
        return Result.success(true);
    }


}
