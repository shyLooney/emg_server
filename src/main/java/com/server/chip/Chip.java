package com.server.chip;

import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class Chip implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String ip;
    private int port;
    private Socket socket;
    private SignalRecipient signalRecipient;
    private RecipientConfig config;
    private Thread threadRecipient;

    public Chip(String name, String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public Chip() {
        signalRecipient = new SignalRecipient();
    }

    public void restart() {
        System.out.println(ip + " " + port);
        if (threadRecipient != null && !threadRecipient.isInterrupted()) { // how does it work with multiple clicks
            threadRecipient.interrupt();
            try {
                threadRecipient.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            socket = new Socket(ip, port);
            socket.setSoTimeout(5000);
            signalRecipient.setSocket(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadRecipient = new Thread(signalRecipient);
        threadRecipient.start();
    }

    public void start() {
        try {
            socket = new Socket(ip, port);
            socket.setSoTimeout(5000);
            signalRecipient.setSocket(socket);

            threadRecipient = new Thread(signalRecipient);
            threadRecipient.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public Chip setRecipientConfig(RecipientConfig config) {
        this.config = config;
        return this;
    }

    public CopyOnWriteArrayList<Float> getDefaultFilter() {
        return signalRecipient.getDefaultFilterSignal();
    }

    public CopyOnWriteArrayList<Float> getKalmanFilter() {
        return signalRecipient.getKalmanFilterSignal();
    }

    public CopyOnWriteArrayList<Float> getWithoutFilter() {
        return signalRecipient.getWithoutFilterSignal();
    }

    public CopyOnWriteArrayList<Integer> getPureSignal() {
        return signalRecipient.getPureSignal();
    }

    public CopyOnWriteArrayList<Float> getSignalQueue() {
        return signalRecipient.getSignalQueue();
    }

    public Prediction getPrediction() {
        return signalRecipient.getPrediction();
    }
}
