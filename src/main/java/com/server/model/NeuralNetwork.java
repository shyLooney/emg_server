package com.server.model;

import lombok.Data;

import java.io.File;
import java.nio.file.Path;

@Data
public abstract class NeuralNetwork {
    private final String name;
    public NeuralNetwork(Path dir) {
        this.name = dir.toString().substring(dir.toString().lastIndexOf(File.separator) + 1);
    }
    abstract public float[] predict(float[] samples);

}
