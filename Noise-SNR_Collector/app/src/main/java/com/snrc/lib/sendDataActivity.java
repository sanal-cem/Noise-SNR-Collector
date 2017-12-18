package com.snrc.lib;

import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Created by Cem on 16.11.2017.
 */

public class sendDataActivity extends AsyncTask<fetchedSNRDataP, Void, String>{
    private TextView infoTextView;
    public sendDataActivity(TextView infoTextView) {
        this.infoTextView = infoTextView;
    }

    protected void onPreExecute(){
    }
    @Override
    protected String doInBackground(fetchedSNRDataP... arrFSnrDP) {
        String textViewResult = "";
        HttpResponse response;

        response = null;
        try {
            String link = "http://noisesnr.eu-west-2.elasticbeanstalk.com/sendData.php/?username=" + "root" + "&password=" + "asdcemasd"
                    + "&ROUTENUM=" + arrFSnrDP[0].getRouteNum() + "&SNR=" + arrFSnrDP[0].getSNR()
                    + "&LOC_X=" + arrFSnrDP[0].getLoc_x() + "&LOC_Y=" + arrFSnrDP[0].getLoc_y() + "&SDATE=" + arrFSnrDP[0].getDate() + "&STIME=" + arrFSnrDP[0].getTime() + "&TABLE=" + arrFSnrDP[0].getTable();

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(link));
            response = client.execute(request);

            textViewResult = "Data Sent";

        } catch (Exception e) {
            e.printStackTrace();
            textViewResult = "error " + response;
        }
        return textViewResult;
    }
    @Override
    protected void onPostExecute(String textViewResult){
        infoTextView.setText(textViewResult);
    }
}