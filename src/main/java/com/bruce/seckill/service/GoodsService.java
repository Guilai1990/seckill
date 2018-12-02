package com.bruce.seckill.service;

import com.bruce.seckill.dao.GoodsDao;
import com.bruce.seckill.domain.SeckillGoods;
import com.bruce.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId)
;    }

    public void reduceStock(GoodsVo goodsVo) {
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goodsVo.getId());
        goodsDao.reduceStock(seckillGoods);
    }
}
