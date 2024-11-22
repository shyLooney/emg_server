package com.server.controller.api;

import com.server.chip.ChartInfo;
import com.server.chip.Chip;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "api/chart",
        produces = "application/json")
public class ChartRestController {
    Map<String, Chip> chipMap;

    public ChartRestController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

    @GetMapping
    public Map<String, List<? extends Number>> getChartData(@RequestParam("name") String name,
                                                            @RequestParam(value = "type", defaultValue = "default") String type) {
        Map<String, List<? extends Number>> chartData = new HashMap<>();

        if (!chipMap.containsKey(name)) {
            return null;
        }

        Chip chip = chipMap.get(name);

        if (type.equals("pure")) {
            var values = new ArrayList<>(chip.getPureSignal());
            chartData.put("values", values);
        } else {
            var kalman = new ArrayList<>(chip.getKalmanFilter());
            var def = new ArrayList<>(chip.getDefaultFilter());
            var without = new ArrayList<>(chip.getWithoutFilter());
            chartData.put("values", kalman);
            chartData.put("default", def);
            chartData.put("valuesNoFilter", without);
        }

        return chartData;
    }

    @PostMapping
    public void reconnect(@RequestParam("name") String name) {
        if (!chipMap.containsKey(name)) {
            return;
        }
        chipMap.get(name).restart();
    }
}
