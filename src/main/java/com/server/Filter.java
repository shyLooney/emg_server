package com.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Filter {
//    static private Filter filter = null;
//    private double[] m_circBuf;
//    private double[] m_impResp;
    private int m_head = 0;
    private ArrayList<Double> m_impResp;
    private ArrayList<Double> m_circBuf;


    public Filter() {
        m_impResp = new ArrayList<Double>();
        try (Scanner scanner = new Scanner(new File("filter.txt"))) {
            while (scanner.hasNextLine()) {
                m_impResp.add(Double.parseDouble(scanner.nextLine()));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Size filter: " + m_impResp.size());
        m_circBuf = new ArrayList<>();
        for (int i = 0; i < m_impResp.size(); i++) {
            m_circBuf.add(0.0);
        }
    }

//    static public Filter getFilter() {
//        if (filter == null) {
//            filter = new Filter();
//
//            filter.m_impResp = new ArrayList<Double>();
//            try (Scanner scanner = new Scanner(new File("filter.txt"))) {
//                while (scanner.hasNextLine()) {
//                    filter.m_impResp.add(Double.parseDouble(scanner.nextLine()));
//                }
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//
//            System.out.println("Size filter: " + filter.m_impResp.size());
//            filter.m_circBuf = new ArrayList<>();
//            for (int i = 0; i < filter.m_impResp.size(); i++) {
//                filter.m_circBuf.add(0.0);
//            }
//        }
//        return filter;
//    }

    public float calculate(double sample) {
        m_circBuf.set(m_head, sample);
        m_head = (m_head + 1) % m_impResp.size();
        double out_sample = 0.0 ;
//        System.out.print("before filter: " + sample);
        for (int nTap=0; nTap < m_impResp.size(); nTap++) {
            out_sample += m_impResp.get(m_impResp.size() - nTap - 1) * m_circBuf.get((m_head + nTap) % m_impResp.size());
        }
//        System.out.println(" after filter: " + (float)out_sample);
        return ((float)out_sample) ;
    }


}
