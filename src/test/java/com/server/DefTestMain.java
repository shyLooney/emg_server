package com.server;

import com.github.psambit9791.jdsp.filter.FIRWin1;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.*;

public class DefTestMain {
    @Data
    @AllArgsConstructor
    static class Node {
        String name;
        int age;

//        @Override
//        public String toString() {
//            return "Node{" +
//                    "name='" + name + '\'' +
//                    ", age=" + age +
//                    '}';
//        }
    }
    public static double[] fftfreq2(int n, double d) {
        double[] freqs = new double[n];
        int half_n = n / 2;

        if (n % 2 == 0) {
            for (int i = 0; i < half_n; i++) {
                freqs[i] = i / (d * n);
                freqs[i + half_n] = (-half_n + i) / (d * n);
            }
        } else {
            for (int i = 0; i < half_n; i++) {
                freqs[i] = i / (d * n);
                freqs[i + half_n + 1] = (-half_n + i) / (d * n);
            }
            freqs[half_n] = 0.0;
        }

        return freqs;
    }

    // Метод для сдвига частот в результате преобразования Фурье
//    public static double[] fftshift(double[] arr) {
//        int n = arr.length;
//        int mid = n / 2;
//        double[] shiftedArr = new double[n];
//
//        System.arraycopy(arr, mid, shiftedArr, 0, mid);
//        System.arraycopy(arr, 0, shiftedArr, mid, mid);
//
//        return shiftedArr;
//    }

    public static double[] fftshift(double[] data) {
        double[] temp = new double[data.length];
        int shiftPointNum = data.length / 2;
        for (int i = 0; i < data.length; i++) {
            temp[(shiftPointNum + i) % data.length] = data[i];
        }
        return temp;
    }

    public static double[] fftfreq(int n, double d) {
        double[] freqs = new double[n];
        double df = 1.0 / (n * d);

        for (int i = 0; i < n/2; i++) {
            freqs[i] = i * df;
        }

        for (int i = -n/2; i < 0; i++) {
            freqs[i + n] = i * df;
        }

        return freqs;
    }

    public static void main(String[] args) {
        int Fs = 1000;

//        double[] signal = new double[]{1, 2, 3, 4};
//        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
//        Complex[] complex = fft.transform(signal, TransformType.FORWARD);
//
//        System.out.println(Arrays.toString(complex));
//        System.out.println(Arrays.toString(fftshift(signal)));
//        System.out.println(Arrays.toString(fftfreq(4, 1.0)));
//
//        double[] data = {1, 2, 3, 4};
//
//        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
//
//        Complex[] fftResult = transformer.transform(data, TransformType.FORWARD);
//        for (Complex c : fftResult) {
//            System.out.println(c);
//        }
//
//        int n = data.length;
//        double[] freq = new double[n];
//        for (int i = 0; i < n; i++) {
//            freq[i] = i / (double) n;
//        }
//        System.out.println(Arrays.toString(freq));
//
//        Complex[] shiftedResult = transformer.transform(data, TransformType.FORWARD);
//        for (Complex c : shiftedResult) {
//            System.out.println(c);
//        }

        String subnet = "192.168.14";

        for (int i = 1; i <= 255; i++) {
            String host = subnet + "." + i;
            try {
                InetAddress address = InetAddress.getByName(host);
                if (address.isReachable(100)) {
                    System.out.println("Host " + host + " is reachable");
                    System.out.println("IP Address: " + address.getHostAddress());
                }
                else
                    System.out.println(address.getHostAddress() + " - ");
            } catch (IOException e) {
                // Handle exception
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testReact() {
        Flux<String> flux = Flux.just("A", "B", "C", "D");

        flux.subscribe(f -> System.out.println("word: " + f));

        StepVerifier.create(flux)
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectNext("D")
                .verifyComplete();
    }

    @Test
    public void testInterval() {
        Flux<Long> flux = Flux.interval(Duration.ofSeconds(5)).take(5);

        flux.subscribe(f -> System.out.println("number: " + f));
        flux.subscribe(f -> System.out.println("number: " + f));

        StepVerifier.create(flux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .verifyComplete();
    }

    @Test
    public void testMerge() {
        Flux<Integer> flux = Flux.range(0, 5)
                .delayElements(Duration.ofMillis(1000));

        Flux<Integer> flux2 = Flux.range(0, 5)
                .delayElements(Duration.ofMillis(1000))
                .delaySubscription(Duration.ofMillis(250));

        Flux<Integer> merge = flux.mergeWith(flux2);

        merge.subscribe(System.out::println);

        StepVerifier.create(merge)
                .expectNext(0,0)
                .expectNext(1,1)
                .expectNext(2,2)
                .expectNext(3,3)
                .expectNext(4,4)
                .verifyComplete();
    }

    @Test
    public void testZip() {
        Flux<Integer> range = Flux.range(0, 5);

        Flux<String> words = Flux.just("aboba", "sema", "beda", "yabeda", "tibeda");

        Flux<String> zip = Flux.zip(range, words, (r, w) -> w + " " + r);

        zip.subscribe(System.out::println);

        StepVerifier.create(zip)
                .expectNext("aboba " + 0)
                .expectNext("sema " + 1)
                .expectNext("beda " + 2)
                .expectNext("yabeda " + 3)
                .expectNext("tibeda " + 4)
                .verifyComplete();
    }

    @Test
    public void trySendGesture() {
        Flux<ArrayList<Float>> listMono = Flux.interval(Duration.ofSeconds(1))
                .map((a) -> generateArray());

        listMono.subscribe(System.out::println);

        while (true);

//        StepVerifier.create(listMono)
//                .expectNextCount(4)
//                .verifyComplete();
    }

    private ArrayList<Float> generateArray() {
        Random random = new Random();
        ArrayList<Float> list = new ArrayList<>();
        for (int i = 0; i < random.nextInt(5, 10); i++) {
            list.add((float)i);
        }
        return list;
    }

    @Test
    public void test() {
    }

}
