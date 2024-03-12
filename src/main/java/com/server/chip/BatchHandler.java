package com.server.chip;

import com.server.SignalUtils;
import reactor.core.publisher.Flux;

import java.io.InputStream;

public class BatchHandler {
    private RecipientConfig config;
    private InputStream inputStream;

    public BatchHandler(RecipientConfig config, InputStream inputStream) {
        this.config = config;
        this.inputStream = inputStream;
    }

    public Flux<Double> getValue() {
        int offset = 0;
        byte[] data = new byte[528];
        int value;

        while (offset < config.getPacketSize()) {
            if (offset != 0) {
                System.out.println("lost of part of the packet");
            }
            if (offset == -1) {
                System.out.println("offset = -1");
                System.exit(1);
            }
            offset += inputStream.read(data, offset, config.getPacketSize() - offset);
        }

//                    signalQueue.clear();
        for (int i = 16, j = 0; i < 528; i += 4, j++) {
            value = ((data[i + 3] & 0xFF) << 24) | ((data[i + 2] & 0xFF) << 16)
                    | ((data[i + 1] & 0xFF) << 8) | (data[i] & 0xFF);
        }
        return value;
    }
}
