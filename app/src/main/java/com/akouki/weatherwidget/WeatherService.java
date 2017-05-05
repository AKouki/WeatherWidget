package com.akouki.weatherwidget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.akouki.weatherwidget.helpers.JSONParser;
import com.akouki.weatherwidget.helpers.PrefsManager;
import com.akouki.weatherwidget.models.Weather;
import com.akouki.weatherwidget.tasks.IWeatherFetchCallback;
import com.akouki.weatherwidget.tasks.WeatherFetchTask;

import org.json.JSONException;

import java.util.ArrayList;

public class WeatherService extends Service implements IWeatherFetchCallback {

    int appWidgetId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Fetch weather
        appWidgetId = intent.getExtras().getInt("appWidgetId");
        int locationId = PrefsManager.loadConfig(getApplicationContext(), appWidgetId).getLocation();
        WeatherFetchTask weatherFetchTask = new WeatherFetchTask(this, locationId);
        weatherFetchTask.execute();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void OnTaskCompleted(String jsonData) {
        try {
            // Parse Weather Data
            ArrayList<Weather> weatherData = JSONParser.getWeather(jsonData);

            // Save weather data and last run to the Preferences
            PrefsManager.save5DWeather(getApplicationContext(), weatherData, appWidgetId);
            PrefsManager.saveLastRun(getApplicationContext(), appWidgetId, System.currentTimeMillis());

            // Force widget update
            Intent intent = new Intent(getApplicationContext(), WeatherWidget.class);
            intent.setAction(WeatherWidget.ACTION_UPDATE_WEATHER);
            sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        stopSelf();
    }

    @Override
    public void OnTaskFailed(String reason) {
        Toast.makeText(this, "Weather Service failed!", Toast.LENGTH_SHORT).show();
        stopSelf();
    }
}
