package com.server;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

@Data
@NoArgsConstructor(access = AccessLevel.NONE)
public class GestureSaver {
    private int gestureNum;
    private ArrayList<Float> signal;

    public GestureSaver(int gestureNum, ArrayList<Float> signal) {
        this.gestureNum = gestureNum;
        this.signal = signal;
    }

    private void createFolder(int folderNum) {
        File gestureNum = new File("gestures/" + folderNum);
        if (!gestureNum.exists()) {
            gestureNum.mkdirs();
        }
    }

    public boolean save() {
        LocalDate currentDate;
        LocalTime currentTime;
        File file;

        if (signal == null || signal.size() < 1)
            return false;

        currentDate = LocalDate.now();
        currentTime = LocalTime.now();

        file = new File("gestures/" + gestureNum + "/" + gestureNum + "_" + currentDate + "_" +
                currentTime.getHour() + "-" +
                currentTime.getMinute() + "-" +
                currentTime.getSecond() +
                ".txt");

        try {
            createFolder(gestureNum);
            if (!file.createNewFile())
                return false;

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file));) {
                StringBuilder stringBuilder = new StringBuilder();
                for (var value : signal)
                    stringBuilder.append(value).append("\n");
                writer.write(stringBuilder.substring(0, stringBuilder.length() - 1));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return true;
    }
}
