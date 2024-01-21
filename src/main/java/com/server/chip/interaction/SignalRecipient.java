package com.server.chip.interaction;

import com.server.Filter;
import com.server.model.Model;
import lombok.Data;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class SignalRecipient {
    private String ip;
    private int port;
    private CopyOnWriteArrayList<Float> signalBuffer;
    private CopyOnWriteArrayList<Long> time;
    private Model model;
    private Prediction prediction;
    private Filter filter;
    private Thread thread;

    private static final int LEN = 10000;
    private static final int FILTER_SIZE = 127;
    private static double sigScale = 0.0001;
    private static final int PACKET_SIZE = 528;
    private static float m_mean = 0;
    private static long counter = 0;
    private static float threshold = 0.06f;

    public SignalRecipient(Model model) {
        this.model = model;
        prediction = new Prediction();
        signalBuffer = new CopyOnWriteArrayList<>();
        time = new CopyOnWriteArrayList<>();
        filter = new Filter();

        thread = new Thread(this::getLoop);
        thread.start();
    }

    @ModelAttribute
    public List<Float> signal() {
        return signalBuffer;
    }

    public void restartThread() {
        if (!thread.isInterrupted()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            thread = new Thread(this::getLoop);
            thread.start();
        }
    }

    public void resetFilter() {
        filter = new Filter();
    }

    private void getLoop() {
        try {
            Socket socket = new Socket(ip, port);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            socket.setSoTimeout(5000);

            byte[] data = new byte[528];

            var samples = new LinkedList<Float>();
            int maxIndex = 0;
            float maxVal = 0;

            while (!Thread.currentThread().isInterrupted()) {
                int offset = 0;
                while (offset < PACKET_SIZE) {
                    if (offset != 0) {
                        System.out.println("lost of part of the packet");
                    }
                    if (offset == -1) {
                        System.out.println("offset = -1");
                        System.exit(1);
                    }
                    offset += inputStream.read(data, offset, PACKET_SIZE - offset);
                }

                for (int i = 16, j = 0; i < 528; i += 4, j++) {
                    int value = ((data[i + 3] & 0xFF) << 24) | ((data[i + 2] & 0xFF) << 16)
                            | ((data[i + 1] & 0xFF) << 8) | (data[i] & 0xFF);
                    float temp = filter.calculate(((double) value) * 0.00001);
                    m_mean = (float) (m_mean * 0.99 + 0.01 * temp);

                    float valSig = temp - m_mean;
                    signalBuffer.add(valSig);
                    samples.add(valSig);

                    if (maxVal < valSig) {
                        maxVal = valSig;
                        maxIndex = 0;
                    } else
                        maxIndex++;

                    if (samples.size() > 2000) {
                        samples.pollFirst();
                    }
                    if (time.size() < LEN)
                        time.add(counter++);

                    if (maxIndex == 1000) {
                        if (samples.size() == 2000 && maxVal > threshold) {
                            float[] tempArray = new float[2000];
                            int count = 0;
                            for (var item : samples) {
                                tempArray[count] = item;
                                count++;
                            }
                            prediction = new Prediction(System.currentTimeMillis(),
                                    model.predict(tempArray));
                        }
                        maxIndex = 0;
                        maxVal = 0;
                    }
                }

                while (signalBuffer.size() > LEN) {
                    signalBuffer.remove(0);
                }


            }
            socket.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("thread dead");
    }
    
    @Data
    public static class Prediction {
        long predictionTime;
        float[] prediction;

        public Prediction(long predictionTime, float[] prediction) {
            this.predictionTime = predictionTime;
            this.prediction = prediction;
        }

        public Prediction() {
            predictionTime = 0;
            prediction = new float[4];
        }
    }
}
