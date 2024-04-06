package com.server.connect;

import com.server.chip.RecipientConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TcpUnoBatchHandler implements BatchHandler{
    private final RecipientConfig config;
    private final InputStream inputStream;

    public TcpUnoBatchHandler(RecipientConfig config, InputStream inputStream) {
        this.config = config;
        this.inputStream = inputStream;
    }

    @Override
    public List<Integer> getValue() throws IOException {
        int offset = 0;
        byte[] data = new byte[528];
        int value;
        List<Integer> signalQueue = new ArrayList<>();

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

        for (int i = 16, j = 0; i < 528; i += 4, j++) {
            value = ((data[i + 3] & 0xFF) << 24) | ((data[i + 2] & 0xFF) << 16)
                    | ((data[i + 1] & 0xFF) << 8) | (data[i] & 0xFF);
            signalQueue.add(value);
        }

        return signalQueue;
    }
}
