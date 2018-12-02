package com.bruce.seckill.controller;

import com.bruce.seckill.domain.SeckillUser;
import com.bruce.seckill.redis.RedisService;
import com.bruce.seckill.service.GoodsService;
import com.bruce.seckill.service.SeckillUserService;
import com.bruce.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/to_list")
    public String toLogin(Model model,
                          SeckillUser user) {
        model.addAttribute("user", user);
        // query goods list
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        return "goods_list";

    }

    @RequestMapping("/to_detail/{goodsId}")
    public String toDetail(Model model, SeckillUser user, @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user", user);

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);

        //
        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;

        if (now < startAt) {
            //seckill not start yet
            seckillStatus = 0;
            remainSeconds = (int)((startAt - now)/1000);
        } else if (now > endAt){
            //seckill finish already
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            // seckilling
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goods_detail";

    }




}
