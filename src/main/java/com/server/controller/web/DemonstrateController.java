package com.server.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("pres")
public class DemonstrateController {

    @GetMapping("/gesture")
    public String gesturePresent(@RequestParam(value = "name") String name) {
        return "demonstrate/gesture_demonstrate";
    }

    @GetMapping("/chart")
    public String chartPresent(@RequestParam(value = "name") String name) {
        return "demonstrate/realtime_signal";
    }

}
