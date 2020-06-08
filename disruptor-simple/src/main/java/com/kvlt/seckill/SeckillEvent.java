package com.kvlt.seckill;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Desc:
 * @Author: daishengkai
 * @Date: 2020/6/2 18:04
 */
public class SeckillEvent implements Serializable {

    private static final long serialVersionUID = -3166662021667262378L;

    private long seckillId;
    private long userId;

    public SeckillEvent() {
        int threadId = ThreadLocalRandom.current().nextInt(10, 100);
        this.seckillId = threadId;
        this.userId = threadId;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
