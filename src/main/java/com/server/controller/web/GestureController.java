package com.server.controller.web;

import com.server.chip.Chip;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class GestureController {
    private Map<String, Chip> chipMap;

    public GestureController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

    @GetMapping("/gesture")
    public String gestureChart(@RequestParam(value = "name") String name) {
        if (!chipMap.containsKey(name)) {
            return "redirect:home";
        }
        Chip chip = chipMap.get(name);


        return "gesture_chart";
    }
}