package com.server.filter;

public class MeanFilter implements Filter {
    private float mean = 0;

    @Override
    public double[] calculate(double... sample) {
        double[] filtered = new double[sample.length];

        for (int i = 0; i < sample.length; i++) {
            mean = (float) (mean * 0.99 + 0.01 * sample[i]);
            filtered[i] = sample[i] - mean;
        }

        return filtered;
    }
}
