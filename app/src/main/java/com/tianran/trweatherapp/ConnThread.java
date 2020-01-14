package com.tianran.trweatherapp;

import android.net.ConnectivityManager;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnThread extends Thread {

    public static final String TAG="ConnThread";
    private String url;
    private String param;
    private String res;

    ConnThread(String url,String param){
        this.url=url;
        this.param=param;
    }

    public String getRes() {
        return res;
    }

    @Override
    public void run() {
        super.run();
        Log.d(TAG, ":run");
        String urlString = url + param;
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                Log.d(TAG, "run_now: " + line);
            }
            res = response.toString();
            Log.d(TAG, "run_now: " + res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
