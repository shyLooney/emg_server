package com.server.controller.api;

import com.server.chip.Chip;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
public class RSocketTestController {

    private Map<String, Chip> chipMap;

    public RSocketTestController(Map<String, Chip> chipMap) {
        this.chipMap = chipMap;
    }

    @MessageMapping("sock/{name}")
    public Flux<List<Float>> getSignalChanel(@DestinationVariable("name") String name) {
        return Flux
                .interval(Duration.ofSeconds(1))
                .map(e -> {
                    Random random = new Random();
                    List<Float> list = new ArrayList<>();
                    for (int i= 0; i < 128; i++) {
                        list.add(random.nextFloat(-0.5f, 0.5f));
                    }
                    return list;
//                    return chipMap.get(name).getDefaultFilter();
                });
    }
}
