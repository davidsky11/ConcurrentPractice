package com.kvlt.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * JMHExample001
 *
 * @author KVLT
 * @date 2018-11-05.
 */
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JMHExample001 {

    @Benchmark
    public void hello() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1);
    }

    public static void main(String[] args) throws RunnerException {
        Options build = new OptionsBuilder().include(JMHExample001.class.getSimpleName())
                .forks(1)
                .measurementIterations(5)  // 迭代5次
                .warmupIterations(10)  // 热身10次（JIT）
                .build();
        new Runner(build).run();
    }
}
