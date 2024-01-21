package com.server.controller.api;

import com.server.chip.Chip;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/chart",
        produces = "application/json")
public class ChartRestController {
    Map<String, Chip> chipMap;

    public ChartRestController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

    @GetMapping("/{name}")
    public Map<String, List<Object>> getChartData(@PathVariable("name") String name) {

        if (!chipMap.containsKey(name)) {
            return new HashMap<>();
        }

        List<Object> labels = new ArrayList<>(chipMap.get(name).getSignalRecipient().getTime());
        List<Object> values = new ArrayList<>(chipMap.get(name).getSignalRecipient().getSignalBuffer());

        Map<String, List<Object>> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("values", values);


        return chartData;
    }
}
