package com.snrc.lib;

import org.json.JSONArray;

public class Global {
    public static Double Avg_myNetwork_Signal = Double.valueOf(0.0d);
    public static Double Avg_myNetwork_linkSpeed = Double.valueOf(0.0d);
    public static double[] LinkSpeed_history = new double[100];
    public static Integer Moving_Avg_Interval = Integer.valueOf(20);
    public static Double Moving_Avg_LinkSpeed;
    public static Double Moving_Avg_SNR;
    public static Double Moving_Avg_Signal;
    public static Double Noise;
    public static Double SNR;
    public static double[] SNR_history = new double[100];
    public static double[] Signal_history = new double[100];
    public static Double Total_myNetwork_Signal = Double.valueOf(0.0d);
    public static Double Total_myNetwork_linkSpeed = Double.valueOf(0.0d);
    public static double interference_range = 10.0d;
    public static String myNetwork_BSSID;
    public static String myNetwork_SSID = "";
    public static Double myNetwork_Signal;
    public static int myNetwork_frequency;
    public static Integer myNetwork_linkSpeed = Integer.valueOf(0);
    public static int step;
    public static JSONArray jsonArr;
    public static String jsonText;
    public static int routeNum;
    public static String networkType;
    public static String operatorName;
}