package com.server.chip;

import com.github.psambit9791.jdsp.filter.adaptive.AP;
import com.github.psambit9791.jdsp.signal.peaks.FindPeak;
import com.server.Filter;
import com.server.SignalUtils;
import com.server.model.NeuronModel;
import lombok.Data;
import org.apache.commons.math3.filter.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class Chip implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String ip;
    private int port;
    private ChartInfo chartInfo;
    private RecipientConfig config;
    private SignalRecipient signalRecipient;
    private NeuronModel neuronModel;
    private Thread threadRecipient;
    private SimpMessagingTemplate simpMessagingTemplate;

    public Chip(String name, String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public Chip() {
        signalRecipient = new SignalRecipient();
        threadRecipient = new Thread(signalRecipient);
        chartInfo = new ChartInfo();
    }

    public void start() {
        System.out.println(ip + " " + port);
        if (!threadRecipient.isInterrupted()) { // how does it work with multiple clicks
            threadRecipient.interrupt();
            try {
                threadRecipient.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        threadRecipient = new Thread(signalRecipient);
        threadRecipient.start();
    }

    public void stop() throws IOException {
    }

    public Chip setName(String name) {
        this.name = name;
        return this;
    }

    public Chip setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Chip setPort(int port) {
        this.port = port;
        return this;
    }

    public Chip setNeuronModel(NeuronModel neuronModel) {
        this.neuronModel = neuronModel;
        return this;
    }

    public Chip setRecipientConfig(RecipientConfig config) {
        this.config = config;
        return this;
    }

    public Chip setChartInfo(ChartInfo chartInfo) {
        this.chartInfo = chartInfo;
        return this;
    }

    public CopyOnWriteArrayList<Float> getDefaultFilter() {
        return signalRecipient.defaultFilterSignal;
    }

    public CopyOnWriteArrayList<Float> getKalmanFilter() {
        return signalRecipient.kalmanFilterSignal;
    }

    public CopyOnWriteArrayList<Float> getWithoutFilter() {
        return signalRecipient.withoutFilterSignal;
    }

    public CopyOnWriteArrayList<Integer> getPureSignal() {
        return signalRecipient.pureSignal;
    }

    public CopyOnWriteArrayList<Float> getSignalQueue() {
        return signalRecipient.signalQueue;
    }

    public Prediction getPrediction() {
        return signalRecipient.prediction;
    }


    @Data
    public class SignalRecipient implements Runnable {
        //        private CopyOnWriteArrayList<Float> signalBuffer;
        private CopyOnWriteArrayList<Float> signalQueue;
        private CopyOnWriteArrayList<Integer> pureSignal;
        private CopyOnWriteArrayList<Float> defaultFilterSignal;
        private CopyOnWriteArrayList<Float> withoutFilterSignal;
        private CopyOnWriteArrayList<Float> kalmanFilterSignal;
        private Prediction prediction;
        private Filter filter;
        private KalmanFilter kalmanFilter;
        private float kalmanMean = 0;
        private float defaultMean = 0;
        private float withoutFilterMean = 0;
        private final Flux<List<Float>> dataFlux;
        private FluxSink<List<Float>> dataSink;


        public SignalRecipient() {
            this.dataFlux = Flux.create(emitter -> this.dataSink = emitter, FluxSink.OverflowStrategy.BUFFER);

            dataFlux.subscribe(System.out::println);

            prediction = new Prediction();
            filter = new Filter();

            pureSignal = new CopyOnWriteArrayList<>();
            signalQueue = new CopyOnWriteArrayList<>();

            defaultFilterSignal = new CopyOnWriteArrayList<>();
            withoutFilterSignal = new CopyOnWriteArrayList<>();
            kalmanFilterSignal = new CopyOnWriteArrayList<>();

            double constantVoltage = 1d;
            double measurementNoise = 0.1d;
            double processNoise = 1e-5d;

            double dt = 1d;

            RealMatrix A = new Array2DRowRealMatrix(new double[]{1d});
            RealMatrix B = null;
            RealMatrix H = new Array2DRowRealMatrix(new double[]{1d});
            RealVector x = new ArrayRealVector(new double[]{constantVoltage});
            RealMatrix Q = new Array2DRowRealMatrix(new double[]{processNoise});
            RealMatrix P0 = new Array2DRowRealMatrix(new double[]{1d});
            RealMatrix R = new Array2DRowRealMatrix(new double[]{measurementNoise});

            ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
            MeasurementModel mm = new DefaultMeasurementModel(H, R);

            kalmanFilter = new KalmanFilter(pm, mm);

        }

        @Override
        public void run() {
            try (Socket socket = new Socket(ip, port);
                 DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {
                byte[] data = new byte[528];
                var samples = new LinkedList<Float>();
                var pureSamples = new LinkedList<Float>();
                int maxIndex = 0;
                float maxVal = 0;


                System.out.println(config);
                socket.setSoTimeout(5000);
//                socket.connect(new InetSocketAddress(ip, port), 10000);


                while (!Thread.currentThread().isInterrupted()) {
                    int offset = 0;
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
                        int value = ((data[i + 3] & 0xFF) << 24) | ((data[i + 2] & 0xFF) << 16)
                                | ((data[i + 1] & 0xFF) << 8) | (data[i] & 0xFF);
                        pureSignal.add(value);

                        double temp = value * 0.00001;

                        float valSig;

                        kalmanFilter(temp);
                        valSig = defaultFilter(temp);
                        float tempPure = withoutFilter(temp);

                        samples.add(valSig);
                        pureSamples.add(tempPure);


//                        FindPeak fp = new FindPeak();

                        if (samples.size() > config.getGestureSize()) {
                            samples.pollFirst();
                            pureSamples.pollFirst();

                        }

                        if (maxVal < valSig) {
                            maxVal = valSig;
                            maxIndex = 0;
                        } else
                            maxIndex++;

                        if (maxIndex == 1000) {
                            if (samples.size() == config.getGestureSize() && maxVal > config.getThreshold()) {
                                float[] tempArray = new float[2000];
                                int count = 0;

                                for (var item : samples) {
                                    tempArray[count] = item;
                                    count++;
                                }

                                prediction = new Prediction(System.currentTimeMillis(),
                                        neuronModel.predict(tempArray),
                                        samples,
                                        pureSamples
                                );
                                dataSink.next(prediction.getInputSignal());


                                simpMessagingTemplate.convertAndSend("/topic/portfolio?name=" + name, SignalUtils.getSpectrum(prediction.getInputSignal()));
                                System.out.println("prediction set");

                                chartInfo.setPrediction(prediction);
                            }
                            maxIndex = 0;
                            maxVal = 0;
                        }
                    }


                    while (defaultFilterSignal.size() > config.getGraphLen()) {
                        defaultFilterSignal.remove(0);
                        kalmanFilterSignal.remove(0);
                        withoutFilterSignal.remove(0);
                        pureSignal.remove(0);
                    }

//                    while (signalQueue.size() > config.getGraphLen()) {
//                        signalBuffer.remove(0);
//                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("thread dead");
        }


        public float defaultFilter(double value) {
            float temp = filter.calculate(value);
            defaultMean = (float) (defaultMean * 0.99 + 0.01 * temp);
            float sig = temp - defaultMean;

            defaultFilterSignal.add(sig);

            return sig;
        }

        public float withoutFilter(double value) {
            withoutFilterMean = (float) (withoutFilterMean * 0.99 + 0.01 * value);
            float sig = (float) (value - withoutFilterMean);

            withoutFilterSignal.add(sig);

            return sig;
        }

        public float kalmanFilter(double value) {
            double[] array = new double[]{value};

//            kalmanFilter.predict();
//            kalmanFilter.correct(array);

            int order = 3;
            double mu = 0.25;
            double eps = 0.001;
            int length = 1;

            AP filt1 = new AP(length, order, mu, eps, AP.WeightsFillMethod.ZEROS); // Initialising weights to zero
            filt1.filter(new double[]{1d}, new double[]{value});



//            float temp = (float) kalmanFilter.getStateEstimation()[0];
            float temp = (float) filt1.getOutput()[0];
            kalmanMean = (float) (kalmanMean * 0.99 + 0.01 * temp);
            float sig = temp - kalmanMean;

            kalmanFilterSignal.add(sig);

            return sig;
        }
    }
}
