package com.server.filter;

import lombok.Data;

import java.nio.file.Path;

@Data
public class MeanFilter extends Filter {
    private float mean = 0;

    public MeanFilter(Path dir) {
        super(dir);
        this.mean = mean;
    }

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
