package com.snrc.cell;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.snrc.lib.Global;

/**
 * Created by Cem on 27.11.2017.
 */

public class cellListenerActivity extends PhoneStateListener  {

    private boolean enabledMData;
    private TelephonyManager tm;
    private static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    private final static String LTE_SIGNAL_STRENGTH = "getLteSignalStrength";

    public void scanCellData(Context c) {
        //SignalStrength signalStrength = new SignalStrength();
        TelephonyManager tm = (TelephonyManager) c.getSystemService(c.TELEPHONY_SERVICE);
        Global.operatorName = tm.getNetworkOperatorName();
        if(Global.operatorName == "")
            Global.operatorName = tm.getSimOperatorName();

        getNetworkType(c);
        tm.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    // Listener for the signal strength.
    @Override
    public void onSignalStrengthsChanged(SignalStrength ss) {
        super.onSignalStrengthsChanged(ss);

        //if phone is not Huawei, execute the general analyzing code.
        analyzeCellHuawei(ss);
    }

    private void analyzeCellGeneral(SignalStrength ssG) {
        if (ssG.isGsm()) {
            if (ssG.getGsmSignalStrength() != 99)
                Global.myNetwork_Signal = ssG.getGsmSignalStrength() * 2 - 113.0;
            else
                Global.myNetwork_Signal = ssG.getGsmSignalStrength() + 0.0;
        } else {
            Global.myNetwork_Signal = ssG.getCdmaDbm() + 0.0;
        }
        Global.SNR = ssG.getEvdoSnr() + 0.0;
    }

    private void analyzeCellHuawei(SignalStrength ssH) {
        String ssignal = ssH.toString();
        String[] parts = ssignal.split(" ");
        if(ssH.getCdmaDbm() == -1 && ssH.getGsmSignalStrength() == 0 && ssH.getEvdoDbm() == -1  ) {
            if (!parts[1].toString().equals("0") || !parts[2].toString().equals("0") || !parts[3].toString().equals("0")) {
                Global.myNetwork_Signal = Double.parseDouble(parts[3]) + 0.0;
                Global.SNR = Double.parseDouble(parts[7]) + 0.0;
            } else {
                Global.myNetwork_Signal = Double.parseDouble(parts[12]) + 0.0;
                Global.SNR = Double.parseDouble(parts[11]) + 0.0;
            }
        }
        else {
            analyzeCellGeneral(ssH);
        }
    }

    private void getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        String networkType = activeNetworkInfo.getTypeName();
        String networkSubType = activeNetworkInfo.getSubtypeName();
        if (!TextUtils.isEmpty(networkSubType)) networkType += "/" + networkSubType;
        Global.networkType = networkType;
    }
}
