package com.bruce.seckill.controller;

import com.bruce.seckill.domain.OrderInfo;
import com.bruce.seckill.domain.SeckillOrder;
import com.bruce.seckill.domain.SeckillUser;
import com.bruce.seckill.redis.RedisService;
import com.bruce.seckill.result.CodeMsg;
import com.bruce.seckill.result.Result;
import com.bruce.seckill.service.GoodsService;
import com.bruce.seckill.service.OrderService;
import com.bruce.seckill.service.SeckillService;
import com.bruce.seckill.service.SeckillUserService;
import com.bruce.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    private static Logger log = LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @RequestMapping(value = "/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> toLogin(Model model, SeckillUser user, @RequestParam("goodsId")long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // check stock
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if (stock <=  0) {
           return Result.error(CodeMsg.SECKILL_OVER);
        }
        // check if user got the seckill goods
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (seckillOrder != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);

        }
        // decrease stock, place order, update seckill order
        OrderInfo orderInfo = seckillService.seckill(user, goodsVo);
        return Result.success(orderInfo);
    }






}
