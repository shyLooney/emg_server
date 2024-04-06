package com.server.detector;

import com.server.chip.RecipientConfig;

import java.util.List;


public class SimpleMiddlePeakDetector implements GestureDetector {
    private int maxIndex = 0;
    private float maxValue = 0;
    private RecipientConfig config;


    public SimpleMiddlePeakDetector(RecipientConfig config) {
        this.config = config;
    }

    @Override
    public boolean isGesture(List<Float> samples) {
        float lastElement = samples.get(samples.size() - 1);

        if (maxValue < lastElement) {
            maxValue = lastElement;
            maxIndex = 0;
        } else
            maxIndex++;
////                        FindPeak fp = new FindPeak();

        if (maxIndex == 1000) {
            if (samples.size() == config.getGestureSize()
                    && maxValue > config.getThreshold()) {
//                    && maxValue < 0.4) {
                maxValue = 0;
                maxIndex = 0;
                return true;
            }
            maxIndex = 0;
            maxValue = 0;
        }

        return false;
    }
}
