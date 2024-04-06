package com.server.chip;

import com.github.psambit9791.jdsp.filter.adaptive.AP;
import com.server.SignalUtils;
import com.server.connect.BatchHandler;
import com.server.connect.TcpUnoBatchHandler;
import com.server.detector.GestureDetector;
import com.server.detector.SimpleMiddlePeakDetector;
import com.server.filter.DefaultFilter;
import com.server.filter.Filter;
import com.server.filter.MeanFilter;
import com.server.model.NeuronModel;
import lombok.Data;
import org.apache.commons.math3.filter.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Data
public class SignalRecipient implements Runnable {
    private NeuronModel neuronModel;
    private Prediction prediction;
    private RecipientConfig config;
    private DefaultFilter defaultFilter;
    private List<Filter> filters;
    private ChartInfo chartInfo;
    private Socket socket;
    private String nameEndPoint;
    private SimpMessagingTemplate simpMessagingTemplate;
    private CopyOnWriteArrayList<Float> signalQueue;
    private CopyOnWriteArrayList<Integer> pureSignal;
    private CopyOnWriteArrayList<Float> defaultFilterSignal;
    private CopyOnWriteArrayList<Float> withoutFilterSignal;
    private CopyOnWriteArrayList<Float> kalmanFilterSignal;
    private KalmanFilter kalmanFilter;
    private float kalmanMean = 0;
    private float defaultMean = 0;
    private float withoutFilterMean = 0;
    private final Flux<List<Float>> dataFlux;
    private FluxSink<List<Float>> dataSink;
    private DataInputStream inputStream;

    public SignalRecipient() {
        this.dataFlux = Flux.create(emitter -> this.dataSink = emitter, FluxSink.OverflowStrategy.BUFFER);

        dataFlux.subscribe(System.out::println);

        prediction = new Prediction();
        defaultFilter = new DefaultFilter();
        chartInfo = new ChartInfo();

        pureSignal = new CopyOnWriteArrayList<>();
        signalQueue = new CopyOnWriteArrayList<>();

        defaultFilterSignal = new CopyOnWriteArrayList<>();
        withoutFilterSignal = new CopyOnWriteArrayList<>();
        kalmanFilterSignal = new CopyOnWriteArrayList<>();
        filters = new ArrayList<>();

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

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    @Override
    public void run() {
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {
            byte[] data = new byte[528];
            var samples = new LinkedList<Float>();
            var pureSamples = new LinkedList<Float>();
            BatchHandler batchHandler = new TcpUnoBatchHandler(config, inputStream);
            GestureDetector gestureDetector = new SimpleMiddlePeakDetector(config);
            this.inputStream = inputStream;
            Filter meanFilter = new MeanFilter();


            System.out.println(config);
            socket.setSoTimeout(5000);
//                socket.connect(new InetSocketAddress(ip, port), 10000);

            List<Float> list = new ArrayList<>();

            System.out.println(simpMessagingTemplate.getDefaultDestination());
            System.out.println(simpMessagingTemplate.getUserDestinationPrefix());
            System.out.println(simpMessagingTemplate.getMessageChannel());

            int counter = 0;
            int N = 10;

            while (!Thread.currentThread().isInterrupted()) {
                if (counter == N)
                    list.clear();
                else
                    counter++;
                for (int receivedItem : batchHandler.getValue()) {
                    double value =  config.getSigScale() * receivedItem;

                    float a = (float) meanFilter.calculate(value)[0];
                    float b = (float) useFilter(value)[0];
                    pureSamples.add(a);
                    samples.add(b);
                    list.add(b);



//                    withoutFilterSignal.add(a);
//                    defaultFilterSignal.add(b);

                    if (samples.size() > config.getGestureSize()) {
                        samples.pollFirst();
                        pureSamples.pollFirst();
                    }

                    if (gestureDetector.isGesture(samples)) {
                        float[] tempArray = new float[2000];
                        int count = 0;

//                        for (var item : samples) {
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

                        simpMessagingTemplate.convertAndSend("/topic/portfolio?name=" + nameEndPoint, SignalUtils.getSpectrum(prediction.getInputSignal()));
                        simpMessagingTemplate.convertAndSend("/topic/portfolio/gesture?name=" + nameEndPoint, prediction);
                        System.out.println("prediction set");

                        chartInfo.setPrediction(prediction);


                    }
                }

                while (defaultFilterSignal.size() > config.getGraphLen()) {
                    defaultFilterSignal.remove(0);
                    withoutFilterSignal.remove(0);
//                    kalmanFilterSignal.remove(0);
//                    pureSignal.remove(0);
                }
                if (counter == N)
                    simpMessagingTemplate.convertAndSend("/topic/portfolio/chart?name=" + nameEndPoint, list);



            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("thread dead");
    }

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

    public SignalRecipient setNameEndPoint(String nameEndPoint) {
        this.nameEndPoint = nameEndPoint;
        return this;
    }

    public SignalRecipient setNeuronModel(NeuronModel neuronModel) {
        this.neuronModel = neuronModel;
        return this;
    }

    public SignalRecipient setChartInfo(ChartInfo chartInfo) {
        this.chartInfo = chartInfo;
        return this;
    }

    public SignalRecipient setRecipientConfig(RecipientConfig config) {
        this.config = config;
        return this;
    }

    public SignalRecipient setSimpMessagingTemplate(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        return this;
    }


    public float defaultFilter(double value) {
        float temp = defaultFilter.calculate(value);
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

    private double[] useFilter(double... sample) {
        double[] saveSample = sample;
        for (Filter filter : filters) {
            saveSample = filter.calculate(saveSample);
        }
        return saveSample;
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
