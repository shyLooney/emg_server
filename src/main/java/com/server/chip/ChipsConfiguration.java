package com.server.chip;

import com.server.model.DefaultModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ChipsConfiguration {

    @Bean
    public Map<String, Chip> chipList() {
        Map<String, Chip> chipMap = new HashMap<>();
        chipMap.put("dmacmill", new Chip("dmacmill", "192.168.1.101", 3000, new DefaultModel()));
        return chipMap;
    }

}
