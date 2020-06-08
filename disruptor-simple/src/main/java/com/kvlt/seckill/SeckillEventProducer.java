package com.kvlt.seckill;

import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.RingBuffer;

/**
 * @Desc:
 * @Author: daishengkai
 * @Date: 2020/6/2 18:09
 */
public class SeckillEventProducer {

    private final static EventTranslatorVararg<SeckillEvent> translator = (seckillEvent, seq, objs) -> {
        seckillEvent.setSeckillId((Long) objs[0]);
        seckillEvent.setUserId((Long) objs[1]);
    };

    private final RingBuffer<SeckillEvent> ringBuffer;

    public SeckillEventProducer(RingBuffer<SeckillEvent> ringBuffer){
        this.ringBuffer = ringBuffer;
    }

    public void seckill(long seckillId, long userId){
        this.ringBuffer.publishEvent(translator, seckillId, userId);
    }

}
