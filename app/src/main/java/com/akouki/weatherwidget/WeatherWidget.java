package com.akouki.weatherwidget;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.akouki.weatherwidget.helpers.PrefsManager;
import com.akouki.weatherwidget.helpers.Utils;
import com.akouki.weatherwidget.models.Weather;
import com.akouki.weatherwidget.models.WidgetConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.ALARM_SERVICE;

public class WeatherWidget extends AppWidgetProvider {

    private static final long WEATHER_UPDATE_INTERVAL = 30 * 60 * 1000; // 30 Minutes
    private static final long CLOCK_UPDATE_INTERVAL = 60000; // 1 Minute
    public static final String ACTION_UPDATE_WEATHER = "com.akouki.weatherwidget.AUTO_UPDATE";
    public static final String ACTION_UPDATE_CLOCK = "com.akouki.weatherwidget.AUTO_UPDATE_CLOCK";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName appWidgetComponentName = new ComponentName(context, WeatherWidget.class);
        int[] appIds = appWidgetManager.getAppWidgetIds(appWidgetComponentName);

        if (action.equalsIgnoreCase(WeatherWidget.ACTION_UPDATE_WEATHER)) {
            if (appIds != null && appIds.length > 0)
                onUpdate(context, appWidgetManager, appIds);
        } else if (action.equalsIgnoreCase(WeatherWidget.ACTION_UPDATE_CLOCK)) {
            for (int id : appIds) {
                updateClock(context, appWidgetManager, id);
            }
        } else {
            super.onReceive(context, intent);
        }
    }

    static void updateClock(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Get Time
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM\nhh:mm a", Locale.getDefault());
        sdf.setTimeZone(timeZone);
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.add(Calendar.SECOND, PrefsManager.loadConfig(context, appWidgetId).getTimeRawOffset());

        // Update View
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.tvDay1, sdf.format(calendar.getTime()));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Load Widget Configuration & Weather Data from SharedPreferences
        WidgetConfiguration wc = PrefsManager.loadConfig(context, appWidgetId);
        ArrayList<Weather> weatherData = PrefsManager.load5DWeather(context, appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        if (weatherData != null && weatherData.size() > 0) {

            views.setTextViewText(R.id.tvCityName, wc.getLocationName());

            // Days
            updateClock(context, appWidgetManager, appWidgetId);
            views.setTextViewText(R.id.tvDay2, getDayOfTheWeekFromNow(1, wc.getTimeRawOffset()));
            views.setTextViewText(R.id.tvDay3, getDayOfTheWeekFromNow(2, wc.getTimeRawOffset()));
            views.setTextViewText(R.id.tvDay4, getDayOfTheWeekFromNow(3, wc.getTimeRawOffset()));
            views.setTextViewText(R.id.tvDay5, getDayOfTheWeekFromNow(4, wc.getTimeRawOffset()));

            // Temperatures
            views.setTextViewText(R.id.tvTemp1, getTemp(weatherData.get(0), wc.getDisplayUnit()));
            views.setTextViewText(R.id.tvTemp2, getTemp2(weatherData.get(1), wc.getDisplayUnit()));
            views.setTextViewText(R.id.tvTemp3, getTemp2(weatherData.get(2), wc.getDisplayUnit()));
            views.setTextViewText(R.id.tvTemp4, getTemp2(weatherData.get(3), wc.getDisplayUnit()));
            views.setTextViewText(R.id.tvTemp5, getTemp2(weatherData.get(4), wc.getDisplayUnit()));

            // Images
            views.setInt(R.id.widget_container, "setBackgroundResource", GetCurrentWeatherBackground(wc.getTimeRawOffset()));
            views.setImageViewResource(R.id.imageView1, GetDrawable(weatherData.get(0), wc.getTimeRawOffset()));
            views.setImageViewResource(R.id.imageView2, GetDrawable(weatherData.get(1), wc.getTimeRawOffset()));
            views.setImageViewResource(R.id.imageView3, GetDrawable(weatherData.get(2), wc.getTimeRawOffset()));
            views.setImageViewResource(R.id.imageView4, GetDrawable(weatherData.get(3), wc.getTimeRawOffset()));
            views.setImageViewResource(R.id.imageView5, GetDrawable(weatherData.get(4), wc.getTimeRawOffset()));
        }

        if (Utils.IsNetworkAvailable(context)) {

            long lastRun = PrefsManager.loadLastRun(context, appWidgetId);
            double elapsedTime = System.currentTimeMillis() - lastRun;

            // Is time for updating weather?
            if (elapsedTime > WEATHER_UPDATE_INTERVAL && wc.getLocation() != 0) {
                // Safe check
                boolean serviceIsRunning = false;
                ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (WeatherService.class.getName().equals(service.service.getClassName())) {
                        serviceIsRunning = true;
                    }
                }
                if (!serviceIsRunning) {
                    Intent intent = new Intent(context, WeatherService.class);
                    intent.putExtra("appWidgetId", appWidgetId);
                    context.startService(intent);
                }
            }
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            PrefsManager.clearConfigs(context, appWidgetId);
            PrefsManager.clearWeather(context, appWidgetId);
            PrefsManager.clearLastRun(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        startAlarm(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        stopAlarm(context);
    }

    private void startAlarm(Context context) {
        // Weather
        Intent intentWeather = new Intent(context, WeatherWidget.class);
        intentWeather.setAction(WeatherWidget.ACTION_UPDATE_WEATHER);
        PendingIntent piWeather = PendingIntent.getBroadcast(context, 0, intentWeather, 0);

        // Clock
        Intent intentClock = new Intent(context, WeatherWidget.class);
        intentClock.setAction(ACTION_UPDATE_CLOCK);
        PendingIntent piClock = PendingIntent.getBroadcast(context, 1, intentClock, 0);

        // Setup alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(piWeather);
        alarmManager.cancel(piClock);
        alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), WEATHER_UPDATE_INTERVAL, piWeather);
        alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), CLOCK_UPDATE_INTERVAL, piClock);
    }

    private void stopAlarm(Context context) {
        Intent alarmIntent = new Intent(WeatherWidget.ACTION_UPDATE_WEATHER);
        PendingIntent piWeather = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent piClock = PendingIntent.getBroadcast(context, 1, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(piWeather);
        alarmManager.cancel(piClock);
    }

    private static int GetCurrentWeatherBackground(int gmtOffset) {
        if (isDay(gmtOffset))
            return R.drawable.skyd;

        return R.drawable.skyn;
    }

    private static int GetDrawable(Weather weather, int gmtOffset) {
        switch (weather.getCondition().toLowerCase()) {
            case "clear sky":
            case "clear":
                if (isDay(gmtOffset))
                    return R.drawable.w01d;
                return R.drawable.w01n;
            case "few clouds":
                if (isDay(gmtOffset))
                    return R.drawable.w02d;
                return R.drawable.w02n;
            case "scattered clouds":
            case "broken clouds":
            case "clouds":
                return R.drawable.w03d;
            case "rain":
            case "light rain":
                if (isDay(gmtOffset))
                    return R.drawable.w10d;
                return R.drawable.w10n;
            case "shower rain":
                return R.drawable.w09d;
            case "thunderstorm":
                return R.drawable.w11d;
            case "snow":
                return R.drawable.w13d;
            case "mist":
                return R.drawable.w50d;
            default:
                return R.drawable.w03d;
        }
    }

    private static boolean isDay(int gmtOffset) {
        Calendar now = Utils.getTime(gmtOffset);
        int currHour = now.get(Calendar.HOUR_OF_DAY);
        if (currHour > 6 && currHour < 20) // Not the best idea but okay...
            return true;
        return false;
    }

    private static CharSequence getTemp(Weather weather, String displayUnit) {
        return Utils.unitConvert(weather.getTemp(), displayUnit) + "\u00B0";
    }

    private static CharSequence getTemp2(Weather weather, String displayUnit) {
        return Utils.unitConvert(weather.getMinTemp(), displayUnit) +
                "/" +
                Utils.unitConvert(weather.getMaxTemp(), displayUnit) +
                "\u00B0";
    }

    private static String getDayOfTheWeekFromNow(int addDay, int gmtOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.DATE, addDay);
        calendar.add(Calendar.SECOND, gmtOffset);
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
        return dayFormat.format(calendar.getTime());
    }
}