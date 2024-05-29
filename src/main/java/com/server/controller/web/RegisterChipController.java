package com.server.controller.web;

import com.server.ConfigLoader;
import com.server.chip.Chip;
import com.server.chip.RecipientConfig;
import com.server.chip.SignalRecipient;
import com.server.filter.DefaultFilter;
import com.server.filter.Filter;
import com.server.filter.MeanFilter;
import com.server.model.DefaultNeuralNetwork;
import com.server.model.NeuralNetwork;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Controller
//@SessionAttributes("chip")
@RequestMapping("/registration")
@AllArgsConstructor
public class RegisterChipController {
    private final Map<String, Chip> chipMap;
    private final ConfigLoader configLoader;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final List<Filter> filterList;
    private final List<NeuralNetwork> neuralNetworkList;

    @GetMapping
    public String registration() {
        return "registration";
    }


    @PostMapping
    public String processRegister(Chip chip,
                                  String model,
                                  String filter,
                                  Errors errors,
                                  SessionStatus sessionStatus) {
        if (errors.hasErrors()) {
            return "/registration";
        }

        if (chipMap.containsKey(chip.getName())) {
            Chip chipTemp = chipMap.get(chip.getName());


        }
        else {

            NeuralNetwork neuralNetwork = null;
            RecipientConfig recipientConfig = new RecipientConfig(configLoader);


            chip.setRecipientConfig(new RecipientConfig(configLoader));

            SignalRecipient signalRecipient = new SignalRecipient();
            signalRecipient.setNeuralNetwork(findNeuralNetwork(model))
                    .setSimpMessagingTemplate(simpMessagingTemplate)
                    .setRecipientConfig(chip.getConfig())
                    .setNameEndPoint(chip.getName());

            signalRecipient.addFilter(findFilter(filter));
            signalRecipient.addFilter(new MeanFilter(Path.of("/default.txt")));

            chip.setSignalRecipient(signalRecipient);
            chipMap.put(chip.getName(), chip);
            chip.start();


            chip.start();
            chipMap.put(chip.getName(), chip);
            for (var item : chipMap.values())
                System.out.println(item);
        }

        sessionStatus.setComplete();
        return "redirect:/";
    }

    private Filter findFilter(String filter) {
        return filterList
                .stream()
                .filter(item -> item.getName().equals(filter))
                .findFirst().get();
    }

    private NeuralNetwork findNeuralNetwork(String model) {
        return neuralNetworkList
                .stream()
                .filter(item -> item.getName().equals(model))
                .findFirst().get();
    }
}
