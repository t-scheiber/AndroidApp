package com.example.bsaweatherapp;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class GetDrawableResource {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static int get(WeatherData data, String name) {
        Map<String, Integer> icons = new HashMap<>();
        icons.put("01d", R.drawable._01d);
        icons.put("02d", R.drawable._02d);
        icons.put("03d", R.drawable._03d);
        icons.put("04d", R.drawable._04d);
        icons.put("09d", R.drawable._09d);
        icons.put("10d", R.drawable._10d);
        icons.put("11d", R.drawable._11d);
        icons.put("13d", R.drawable._13d);
        icons.put("50d", R.drawable._50d);
        icons.put("01n", R.drawable._01n);
        icons.put("02n", R.drawable._02n);
        icons.put("03n", R.drawable._03n);
        icons.put("04n", R.drawable._04n);
        icons.put("09n", R.drawable._09n);
        icons.put("10n", R.drawable._10n);
        icons.put("11n", R.drawable._11n);
        icons.put("13n", R.drawable._13n);
        icons.put("50n", R.drawable._50n);
        //TODO: ICONS downloaden und einfügen https://openweathermap.org/weather-conditions
        int id = R.drawable._01n;
        try {
            id = icons.get(data.getIcon());
        }
        catch (NullPointerException e){
            Log.e(LOG_TAG, "Error");
        }
        return id;
    }
}
