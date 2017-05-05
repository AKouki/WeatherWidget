package com.akouki.weatherwidget.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.akouki.weatherwidget.models.Weather;
import com.akouki.weatherwidget.models.WidgetConfiguration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PrefsManager {

    private static String PREFS_CONFIG_NAME = "weatherwidget.config";
    private static String PREFS_5DWEATHER_NAME = "weatherwidget.5dweather";
    private static String PREFS_LAST_RUN_NAME = "weatherwidget.lastrun";

    public static void save5DWeather(Context context, ArrayList<Weather> weatherData, int appWidgetId) {
        String json = new Gson().toJson(weatherData);
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_5DWEATHER_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("jsonWeatherData" + appWidgetId, json);
        editor.commit();
    }

    public static ArrayList<Weather> load5DWeather(Context context, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(PREFS_5DWEATHER_NAME, Context.MODE_PRIVATE);

        String json = prefs.getString("jsonWeatherData" + appWidgetId, null);
        Type type = new TypeToken<ArrayList<Weather>>() {
        }.getType();

        ArrayList<Weather> weatherList = new Gson().fromJson(json, type);
        return weatherList;
    }

    public static void clearWeather(Context context, int appWidgetId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_5DWEATHER_NAME, Context.MODE_PRIVATE).edit();
        editor.remove("jsonWeatherData" + appWidgetId);
        editor.commit();
    }

    public static void saveConfigs(Context context, int appWidgetId, int locationId, String locationName, String displayUnit, int gmtOffset) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_CONFIG_NAME, 0).edit();
        editor.putInt("locationId" + appWidgetId, locationId);
        editor.putString("locationName" + appWidgetId, locationName);
        editor.putString("displayUnit" + appWidgetId, displayUnit);
        editor.putInt("gmtOffset" + appWidgetId, gmtOffset);
        editor.commit();

    }

    public static WidgetConfiguration loadConfig(Context context, int appWidgetId) {
        WidgetConfiguration cfg = new WidgetConfiguration();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_CONFIG_NAME, 0);
        cfg.setLocation(prefs.getInt("locationId" + appWidgetId, 0));
        cfg.setLocationName(prefs.getString("locationName" + appWidgetId, "Unset"));
        cfg.setDisplayUnit(prefs.getString("displayUnit" + appWidgetId, "celsius"));
        cfg.setTimeRawOffset(prefs.getInt("gmtOffset" + appWidgetId, 0));

        return cfg;
    }

    public static void clearConfigs(Context context, int appWidgetId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_CONFIG_NAME, Context.MODE_PRIVATE).edit();
        editor.remove("locationId" + appWidgetId);
        editor.remove("locationName" + appWidgetId);
        editor.remove("displayUnit" + appWidgetId);
        editor.commit();
    }

    public static void saveLastRun(Context context, int appWidgetId, long currentTimeMillis) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_LAST_RUN_NAME, 0).edit();
        editor.putLong("lastrun" + appWidgetId, currentTimeMillis);
        editor.commit();
    }

    public static long loadLastRun(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_LAST_RUN_NAME, 0);
        return prefs.getLong("lastrun" + appWidgetId, 0);
    }

    public static void clearLastRun(Context context, int appWidgetId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_LAST_RUN_NAME, Context.MODE_PRIVATE).edit();
        editor.remove("lastrun" + appWidgetId);
        editor.commit();
    }
}
