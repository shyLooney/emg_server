package com.server.model;

import lombok.Data;

@Data
public class NeuralNetworkConfig {
    private String feed;
    private String fetch;
    private String[] tags;
}
