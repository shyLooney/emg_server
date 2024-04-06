package com.server.controller.api;

import com.server.chip.Chip;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tensorflow.op.signal.Fft;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

@RestController
@RequestMapping("/api/chart/spectrum")
public class SpectrumRestController {
    private Map<String, Chip> chipMap;

    public SpectrumRestController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

//    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<Map<String, List<Double>>> getSpectrum(@RequestParam(value = "name") String name) {
//
//        if (!chipMap.containsKey(name)) {
//            return null;
//        }
//
//        Map<String, List<Double>> map = new HashMap<>();
//
//        Chip chip = chipMap.get(name);
//
//        double[] signal = new double[chip.getPrediction().getInputSignal().size() + 48];
//        for (int i = 0; i < signal.length; i++) {
//            if (i >= chip.getPrediction().getInputSignal().size())
//                signal[i] = 0;
//            else
//                signal[i] = chip.getPrediction().getInputSignal().get(i);
//        }
//
//
//        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
//        Complex[] complex = fft.transform(signal, TransformType.FORWARD);
//
//
//        double[] x = fftshift(fftfreq(complex.length, 1.0));
//
//        for (int i = 0; i < x.length; i++) {
//            x[i] *= 10;
//        }
//
//        double[] outputSignal = new double[complex.length];
//        for (int i = 0; i < outputSignal.length; i++) {
//            outputSignal[i] = complex[i].abs();
//        }
//
//        double[] y = fftshift(outputSignal);
//
//        for (int i = 0; i < y.length; i++) {
//            y[i] = 10 * Math.log10(y[i]);
//        }
//
//        map.put("labels", DoubleStream.of(x).boxed().toList());
//        map.put("values", DoubleStream.of(y).boxed().toList());
//
//        return Flux.just(map);
//    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, List<Double>>> getSpectrum(@RequestParam(value = "name") String name) {
        if (!chipMap.containsKey(name)) {
            return null;
        }

        Chip chip = chipMap.get(name);

        return chip.getSignalRecipient().getDataFlux()
                .flatMap(list -> {
                    Map<String, List<Double>> map = new HashMap<>();


                    double[] signal = new double[list.size() + 48];
                    for (int i = 0; i < signal.length; i++) {
                        if (i >= chip.getPrediction().getInputSignal().size())
                            signal[i] = 0;
                        else
                            signal[i] = chip.getPrediction().getInputSignal().get(i);
                    }


                    FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
                    Complex[] complex = fft.transform(signal, TransformType.FORWARD);


                    double[] x = fftshift(fftfreq(complex.length, 1.0));

                    for (int i = 0; i < x.length; i++) {
                        x[i] *= 1000;
                    }

                    double[] outputSignal = new double[complex.length];
                    for (int i = 0; i < outputSignal.length; i++) {
                        outputSignal[i] = complex[i].abs();
                    }

                    double[] y = fftshift(outputSignal);

                    for (int i = 0; i < y.length; i++) {
                        y[i] = 10 * Math.log10(y[i]);
                    }

                    map.put("labels", DoubleStream.of(x).boxed().toList());
                    map.put("values", DoubleStream.of(y).boxed().toList());

//                    Flux<Map<String, List<Double>>> mapFlux = Flux.just(map);
                    return Flux.just(map);
                });
    }

    private double[] fftshift(double[] data) {
        double[] temp = new double[data.length];
        int shiftPointNum = data.length / 2;
        for (int i = 0; i < data.length; i++) {
            temp[(shiftPointNum + i) % data.length] = data[i];
        }
        return temp;
    }

    private double[] fftfreq(int n, double d) {
        double[] freqs = new double[n];
        double df = 1.0 / (n * d);
        int Fs = 1000;

        for (int i = 0; i < n / 2; i++) {
            freqs[i] = i * df;
        }

        for (int i = -n / 2; i < 0; i++) {
            freqs[i + n] = i * df;
        }

        return freqs;
    }


}
