package com.server.chip;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class ChartInfo {
    private List<? extends Number> values;
    private Prediction prediction;

    public ChartInfo() {
        values = new ArrayList<>();
    }

}
