package com.snrc.lib;

import android.os.AsyncTask;
import android.os.Handler;

/**
 * Created by Cem on 5.12.2017.
 */

public class asyncGraphSmallWait extends AsyncTask<Object, Void, Void>
{

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }
    @Override
    protected Void doInBackground(Object... params) {
        Handler mHandler = (Handler) params[0];
        Runnable mTimer1 = (Runnable) params[1];
        int time = (int) params[2];
        mHandler.postDelayed(mTimer1, time);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        //this method will be running on UI thread

    }
}