package com.server.controller.web;

import com.server.chip.Chip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class SpectrumController {
    private Map<String, Chip> chipMap;
//    private final Flux<List<Float>> dataFlux;
//
//    private FluxSink<List<Float>> dataSink;
    SimpMessagingTemplate simpMessagingTemplate;

    public SpectrumController(Map<String, Chip> chipMap, SimpMessagingTemplate simpMessagingTemplate) {
//        this.dataFlux = Flux.create(emitter -> this.dataSink = emitter, FluxSink.OverflowStrategy.BUFFER);
        this.chipMap = chipMap;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @GetMapping("/spectrum")
    public String gestureChart(@RequestParam(value = "name") String name) {
        if (!chipMap.containsKey(name)) {
            return "redirect:home";
        }
        Chip chip = chipMap.get(name);

        System.out.println(simpMessagingTemplate.getDefaultDestination());
        System.out.println(simpMessagingTemplate.getUserDestinationPrefix());
        System.out.println(simpMessagingTemplate.getHeaderInitializer());
        System.out.println(simpMessagingTemplate.getMessageChannel());
        System.out.println(simpMessagingTemplate.getHeaderInitializer());
        System.out.println(simpMessagingTemplate.getMessageConverter());

        for (int i = 0; i < 10; i++) {
            simpMessagingTemplate.convertAndSend("/topic/portfolio?name=dmacmill", "a");
        }

        return "spectrum_chart";
    }

    @MessageMapping("/send")
    @SendTo("/topic/portfolio")
    public String sendFloatList() {
        List<Float> floatList = new ArrayList<>();
        floatList.add(1.1f);
        floatList.add(2.2f);
        floatList.add(3.3f);

        for (int i = 0; i < 10; i++) {
            simpMessagingTemplate.convertAndSend("/topic/portfolio?name=aboba", floatList);
        }

        return "Hello world!";
    }

    @Scheduled(fixedRate = 1)
    @MessageMapping
    public void sendMessage(SimpMessagingTemplate simpMessagingTemplate) {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        simpMessagingTemplate.convertAndSend("/topic/portfolio",
                "Hello world!");
//        return "Hello world!";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
