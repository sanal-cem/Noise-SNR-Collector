package com.snrc.lib;

import android.os.AsyncTask;

/**
 * Created by Cem on 5.12.2017.
 */

public class asyncGraphLongWait extends AsyncTask<Object, Void, Void>
{

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }
    @Override
    protected Void doInBackground(Object... objT) {
        int time = (int) objT[0];
        try {
            Thread.sleep(time);
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        //this method will be running on UI thread

    }
}
