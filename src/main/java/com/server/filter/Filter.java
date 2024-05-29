package com.server.filter;

import lombok.Data;

import java.io.File;
import java.nio.file.Path;

@Data
public abstract class Filter {
    private final String name;
    public Filter(Path dir) {
        System.out.println(dir);
        this.name = dir.toString().substring(dir.toString().lastIndexOf(File.separator) + 1, dir.toString().indexOf('.'));
    }
    public abstract double[] calculate(double... samples);
}
