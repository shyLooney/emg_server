package com.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "recipient")
@Data
@EnableConfigurationProperties
public class ConfigLoader {
    private int graphLen;
    private double sigScale;
    private int gestureSize;
    private int packetSize;
    private float minThreshold;
    private float maxThreshold;
    private int waitingConnectionTimeout;
    private int waitingReadTimeout;
    private int FILTER_SIZE = 127;

    public ConfigLoader() {
        graphLen = 10000;
        sigScale = 0.0001;
        gestureSize = 2000;
        packetSize = 528;
        waitingConnectionTimeout = 10000;
        waitingReadTimeout = 5000;
        minThreshold = 0.01f;
        maxThreshold = 0.5f;
    }
}
