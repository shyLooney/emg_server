package com.server.controller.web;

import com.server.chip.Chip;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class ChartController {
    private Map<String, Chip> chipMap;

    public ChartController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

    @GetMapping("/chart")
    public String chart(@RequestParam(value = "name") String name,
                        @RequestParam(value = "type", defaultValue = "default") String type) {
        if (!chipMap.containsKey(name)) {
            return "redirect:home";
        }
        Chip chip = chipMap.get(name);

        System.out.println(type);
        switch (type) {
            case "gesture":
                return "gesture_chart";
            case "pure":
                return "pure_chart";
            case "list":
                return "list_chart";
            default:
                return "filter_chart";
        }

    }
}