package com.server.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/settings")
public class ChipSettings {
    @GetMapping
    public String chipSettings() {
        return "chip_settings";
    }
}
