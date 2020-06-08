package com.kvlt.seckill;

import com.lmax.disruptor.EventHandler;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @Desc:
 * @Author: daishengkai
 * @Date: 2020/6/2 18:07
 */
public class SeckillEventConsumer implements EventHandler<SeckillEvent> {

    @Override
    public void onEvent(SeckillEvent seckillEvent, long seq, boolean bool) throws Exception {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(0,2));
            System.out.println(seckillEvent.getSeckillId() + " \t" + seckillEvent.getUserId());
        } catch (Exception e) {

        }

    }

}
