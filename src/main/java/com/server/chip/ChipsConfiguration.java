package com.server.chip;

import com.server.ConfigLoader;
import com.server.filter.DefaultFilter;
import com.server.filter.MeanFilter;
import com.server.model.DefaultNeuronModel;
import com.server.model.NeuronModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ChipsConfiguration {
    private ConfigLoader config;
    private SimpMessagingTemplate simpMessagingTemplate;
    private DefaultFilter defaultFilter = new DefaultFilter();
    private MeanFilter meanFilter = new MeanFilter();

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
                .setIp("192.168.22.91")
                .setPort(3000);

        chip.setRecipientConfig(new RecipientConfig(config));

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
        return chipMap;
    }

    @Bean
    public DefaultFilter defaultFilter() {
        return defaultFilter;
    }

    @Bean
    public MeanFilter filterMean() {
        return meanFilter;
    }
}
