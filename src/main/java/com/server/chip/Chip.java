package com.server.chip;

import com.server.chip.interaction.SignalRecipient;
import com.server.model.DefaultModel;
import com.server.model.Model;
import lombok.Data;

import java.io.Serializable;

@Data
public class Chip implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String ip;
    private int port;
    private SignalRecipient signalRecipient;
    private Model model;

    public Chip(String name, String ip, int port, Model model) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.model = model;
        model = new DefaultModel();
        signalRecipient = new SignalRecipient(model);
        signalRecipient.setIp(ip);
        signalRecipient.setPort(port);
    }

}
