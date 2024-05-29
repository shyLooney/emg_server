package com.server.chip;

import com.server.ConfigLoader;
import com.server.filter.DefaultFilter;
import com.server.filter.Filter;
import com.server.filter.MeanFilter;
import com.server.model.DefaultNeuralNetwork;
import com.server.model.NeuralNetwork;
import com.server.storage.StorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
public class ChipsConfiguration {
    private ConfigLoader config;
    private SimpMessagingTemplate simpMessagingTemplate;
//    private MeanFilter meanFilter = new MeanFilter(null);

    public ChipsConfiguration(ConfigLoader config,
                              SimpMessagingTemplate simpMessagingTemplate) {
        this.config = config;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Bean
    public Map<String, Chip> chipList() {
        Map<String, Chip> chipMap = new HashMap<>();
//        NeuralNetwork neuralNetwork = new DefaultNeuralNetwork("model");
//
//        Chip chip = new Chip()
//                .setName("dmacmill")
//                .setIp("192.168.108.91")
//                .setPort(3000);
//
//        chip.setRecipientConfig(new RecipientConfig(config));
//
//        SignalRecipient signalRecipient = new SignalRecipient();
//        signalRecipient.setNeuralNetwork(neuralNetwork)
//                .setSimpMessagingTemplate(simpMessagingTemplate)
//                .setRecipientConfig(chip.getConfig())
//                .setNameEndPoint(chip.getName());
////        signalRecipient.addFilter(defaultFilter);
//        signalRecipient.addFilter(meanFilter);
//
//        chip.setSignalRecipient(signalRecipient);
//        chipMap.put(chip.getName(), chip);
//        chip.start();
        return chipMap;
    }

    @Bean
    public List<Filter> filterList(StorageService storageService) {
        List<Filter> filterList = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(storageService.getLocation().resolve("filters"), 1)
                .filter(path -> !path.equals(storageService.getLocation().resolve("filters")))
                .map(storageService.getLocation()::relativize);){

            stream.forEach(item -> filterList.add(new DefaultFilter(storageService.getLocation().resolve(item))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filterList;
    }

    @Bean
    public List<NeuralNetwork> neuralNetworkList(StorageService storageService) {
        List<NeuralNetwork> neuralNetworkList = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(storageService.getLocation().resolve("models"), 1)
                .filter(path -> !path.equals(storageService.getLocation().resolve("models")))
                .map(storageService.getLocation()::relativize);){

            stream.forEach(item -> neuralNetworkList.add(new DefaultNeuralNetwork(storageService.getLocation().resolve(item))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return neuralNetworkList;
    }
}
