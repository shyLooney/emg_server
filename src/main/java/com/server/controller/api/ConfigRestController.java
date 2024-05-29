package com.server.controller.api;

import com.server.ConfigLoader;
import com.server.chip.Chip;
import com.server.chip.RecipientConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigRestController {
    private final Map<String, Chip> chipMap;

    public ConfigRestController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

    @GetMapping
    public RecipientConfig getConfig(@RequestParam(name = "name") String name) {
        if (chipMap.containsKey(name)) {
            return chipMap.get(name).getConfig();
        }
        else
            return null;
    }

    @GetMapping
    @RequestMapping("/chip")
    public Chip getChip(@RequestParam(name = "name") String name) {
        return chipMap.getOrDefault(name, null);
    }
}
