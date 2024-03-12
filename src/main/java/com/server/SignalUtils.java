package com.server;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

public class SignalUtils {

    static public Map<String, List<Double>> getSpectrum(List<Float> inputSignal) {
        Map<String, List<Double>> map = new HashMap<>();


        double[] signal = new double[inputSignal.size() + 48];
        for (int i = 0; i < signal.length; i++) {
            if (i >= inputSignal.size())
                signal[i] = 0;
            else
                signal[i] = inputSignal.get(i);
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

        return map;
    }

    static private double[] fftshift(double[] data) {
        double[] temp = new double[data.length];
        int shiftPointNum = data.length / 2;
        for (int i = 0; i < data.length; i++) {
            temp[(shiftPointNum + i) % data.length] = data[i];
        }
        return temp;
    }

    static private double[] fftfreq(int n, double d) {
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
