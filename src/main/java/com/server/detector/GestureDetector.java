package com.server.detector;

import java.util.List;

public interface GestureDetector {
    boolean isGesture(List<Float> samples);
}
