package com.server.controller.web;

import com.server.chip.Chip;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

@Controller
public class HomeController {
    Map<String, Chip> chipMap;

    public HomeController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }


    @ModelAttribute(name = "chipMap")
    public Map<String, Chip> chipMap() {
        return chipMap;
    }

    @GetMapping({"/home", "/"})
    public String home() {
        return "home";
    }

}
