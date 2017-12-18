package com.snrc.lib;

/**
 * Created by Cem on 17.11.2017.
 */

public class fetchedSNRDataP {
    private int routeNum;
    private double SNR;
    private double loc_x;
    private double loc_y;
    private String date;
    private String time;
    private String table;

    public fetchedSNRDataP() {
    }
    public int getRouteNum() {
        return this.routeNum;
    }
    public double getSNR() {
        return this.SNR;
    }
    public double getLoc_x() {
        return this.loc_x;
    }
    public double getLoc_y() {
        return this.loc_y;
    }
    public String getDate() {
        return this.date;
    }
    public String getTime() {
        return this.time;
    }
    public String getTable() {
        return this.table;
    }
    public void setRouteNum(int routeNum) {
        this.routeNum = routeNum;
    }
    public void setSNR(double SNR) {
        this.SNR = SNR;
    }
    public void setLoc_x(double loc_x) {
        this.loc_x = loc_x;
    }
    public void setLoc_y(double loc_y) {
        this.loc_y = loc_y;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setTable(String table) {
        this.table = table;
    }
}
