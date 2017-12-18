package com.snrc.lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class SNRDataPoint {
    private Date date;
    private Double noise;
    private Double signal;
    private int snr;
    private long time;
    public int[] routeNum;
    public double[] loc_x;
    public double[] loc_y;
    public double[] SNR_all;
    public int jsonArrLength;

    public SNRDataPoint() {
    }
    public SNRDataPoint(Double signal, Double noise, int snr, long time, Date date) {
        this.signal = signal;
        this.noise = noise;
        this.snr = snr;
        this.date = date;
        this.time = time;
    }
    public Double getSignal() {
        return this.signal;
    }
    public Double getNoise() {
        return this.noise;
    }
    public int getSNR() {
        return this.snr;
    }
    public long getTime() {
        return this.time;
    }
    public Date getDate() {
        return this.date;
    }

    public void fetchSNRDataDb(){
        if(Global.jsonText != null) {
            JSONObject jsonObj;
            try {
                Global.jsonArr = new JSONArray(Global.jsonText);
                jsonArrLength = Global.jsonArr.length();
                SNR_all = new double[jsonArrLength];
                routeNum = new int[jsonArrLength];
                loc_x = new double[jsonArrLength];
                loc_y = new double[jsonArrLength];

                for (int i = 0; i < jsonArrLength; i++) {
                    jsonObj = Global.jsonArr.getJSONObject(i);

                    SNR_all[i] = jsonObj.getDouble("SNR");
                    routeNum[i] = jsonObj.getInt("ROUTENUM");
                    loc_x[i] = jsonObj.getDouble("LOC_X");
                    loc_y[i] = jsonObj.getDouble("LOC_Y");
                    if(i>0) {
                        if (routeNum[i] > routeNum[i - 1])
                            Global.routeNum = routeNum[i] + 1;
                    }
                    else {
                        Global.routeNum = routeNum[i] + 1;
                    }
                    //total_SNR += SNR_all[i];
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
