package com.kvlt.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * JMHExample004
 *
 * @author KVLT
 * @date 2018-11-05.
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class JMHExample004 {

    @State(Scope.Benchmark)  // 多线程共享
    public static class ConcurrentMap {
        Map<String, Long> map = new ConcurrentHashMap<String, Long>();
    }

    @State(Scope.Benchmark)
    public static class SynchronizedMap {
        Map<String, Long> map = Collections.synchronizedMap(new HashMap<String, Long>());
    }

    @Benchmark
    public void testConcurrentMapPerf(ConcurrentMap map) {
        for (int i = 0; i < 100; i++) {
            long nanoTime = System.nanoTime();
            map.map.put(String.valueOf(nanoTime), nanoTime);
        }
    }

    @Benchmark
    public void testSynchronizedMapPerf(SynchronizedMap map) {
        for (int i = 0; i < 100; i++) {
            long nanoTime = System.nanoTime();
            map.map.put(String.valueOf(nanoTime), nanoTime);
        }
    }

    public static void main(String[] args) throws RunnerException {
        final Options options = new OptionsBuilder().include(JMHExample004.class.getSimpleName())
                .forks(1)
                .threads(4)
                .build();
        new Runner(options).run();
    }
}
