package com.bruce.seckill.controller;

import com.bruce.seckill.domain.SeckillUser;
import com.bruce.seckill.redis.GoodsKey;
import com.bruce.seckill.redis.RedisService;
import com.bruce.seckill.result.Result;
import com.bruce.seckill.service.GoodsService;
import com.bruce.seckill.service.SeckillUserService;
import com.bruce.seckill.vo.GoodsDetailVo;
import com.bruce.seckill.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toLogin(HttpServletRequest request,
                          HttpServletResponse response,
                          Model model,
                          SeckillUser user) {
        model.addAttribute("user", user);
      // get data from cache
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);

        SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        // manual rendering
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetail(HttpServletRequest request,
                                          HttpServletResponse response,
                                          Model model,
                                          SeckillUser user,
                                          @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user", user);



        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);


        // manual rendering

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

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goodsVo);
        goodsDetailVo.setSeckillUser(user);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setSeckillStatus(seckillStatus);
        return Result.success(goodsDetailVo);
    }


    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    @ResponseBody
    public String toDetail2(HttpServletRequest request,
                           HttpServletResponse response,
                           Model model,
                           SeckillUser user,
                           @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user", user);

        // get cache data
        String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);

        // manual rendering



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
        // return "goods_detail";

        SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        // manual rendering
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
        }
        return html;
    }


}
