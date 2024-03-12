package com.server.chip;

import com.server.ConfigLoader;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
public class RecipientConfig {
    private int graphLen;
    private double sigScale;
    private int gestureSize;
    private int packetSize;
    private float threshold;
    private int waitingConnectionTimeout;
    private int waitingReadTimeout;
    private int FILTER_SIZE = 127;

    public RecipientConfig(ConfigLoader config) {
        graphLen = config.getGraphLen();
        sigScale = config.getSigScale();
        gestureSize = config.getGestureSize();
        packetSize = config.getPacketSize();
        threshold = config.getThreshold();
        waitingConnectionTimeout = config.getWaitingConnectionTimeout();
        waitingReadTimeout = config.getWaitingReadTimeout();
    }
}
