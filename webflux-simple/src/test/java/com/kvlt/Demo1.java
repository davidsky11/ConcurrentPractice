package com.kvlt;

import org.junit.Test;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Demo1
 *
 * @author KVLT
 * @date 2019-04-13.
 */
public class Demo1 {

    @Test
    public void testMonoBasic(){
        Mono.fromSupplier(() -> "Hello").subscribe(System.out::println);
        Mono.justOrEmpty(Optional.of("Hello")).subscribe(System.out::println);
        Mono.create(sink -> sink.success("Hello")).subscribe(System.out::println);
    }
}
