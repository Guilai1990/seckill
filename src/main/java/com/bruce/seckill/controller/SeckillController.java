package com.bruce.seckill.controller;

import com.bruce.seckill.domain.SeckillOrder;
import com.bruce.seckill.domain.SeckillUser;
import com.bruce.seckill.rabbitmq.MQSender;
import com.bruce.seckill.rabbitmq.SeckillMessage;
import com.bruce.seckill.redis.GoodsKey;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

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

    @Autowired
    MQSender mqSender;

    private Map<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * system initialization
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null) {
            return;
        }
        for (GoodsVo goods:goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, ""+goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }


    @RequestMapping(value = "/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> toLogin(Model model, SeckillUser user, @RequestParam("goodsId")long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // mark it in cache, reduce data flow to redis service
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        // decrease stock in advance
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, ""+goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        // check if got the sckill good
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (seckillOrder != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }

        // join the queue
        SeckillMessage sm = new SeckillMessage();
        sm.setUser(user);
        sm.setGoodsId(goodsId);
        mqSender.sendSeckillMessage(sm);
        return Result.success(0);//in the queue




//        // check stock
//        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
//        int stock = goodsVo.getStockCount();
//        if (stock <=  0) {
//           return Result.error(CodeMsg.SECKILL_OVER);
//        }
//        // check if user got the seckill goods
//        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
//        if (seckillOrder != null) {
//            return Result.error(CodeMsg.REPEATE_SECKILL);
//
//        }
//        // decrease stock, place order, update seckill order
//        OrderInfo orderInfo = seckillService.seckill(user, goodsVo);
//        return Result.success(orderInfo);

    }

    /**
     * orderId: success
     * -1: stock is not enough
     * 0: in the queue
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, SeckillUser user, @RequestParam("goodsId")long goodsId){
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(result);
    }



}
