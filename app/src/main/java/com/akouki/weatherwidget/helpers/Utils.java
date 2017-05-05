package com.akouki.weatherwidget.helpers;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.Calendar;
import java.util.TimeZone;

public class Utils {

    public static boolean IsNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            return cm.getActiveNetworkInfo() != null &&
                    cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception ex) {
        }
        return false;
    }

    public static int unitConvert(float kelvin, String unit) {
        if (unit.toLowerCase().equals("fahrenheit"))
            return (int) (kelvin * 9 / 5 - 459.67);

        return ((int) (kelvin - 273.15));
    }

    public static Calendar getTime(int gmtOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.SECOND, gmtOffset);
        return calendar;
    }

}
