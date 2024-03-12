package com.server.controller.web;

import com.server.chip.Chip;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("d3js")
public class D3JSTest {
    private Map<String, Chip> chipMap;

    public D3JSTest(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

    @GetMapping
    public String getD3JS(@RequestParam(value = "name") String name) {

        return "d3js_test";
    }
}
