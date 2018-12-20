package com.bruce.seckill.rabbitmq;

import com.bruce.seckill.domain.SeckillUser;
import lombok.Data;

@Data
public class SeckillMessage {

    private SeckillUser user;
    private long goodsId;

}
