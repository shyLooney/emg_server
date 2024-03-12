package com.server.chip;

import com.server.ConfigLoader;
import com.server.model.DefaultNeuronModel;
import com.server.model.NeuronModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ChipsConfiguration {
    private ConfigLoader config;
    SimpMessagingTemplate simpMessagingTemplate;
    public ChipsConfiguration(ConfigLoader config,
                              SimpMessagingTemplate simpMessagingTemplate) {
        this.config = config;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Bean
    public Map<String, Chip> chipList() {
        Map<String, Chip> chipMap = new HashMap<>();
        NeuronModel neuronModel = new DefaultNeuronModel();

        Chip chip = new Chip()
                .setName("dmacmill")
                .setIp("192.168.1.101")
                .setPort(3000)
                .setNeuronModel(neuronModel)
                .setRecipientConfig(new RecipientConfig(config));
        chip.setSimpMessagingTemplate(simpMessagingTemplate);


        chipMap.put(chip.getName(), chip);
        chip.start();
        return chipMap;
    }

}
