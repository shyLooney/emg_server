package com.server.controller.api;

import com.server.chip.Chip;
import com.server.chip.interaction.SignalRecipient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/api/predict",
        produces = "text/plain")
public class PredictionRestController {
    Map<String, Chip> chipMap;

    public PredictionRestController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

    @GetMapping("/{name}")
    public String getChartData(@PathVariable("name") String name) {
        if (!chipMap.containsKey(name))
            return "";

        Chip chip = chipMap.get(name);
        SignalRecipient.Prediction prediction = chip.getSignalRecipient().getPrediction();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prediction.getPredictionTime()).append(" ");
        for (var item : prediction.getPrediction()) {
            stringBuilder.append(item).append(" ");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());

        return stringBuilder.toString();
    }

}
