package com.server.controller.api;

import com.server.GestureSaver;
import com.server.chip.ChartInfo;
import com.server.chip.Chip;
import com.server.chip.Prediction;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/gesture",
        produces = "application/json")
public class GestureRestController {
    private Map<String, Chip> chipMap;

    public GestureRestController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

    @GetMapping
    public Prediction getChartData(@RequestParam("name") String name,
                                   @RequestParam(value = "type", defaultValue = "default") String type) {

        if (!chipMap.containsKey(name)) {
            return null;
        }

        Chip chip = chipMap.get(name);

        return chip.getPrediction();
    }

    @PostMapping
    public void saveGesture(@RequestBody GestureSaver gesture) {
        System.out.println(gesture);
        gesture.save();
    }
}
