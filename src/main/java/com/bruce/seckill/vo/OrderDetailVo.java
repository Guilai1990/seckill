package com.bruce.seckill.vo;

import com.bruce.seckill.domain.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVo {

    private GoodsVo goods;
    private OrderInfo order;

}
