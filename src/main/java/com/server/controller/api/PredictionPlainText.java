package com.server.controller.api;

import com.server.chip.Chip;
import com.server.chip.Prediction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/predict",
        produces = "text/plain")
public class PredictionPlainText {
    private Map<String, Chip> chipMap;

    public PredictionPlainText(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

    @GetMapping("/{name}")
    public String getChartData(@PathVariable("name") String name) {
        if (!chipMap.containsKey(name))
            return "";

        Chip chip = chipMap.get(name);
        Prediction prediction = chip.getPrediction();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prediction.getPredictionTime()).append(" ");
        for (var item : prediction.getOutputNeurons()) {
            stringBuilder.append(item).append(" ");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());

//        System.out.println("READ API");

        return stringBuilder.toString();
    }
}
