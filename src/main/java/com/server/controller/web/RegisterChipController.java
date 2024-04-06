package com.server.controller.web;

import com.server.ConfigLoader;
import com.server.chip.Chip;
import com.server.chip.RecipientConfig;
import com.server.chip.SignalRecipient;
import com.server.filter.DefaultFilter;
import com.server.filter.MeanFilter;
import com.server.model.DefaultNeuronModel;
import com.server.model.NeuronModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    ConfigLoader configLoader;
    SimpMessagingTemplate simpMessagingTemplate;
    private DefaultFilter defaultFilter = new DefaultFilter();
    private MeanFilter meanFilter = new MeanFilter();

    public RegisterChipController(Map<String, Chip> chipMap,
                                  ConfigLoader configLoader,
                                  SimpMessagingTemplate simpMessagingTemplate) {
        this.chipMap = chipMap;
        this.configLoader = configLoader;
        this.simpMessagingTemplate = simpMessagingTemplate;
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

        NeuronModel neuronModel = new DefaultNeuronModel();
        RecipientConfig recipientConfig = new RecipientConfig(configLoader);

//        recipientConfig.setNeuronModel(neuronModel);
//        recipientConfig.setConfig(new RecipientConfig(configLoader));
//        recipientConfig.setSimpMessagingTemplate(simpMessagingTemplate);


        chip.setRecipientConfig(new RecipientConfig(configLoader));

        SignalRecipient signalRecipient = new SignalRecipient();
        signalRecipient.setNeuronModel(neuronModel)
                .setSimpMessagingTemplate(simpMessagingTemplate)
                .setRecipientConfig(chip.getConfig())
                .setNameEndPoint(chip.getName());
        signalRecipient.addFilter(defaultFilter);
        signalRecipient.addFilter(meanFilter);

        chip.setSignalRecipient(signalRecipient);
        chipMap.put(chip.getName(), chip);
        chip.start();


        chip.start();
        chipMap.put(chip.getName(), chip);
        for (var item : chipMap.values())
            System.out.println(item);

        sessionStatus.setComplete();
        return "redirect:/";
    }
}
