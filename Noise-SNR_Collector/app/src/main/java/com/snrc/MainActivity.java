package com.snrc;

/**
 * Created by Cem on 26.10.2017.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.snrc.R;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gainPermission();

        Button butWifi = findViewById(R.id.butWifi);
        Button butMobile = findViewById(R.id.butMobile);
        butWifi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gotowNaviActivity();
            }
        });

        butMobile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gotocNaviActivity();
            }
        });

        // BANNER ad
        loadAdView(R.id.adView_main);
    }
    private void gotowNaviActivity() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            Intent i = new Intent(this, com.snrc.wifi.wNaviActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else{
            TextView connInfoText = findViewById(R.id.connInfoText);
            connInfoText.setText(R.string.eWifiData);
        }

    }
    private void gotocNaviActivity() {
        if(isMobileDataEnabled(this)) {
            Intent i = new Intent(this, com.snrc.cell.cNaviActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else{
            TextView connInfoText = findViewById(R.id.connInfoText);
            connInfoText.setText(R.string.eMobData);
        }
    }
    private void loadAdView(int adInt) {
        AdView adView = this.findViewById(adInt);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    public static boolean isMobileDataEnabled(Context context) {
        // If we have no SIM card, then then mobile data can't be enabled
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        if (simState == TelephonyManager.SIM_STATE_ABSENT) return false;

        // http://stackoverflow.com/questions/12806709/android-how-to-tell-if-mobile-network-data-is-enabled-or-disabled-even-when
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class<?> cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            return (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Verbose warning instead of error, because we don't want this polluting the logs.
            // Could not determine if we have mobile data enabled"
            return true;
        }
    }
    private void gainPermission() {
        try {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION}
                            , MY_PERMISSIONS_REQUEST_LOCATION);
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
