package com.akouki.weatherwidget.helpers;

import com.akouki.weatherwidget.models.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {

    // Get weather data and return 5Day weather
    public static ArrayList<Weather> getWeather(String jsonData) throws JSONException {

        ArrayList<Weather> weatherList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("list");

        for (int i = 0; i < jsonArray.length(); i++) {

            Weather weather = new Weather();

            // Temperature data
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            JSONObject mainObject = jsonObject2.getJSONObject("main");
            weather.setTemp(getFloat("temp", mainObject));
            weather.setMinTemp(getFloat("temp_min", mainObject));
            weather.setMaxTemp(getFloat("temp_max", mainObject));
            weather.setDateTime(getString("dt_txt", jsonObject2).split(" ")[0]);
            // Condition data
            JSONArray array = jsonObject2.getJSONArray("weather");
            weather.setCondition(getString("description", array.getJSONObject(0)));

            weatherList.add(weather);
        }

        ArrayList<Weather> finalList = get5dWeather(weatherList);

        return finalList;
    }

    // Find min & max weather temperature for every day
    private static ArrayList<Weather> get5dWeather(ArrayList<Weather> weatherList) {
        ArrayList<Weather> finalList = new ArrayList<>();

        // 1. Find indexes where date changes
        ArrayList<Integer> indexes = new ArrayList<>();
        String date = weatherList.get(0).getDateTime();
        for (int i = 0; i < weatherList.size(); i++) {
            if (!weatherList.get(i).getDateTime().equals(date)) {
                indexes.add(i);
            }
            date = weatherList.get(i).getDateTime();
        }

        // 2. Now that we have indexes, we know where to search in 'weatherList' for specific day
        finalList.add(weatherList.get(0)); // Day 1 - Current Weather (Always first item in 'weatherList')
        finalList.add(GetMinMaxWeather(weatherList, indexes.get(0), indexes.get(1))); // Day 2
        finalList.add(GetMinMaxWeather(weatherList, indexes.get(1), indexes.get(2))); // Day 3
        finalList.add(GetMinMaxWeather(weatherList, indexes.get(2), indexes.get(3))); // Day 4
        finalList.add(GetMinMaxWeather(weatherList, indexes.get(3), weatherList.size())); // Day 5

        return finalList;
    }

    // Find min & max temperature for every day
    private static Weather GetMinMaxWeather(ArrayList<Weather> weatherList, Integer startIndex, Integer endIndex) {

        float min = weatherList.get(startIndex).getTemp();
        float max = weatherList.get(startIndex).getTemp();
        for (int i = startIndex; i < endIndex; i++) {
            if (min > weatherList.get(i).getTemp())
                min = weatherList.get(i).getTemp();

            if (max < weatherList.get(i).getTemp())
                max = weatherList.get(i).getTemp();
        }
        Weather w = weatherList.get(startIndex);
        w.setMinTemp(min);
        w.setMaxTemp(max);

        return w;
    }

    public static ArrayList<String> getLocations(String jsonData) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject subObject = jsonArray.getJSONObject(i);
                int id = subObject.getInt("id");
                String name = subObject.getString("name");
                String countryCode = subObject.getJSONObject("sys").getString("country");
                double lat = subObject.getJSONObject("coord").getDouble("lat");
                double lon = subObject.getJSONObject("coord").getDouble("lon");

                arrayList.add(id + "/" + name + "/" + countryCode + "/" + lat + "/" + lon);
            }
        } catch (JSONException e) {
        }

        return arrayList;
    }

    private static String getString(String name, JSONObject jsonObject) throws JSONException {
        return jsonObject.getString(name);
    }

    private static float getFloat(String name, JSONObject jsonObject) throws JSONException {
        return (float) jsonObject.getDouble(name);
    }

}
