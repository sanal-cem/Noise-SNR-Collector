package com.snrc.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.snrc.lib.Global;
import com.snrc.lib.SNRDataPoint;

import java.util.Date;
import java.util.List;

public class wSNRData {
    int signal_difference = 0;
    Date curr_time;
    public WifiInfo info;
    double[] signal_table = new double[]{3.0d, 2.6d, 2.2d, 1.8d, 1.5d, 1.25d, 1.0d, 0.85d, 0.7d, 0.55d, 0.4d, 0.35d, 0.3d, 0.25d, 0.2d, 0.15d, 0.1d};
    int snrCalculated;
    double temp_sum_linkspeed = 0.0d;
    double temp_sum_signal = 0.0d;
    double temp_sum_snr = 0.0d;
    public WifiManager wifi;

    public SNRDataPoint scanWifiData(Context context) {
        Global.myNetwork_Signal = Double.valueOf(0.0d);
        Global.Noise = Double.valueOf(0.0d);
        this.wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (this.wifi == null) {
            return null;
        }
        this.info = this.wifi.getConnectionInfo();
        if (this.info.getBSSID() == null) {
            return null;
        }
        List<ScanResult> sresults = this.wifi.getScanResults();
        Global.myNetwork_SSID = this.info.getSSID();
        if (Global.myNetwork_SSID == null) {
            return null;
        }
        Global.myNetwork_SSID = trim_string(Global.myNetwork_SSID);
        Global.myNetwork_BSSID = this.info.getBSSID();
        if (Global.myNetwork_BSSID == null) {
            return null;
        }
        Global.myNetwork_BSSID = trim_string(Global.myNetwork_BSSID);
        Global.myNetwork_linkSpeed = Integer.valueOf(this.info.getLinkSpeed());
        for (ScanResult sresult : sresults) {
            if (Global.myNetwork_BSSID.equalsIgnoreCase(sresult.BSSID) && Global.myNetwork_SSID.equalsIgnoreCase(sresult.SSID)) {
                Global.myNetwork_Signal = Double.valueOf(((double) sresult.level) + 0.0d);
                Global.myNetwork_frequency = sresult.frequency;
                Global.myNetwork_linkSpeed = Integer.valueOf(this.info.getLinkSpeed());
            }
        }
        Global.Total_myNetwork_linkSpeed = Double.valueOf(Global.Total_myNetwork_linkSpeed.doubleValue() + Double.valueOf(((double) Global.myNetwork_linkSpeed.intValue()) + 0.0d).doubleValue());
        Global.Avg_myNetwork_linkSpeed = Double.valueOf(Global.Total_myNetwork_linkSpeed.doubleValue() / ((double) (Global.step + 1)));
        Global.Total_myNetwork_Signal = Double.valueOf(Global.Total_myNetwork_Signal.doubleValue() + Double.valueOf(Global.myNetwork_Signal.doubleValue() + 0.0d).doubleValue());
        Global.Avg_myNetwork_Signal = Double.valueOf(Global.Total_myNetwork_Signal.doubleValue() / ((double) (Global.step + 1)));
        signal_difference = 0;
        for (ScanResult sresult2 : sresults) {
            if (!Global.myNetwork_BSSID.equalsIgnoreCase(sresult2.BSSID) && ((double) sresult2.frequency) >= ((double) Global.myNetwork_frequency) - Global.interference_range && ((double) sresult2.frequency) <= ((double) Global.myNetwork_frequency) + Global.interference_range) {
                if (Global.Noise.doubleValue() == 0.0d) {
                    Global.Noise = Double.valueOf(((double) sresult2.level) + 0.0d);
                } else {
                    signal_difference = Math.abs((int) (Global.Noise.doubleValue() - ((double) sresult2.level)));
                    if (signal_difference > 16) {
                        Global.Noise = Double.valueOf(Math.max(Global.Noise.doubleValue(), (double) sresult2.level));
                    } else {
                        Global.Noise = Double.valueOf(Math.max(Global.Noise.doubleValue(), (double) sresult2.level) + this.signal_table[signal_difference]);
                    }
                }
            }
        }
        Global.myNetwork_Signal = Double.valueOf(((double) Math.round(Global.myNetwork_Signal.doubleValue() * 100.0d)) / 100.0d);
        Global.Noise = Double.valueOf(((double) Math.round(Global.Noise.doubleValue() * 100.0d)) / 100.0d);
        this.snrCalculated = (int) (Global.myNetwork_Signal.doubleValue() - Global.Noise.doubleValue());
        if (Global.myNetwork_Signal.doubleValue() == 0.0d) {
            this.snrCalculated = 0;
        }
        Global.SNR = Double.valueOf((double) this.snrCalculated);
        Global.SNR_history[Global.step % Global.Moving_Avg_Interval.intValue()] = Global.SNR.doubleValue();
        Global.LinkSpeed_history[Global.step % Global.Moving_Avg_Interval.intValue()] = (double) Global.myNetwork_linkSpeed.intValue();
        Global.Signal_history[Global.step % Global.Moving_Avg_Interval.intValue()] = Global.myNetwork_Signal.doubleValue();
        this.temp_sum_snr = 0.0d;
        this.temp_sum_linkspeed = 0.0d;
        this.temp_sum_signal = 0.0d;
        int i;
        if (Global.step < Global.Moving_Avg_Interval.intValue()) {
            for (i = 0; i <= Global.step; i++) {
                this.temp_sum_snr += Global.SNR_history[i];
                this.temp_sum_linkspeed += Global.LinkSpeed_history[i];
                this.temp_sum_signal += Global.Signal_history[i];
            }
            Global.Moving_Avg_SNR = Double.valueOf(this.temp_sum_snr / ((double) (Global.step + 1)));
            Global.Moving_Avg_LinkSpeed = Double.valueOf(this.temp_sum_linkspeed / ((double) (Global.step + 1)));
            Global.Moving_Avg_Signal = Double.valueOf(this.temp_sum_signal / ((double) (Global.step + 1)));
        } else {
            for (i = 0; i < Global.Moving_Avg_Interval.intValue(); i++) {
                this.temp_sum_snr += Global.SNR_history[i];
                this.temp_sum_linkspeed += Global.LinkSpeed_history[i];
                this.temp_sum_signal += Global.Signal_history[i];
            }
            Global.Moving_Avg_SNR = Double.valueOf(this.temp_sum_snr / ((double) Global.Moving_Avg_Interval.intValue()));
            Global.Moving_Avg_LinkSpeed = Double.valueOf(this.temp_sum_linkspeed / ((double) Global.Moving_Avg_Interval.intValue()));
            Global.Moving_Avg_Signal = Double.valueOf(this.temp_sum_signal / ((double) Global.Moving_Avg_Interval.intValue()));
        }
        this.wifi.startScan();
        this.curr_time = new Date(System.currentTimeMillis());
        Double d = Global.myNetwork_Signal;
        Double d2 = Global.Noise;
        int j = this.snrCalculated;
        int k = Global.step;
        Global.step = k + 1;
        return new SNRDataPoint(d, d2, j, (long) k, this.curr_time);
    }

    private String trim_string(String str) {
        String quotes = new String("\"");
        int str_len = str.length();
        if (str.substring(0, 1).equals(quotes) && str.substring(str_len - 1, str_len).equals(quotes)) {
            return str.substring(1, str_len - 1);
        }
        return str;
    }
}