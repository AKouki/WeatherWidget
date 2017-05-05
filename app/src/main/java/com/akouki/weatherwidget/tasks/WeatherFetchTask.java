package com.akouki.weatherwidget.tasks;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetchTask extends AsyncTask<Void, Void, String> {

    private String API_KEY = "YOUR_API_KEY";

    private int locationId = 0;
    private IWeatherFetchCallback iWeatherFetchCallback;

    public WeatherFetchTask(IWeatherFetchCallback iwfc, int locationId) {
        this.iWeatherFetchCallback = iwfc;
        this.locationId = locationId;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?id=" + locationId + "&mode=json&appid=" + API_KEY);
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

            return result.toString();

        } catch (IOException e) {
            iWeatherFetchCallback.OnTaskFailed(e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        iWeatherFetchCallback.OnTaskCompleted(jsonData);
    }
}
