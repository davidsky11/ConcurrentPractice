package com.kvlt.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * JMHExample002
 * 借助JMH分析 String，StringBuilder，StringBuffer 各自性能
 * @author KVLT
 * @date 2018-11-05.
 */
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class JMHExample002 {

    /**
     * 关闭JIT优化
     *  1、java.lang.Compiler.disable();
     *  2、@CompilerControl(CompilerControl.Mode.EXCLUDE)
     */

//    static {
//        java.lang.Compiler.disable();  // 关闭JIT优化
//    }

    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    @Benchmark
    public String appendString() {
        String s = "";
        for (int i = 0; i < 100; i++) {
            s = s + i;
        }
        return s;
    }

    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    @Benchmark
    public StringBuffer appendStringBuffer() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 100; i++) {
            buffer.append(i);
        }
        return buffer;
    }

    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    @Benchmark
    public StringBuilder appendStringBuilder() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            builder.append(i);
        }
        return builder;
    }

    public static void main(String[] args) throws RunnerException {
        final Options options = new OptionsBuilder().include(JMHExample002.class.getSimpleName())
                .forks(1)
                .measurementIterations(10)
                .warmupIterations(10)
                .build();
        new Runner(options).run();
    }
}
