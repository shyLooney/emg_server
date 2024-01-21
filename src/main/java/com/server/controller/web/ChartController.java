package com.server.controller.web;

import com.server.chip.Chip;
import com.server.chip.interaction.SignalRecipient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class ChartController {
    Map<String, Chip> chipMap;

    public ChartController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

    @GetMapping("/chart/{name}")
    public String chart(@PathVariable("name") String name) {
        return "chart";
    }

    @PostMapping("/chart/{name}/options")
    public String restartRecipient(@PathVariable("name") String name,
                                 @RequestParam(value = "action") String option) {
        if (option.equals("restart")) {
            System.out.println("was restart");
            chipMap.get(name).getSignalRecipient().restartThread();
        }
        if (option.equals("reset_filter")) {
            System.out.println("was reset filter");
            chipMap.get(name).getSignalRecipient().resetFilter();
        }
        return "chart";
    }
}