package com.server;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.SplittableRandom;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@SpringBootTest
public class SignalStreamGenerateTest {
    private DoubleStream stream() {
        SplittableRandom random = new SplittableRandom();

        return DoubleStream.generate(() -> random.nextDouble(-0.5, 0.5));
    }
}
