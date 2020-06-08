package com.kvlt.seckill;

import com.lmax.disruptor.EventFactory;

/**
 * @Desc:
 * @Author: daishengkai
 * @Date: 2020/6/2 18:06
 */
public class SeckillEventFactory implements EventFactory<SeckillEvent> {

    @Override
    public SeckillEvent newInstance() {
        return new SeckillEvent();
    }

}
