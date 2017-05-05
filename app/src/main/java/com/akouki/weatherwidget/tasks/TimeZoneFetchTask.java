package com.akouki.weatherwidget.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TimeZoneFetchTask extends AsyncTask<Void, Void, Integer> {

    private String TIMEZONE_API = "http://api.timezonedb.com/v2/get-time-zone";
    private String API_KEY = "YOUR_API_KEY";

    private double lat, lng;

    public TimeZoneFetchTask(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            StringBuilder sb = new StringBuilder(TIMEZONE_API);
            sb.append("?key=" + API_KEY);
            sb.append("&format=json&by=position");
            sb.append("&lat=" + lat + "&lng=" + lng);

            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return new JSONObject(result.toString()).getInt("gmtOffset");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }

}