package com.server.controller.web;

import com.server.chip.Chip;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
//@SessionAttributes("chip")
@RequestMapping("/registration")
public class RegisterChipController {
    Map<String, Chip> chipMap;

    public RegisterChipController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }


    @GetMapping
    public String registration() {
        return "registration";
    }


    @PostMapping
    public String processRegister(Chip chip, Errors errors,
                                  SessionStatus sessionStatus) {
        if (errors.hasErrors()) {
            return "/registration";
        }

        chipMap.put(chip.getName(), chip);
        for (var item : chipMap.values())
            System.out.println(item);

        sessionStatus.setComplete();
        return "redirect:/";
    }
}
