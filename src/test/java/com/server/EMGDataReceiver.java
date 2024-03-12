package com.server;

import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class EMGDataReceiver {
    public static final String EMG8x_ADDRESS = "192.168.1.101";
    public static final int[] CHANNELS_TO_MONITOR = {2};

    public static final int AD1299_NUM_CH = 8;
    public static final int TRANSPORT_BLOCK_HEADER_SIZE = 16;
    public static final int PKT_COUNT_OFFSET = 2;
    public static final int SAMPLES_PER_TRANSPORT_BLOCK = 64;
    public static final int TRANSPORT_QUE_SIZE = 4;
    public static final int TCP_SERVER_PORT = 3000;
    public static final int SPS = 1000;
    public static final int SAMPLES_TO_COLLECT = SAMPLES_PER_TRANSPORT_BLOCK * 8 * 80;

    public static final int TCP_PACKET_SIZE = ((TRANSPORT_BLOCK_HEADER_SIZE / 4) + (AD1299_NUM_CH + 1) * (SAMPLES_PER_TRANSPORT_BLOCK)) * 4;

    public static void main(String[] args) {
        Socket sock = null;
        try {
            sock = new Socket(EMG8x_ADDRESS, TCP_SERVER_PORT);

            byte[] receivedBuffer = new byte[0];
            int[][] rawSamples = new int[SAMPLES_TO_COLLECT][CHANNELS_TO_MONITOR.length];
            int numSamples = 0;

            while (true) {
                if (receivedBuffer.length >= TCP_PACKET_SIZE * 2) {
                    int startOfBlock = findSyncBytes(receivedBuffer);

                    if (startOfBlock >= 0) {
                        ByteBuffer byteBuffer = ByteBuffer.wrap(receivedBuffer, startOfBlock, TCP_PACKET_SIZE);
                        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                        IntBuffer intBuffer = byteBuffer.asIntBuffer();
                        int[] samples = new int[intBuffer.remaining()];
                        intBuffer.get(samples);

                        receivedBuffer = Arrays.copyOfRange(receivedBuffer, startOfBlock + TCP_PACKET_SIZE, receivedBuffer.length);

                        int chCount = 0;
                        for (int chIdx : CHANNELS_TO_MONITOR) {
                            int offsetToCh = TRANSPORT_BLOCK_HEADER_SIZE / 4 + SAMPLES_PER_TRANSPORT_BLOCK * chIdx;
                            int[] dataSamples = Arrays.copyOfRange(samples, offsetToCh, offsetToCh + SAMPLES_PER_TRANSPORT_BLOCK);

                            double[] blockSamples = Arrays.stream(dataSamples).asDoubleStream().toArray();

//                            System.out.println("Ch#" + chIdx + " Block #" + samples[PKT_COUNT_OFFSET] + " mean: " + getMean(blockSamples) + ", var: " + getVariance(blockSamples) / 1e6 + ", sec: " + (numSamples / SPS));

                            for (int i = 0; i < SAMPLES_PER_TRANSPORT_BLOCK; i++) {
                                rawSamples[numSamples + i][chCount] = dataSamples[i];
                            }

                            chCount++;
                        }

                        numSamples += SAMPLES_PER_TRANSPORT_BLOCK;
                        if (numSamples >= SAMPLES_TO_COLLECT) {
                            break;
                        }
                    }
                } else {
                    byte[] receivedData = new byte[TCP_PACKET_SIZE];
                    int bytesRead = sock.getInputStream().read(receivedData);
                    if (bytesRead == -1) {
                        break;
                    }
                    receivedBuffer = Arrays.copyOf(receivedBuffer, receivedBuffer.length + receivedData.length);
                    System.arraycopy(receivedData, 0, receivedBuffer, receivedBuffer.length - receivedData.length, receivedData.length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sock != null) {
                try {
                    sock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static int findSyncBytes(byte[] buffer) {
        for (int i = 0; i < buffer.length - 4; i++) {
            if (buffer[i] == 'E' && buffer[i + 1] == 'M' && buffer[i + 2] == 'G' && buffer[i + 3] == '8' && buffer[i + 4] == 'x') {
                return i;
            }
        }
        return -1;
    }

    private static double getMean(double[] arr) {
        double sum = 0;
        for (double num : arr) {
            sum += num;
        }
        return sum / arr.length;
    }

    private static double getVariance(double[] arr) {
        double mean = getMean(arr);
        double sum = 0;
        for (double num : arr) {
            sum += Math.pow(num - mean, 2);
        }
        return sum / arr.length;
    }
}