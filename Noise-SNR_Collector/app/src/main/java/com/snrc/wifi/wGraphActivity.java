package com.snrc.wifi;

/**
 * Created by Cem on 15.11.2017.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.snrc.lib.GPSTracker;
import com.snrc.lib.Global;
import com.snrc.lib.SNRDataPoint;
import com.snrc.lib.asyncGraphLongWait;
import com.snrc.lib.asyncGraphSmallWait;
import com.snrc.lib.fetchedSNRDataP;
import com.snrc.lib.sendDataActivity;
import com.snrc.R;

import java.util.Calendar;

/**
 * Created by Cem on 15.11.2017.
 */

public class wGraphActivity extends AppCompatActivity {
    private int SNRlength = 14;
    private Calendar cal;
    private String dateNow;
    private String timeNow;
    private fetchedSNRDataP[] arrFSnrDP;
    private double minSNR = 100.0;
    private double maxSNR = -100.0;
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> series;
    private GraphView graph;
    private DataPoint[] SNRpoints;
    private DataPoint[] Signalpoints;
    private DataPoint[] AvgSNRpoints;
    private SNRDataPoint snrData;
    private double cacheSNR;
    private double cacheLoc_x;
    private double cacheLoc_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Global.jsonText != null){
            snrData = new SNRDataPoint();
            snrData.fetchSNRDataDb();
        }

        TextView countTextView = findViewById(R.id.infoTextView);
        countTextView.setText("Calculating...");

        cal = Calendar.getInstance();
        setDateNow();
        arrFSnrDP = new fetchedSNRDataP[1];

        FloatingActionButton fab = findViewById(R.id.backButGraph);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNaviActivity();
            }
        });
        // BANNER ad
        loadAdView(R.id.adView_graph);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Global.Moving_Avg_Signal = 0.0;
        try {
            mTimer1 = new Runnable() {
                @Override
                public void run() {
                    if(graph != null)
                        graph.removeAllSeries();
                    if (Global.jsonText != null) {
                        fetchSNR();
                        drawGraph();
                        setPageText();
                    }
                    new asyncGraphSmallWait().execute(mHandler, mTimer1, 40);
                }
            };
            new asyncGraphSmallWait().execute(mHandler, mTimer1, 40);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }
    private void loadAdView(int adInt) {
        AdView adView = this.findViewById(adInt);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    private void gotoNaviActivity() {
        Intent i = new Intent(this, wNaviActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.graph_refresh, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.graph_menu_refresh)
            refresh();
        else if(id == R.id.graph_send_data){
            sendData();
        }
        return super.onOptionsItemSelected(item);
    }
    private void refresh() {
        Intent i = new Intent(wGraphActivity.this, wGraphActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
    private void fetchSNR() {
        SNRpoints = new DataPoint[SNRlength];
        Signalpoints = new DataPoint[SNRlength];
        AvgSNRpoints = new DataPoint[SNRlength];
        generateData(SNRpoints, Signalpoints, AvgSNRpoints);
    }
    private void drawGraph() {
        drawGraph(SNRpoints, Color.GREEN, "@string/snrText");
        drawGraph(Signalpoints, Color.BLUE, "@string/signalText");
        drawGraph(AvgSNRpoints, Color.RED, "@string/avgSnrText");
    }
    private void setPageText() {
        TextView SSIDTextView = findViewById(R.id.SSIDTextView);
        SSIDTextView.setText("" + "SSID: " + Global.myNetwork_SSID);
        TextView countTextView = findViewById(R.id.infoTextView);
        countTextView.setText(" ");
        TextView maxSnrText = findViewById(R.id.maxSnrText);
        maxSnrText.setText("-- Max SNR (dB)= " + maxSNR);
        TextView minSnrText = findViewById(R.id.minSnrText);
        minSnrText.setText("-- Min SNR (dB)= " + minSNR);
        TextView avgSnrText = findViewById(R.id.avgSnrText);
        avgSnrText.setText("-- Avg SNR (dB)= " + Global.Moving_Avg_Signal);
    }
    private void generateData(DataPoint[] SNRpoints, DataPoint[] Signalpoints, DataPoint[] AvgSNRpoints) {
        fetchedSNRDataP fSnrDP;
        for(int i=0; i < SNRlength; i++) {
            listenWifi();
            fSnrDP = new fetchedSNRDataP();

            fSnrDP.setSNR(Global.SNR);
            cacheSNR = Global.SNR;
            minMaxSNRCalc();

            fSnrDP.setRouteNum(Global.routeNum);

            fSnrDP.setDate(dateNow);
            setTimeNow();
            fSnrDP.setTime(timeNow);
            fSnrDP.setTable("WSNR");

            SNRpoints[i] = new DataPoint(i, Global.SNR);
            Signalpoints[i] = new DataPoint(i, Global.myNetwork_Signal);
            AvgSNRpoints[i] = new DataPoint(i, Global.Moving_Avg_Signal);

            // check the location is changed or not.
            if(checkSNROldOrNot(fSnrDP)) {
                arrFSnrDP[0] = fSnrDP;
                sendData();
            }
            new asyncGraphLongWait().execute(250);
        }
    }
    private void listenWifi(){
        wSNRData wSnrData = new wSNRData();
        wSnrData.scanWifiData(this);
    }
    private void drawGraph(DataPoint[] points, int color, String title) {
        series = new LineGraphSeries<DataPoint>(points);
        graph = findViewById(R.id.graph);
        series.setTitle(title);
        series.setColor(color);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(5);
        series.setThickness(3);
        // set manual bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-100);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(4);
        graph.getViewport().setMaxX(SNRlength);
        graph.addSeries(series);
    }
    private void sendData() {
        TextView infoTextView = findViewById(R.id.infoTextView);
        new sendDataActivity(infoTextView).execute(arrFSnrDP);
    }
    private boolean checkSNROldOrNot(fetchedSNRDataP fSnrDP) {
        GPSTracker gpsTObj = new GPSTracker(this);
        // get location of SNR data.
        double lat = gpsTObj.getLatitude();
        double lon = gpsTObj.getLongitude();
        if(cacheLoc_x != lat || cacheLoc_y != lon){
            if(lat < 0.1 && lat > -0.1 && lon < 0.1 && lon > -0.1 ) {
                return false;
            }
            else{
                cacheLoc_x = lat;
                cacheLoc_y = lon;
                fSnrDP.setLoc_x(cacheLoc_x);
                fSnrDP.setLoc_y(cacheLoc_y);
                return true;
            }
        }
        return false;
    }
    private void setDateNow() {
        String month = "", day = "";
        if(cal.get(Calendar.MONTH) < 10)
            month = "0" + cal.get(Calendar.MONTH);
        else
            month = "" + cal.get(Calendar.MONTH);
        if(cal.get(Calendar.DAY_OF_MONTH) < 10)
            day = "0" + cal.get(Calendar.DAY_OF_MONTH);
        else
            day = "" + cal.get(Calendar.DAY_OF_MONTH);

        dateNow = cal.get(Calendar.YEAR) + "-" + month + "-" + day;
    }
    private void setTimeNow() {
        String hour = "", minute = "", second = "";
        if (cal.get(Calendar.HOUR_OF_DAY) < 10)
            hour = "0" + cal.get(Calendar.HOUR_OF_DAY);
        else
            hour = "" + cal.get(Calendar.HOUR_OF_DAY);
        if (cal.get(Calendar.MINUTE) < 10)
            minute = "0" + cal.get(Calendar.MINUTE);
        else
            minute = "" + cal.get(Calendar.MINUTE);
        if (cal.get(Calendar.SECOND) < 10)
            second = "0" + cal.get(Calendar.SECOND);
        else
            second = "" + cal.get(Calendar.SECOND);

        timeNow = hour + minute + second;
    }
    private void minMaxSNRCalc(){
        if(Global.SNR < minSNR)
            minSNR = Global.SNR;
        if(Global.SNR > maxSNR)
            maxSNR = Global.SNR;
    }
}
