package com.server.chip;

import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class Prediction {
    private volatile long predictionTime;
    private volatile CopyOnWriteArrayList<Float> outputNeurons;
    private CopyOnWriteArrayList<Float> inputSignal;
    private CopyOnWriteArrayList<Float> pureInputSignal;

    public Prediction(long predictionTime, float[] prediction, List<Float> inputSignal, List<Float> pureInputSignal) {
        this.predictionTime = predictionTime;
        this.inputSignal = new CopyOnWriteArrayList<>(inputSignal);
        this.pureInputSignal = new CopyOnWriteArrayList<>(pureInputSignal);

        outputNeurons = new CopyOnWriteArrayList<>();
        for (var item : prediction)
            outputNeurons.add(item);
    }

    public Prediction() {
        predictionTime = 0;
        outputNeurons = new CopyOnWriteArrayList<>();
        inputSignal = new CopyOnWriteArrayList<>();
    }
}