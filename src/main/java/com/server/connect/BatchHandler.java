package com.server.connect;

import java.io.IOException;
import java.util.List;

public interface BatchHandler {
    List<Integer> getValue() throws IOException;

}
