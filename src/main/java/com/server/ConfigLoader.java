package com.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "recipient")
@Data
public class ConfigLoader {
    private int graphLen;
    private double sigScale;
    private int gestureSize;
    private int packetSize;
    private float threshold;
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
        threshold = 0.01f;
    }
}
