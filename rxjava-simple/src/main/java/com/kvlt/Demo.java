package com.kvlt;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;

/**
 * Demo
 *
 * @author KVLT
 * @date 2019-04-13.
 */
public class Demo {

    @Test
    public void ttt1() {
        Flowable.just("Hello world").subscribe(System.out::println);
    }

    @Test
    public void ttt2() {
        Observable.create(emitter -> {
            while (!emitter.isDisposed()) {
                long time = System.currentTimeMillis();
                emitter.onNext(time);
                if (time % 2 != 0) {
                    emitter.onError(new IllegalStateException("Odd millisecond!"));
                    break;
                }
            }
        })
        .subscribe(System.out::println, Throwable::printStackTrace);
    }

    @Test
    public void ttt3() {
        Flowable.range(1, 10)
                .observeOn(Schedulers.computation())
                .map(v -> v * v)
                .blockingSubscribe(System.out::println);
    }

}
