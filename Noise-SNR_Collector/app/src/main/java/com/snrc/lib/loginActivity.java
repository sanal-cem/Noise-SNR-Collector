package com.snrc.lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Created by Cem on 10.11.2017.
 */

public class loginActivity extends AsyncTask<Object, Void, String>{
    private TextView statusField;
    public loginActivity(TextView statusField) {
        this.statusField = statusField;
    }

    protected void onPreExecute(){
    }
    @Override
    protected String doInBackground(Object... objS) {
        String tablename = (String) objS[0];
        String textViewResult = "";
        try {
            String link = "http://noisesnr.eu-west-2.elasticbeanstalk.com/login.php/?username=" + "root" + "&password=" + "asdcemasd" + "&TABLE=" + tablename;

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(link));
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";

            while ((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            Global.jsonText = sb.toString();
            textViewResult = "Data Processed";
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
            textViewResult = "error";
        }
        return textViewResult;
    }
    @Override
    protected void onPostExecute(String textViewResult){
        statusField.setText(textViewResult);
    }
}