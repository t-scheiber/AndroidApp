package com.example.bsaweatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String RESULT_STRING = "result";
    private final Random mRandom = new Random();
    private ListAdapter mAdapter;
    private final List<WeatherData> weatherData = new LinkedList<>();
    private String language;
    private final String baseURL = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private String city = "Wien";
    private boolean use_metric_system = true;

    @Override
    protected void onResume() {
        super.onResume();
        loadWebResult();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.settings_city_key), "Wien");
        editor.putBoolean(getString(R.string.settings_use_metric_system_key), true);
        editor.apply();

        RecyclerView recyclerView = findViewById(R.id.rv_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ListAdapter(weatherData);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new ListAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(WeatherData item) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.VALUE_KEY, item);
                startActivity(intent);
            }
        });

        loadWebResult();
    }

    private void getLang() {
        String tmp = Locale.getDefault().getLanguage();
        String lang = "en";
        if(tmp.equals("en") || tmp.equals("de")) {
            lang = tmp;
            language = lang;
        }
    }

    private void loadWebResult() {
        getLang();
        try {
            String appid = "&appid=231b88bacecc85e7d47891a53667356f";
            String system = "metric";
            if(!use_metric_system)
                system = "imperial";

            String sUrl = baseURL + city + "&units=" + system + appid + "&lang=" + language;
            Log.d(LOG_TAG, sUrl);
            URL url = new URL(sUrl);
            new LoadWebContentTask().execute(url);
        } catch (MalformedURLException e) {
//            outputTextView.setText(R.string.url_error);
            Log.e(LOG_TAG, "URL Error", e);
        }
    }

    private String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setConnectTimeout(5000);
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput)
                return scanner.next();
            else
                return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.w(LOG_TAG, key);
        if (key.equals(getString(R.string.settings_use_metric_system_key))) {
            this.use_metric_system = sharedPreferences.getBoolean(key, true);
            WeatherData.setUseMetricSystem(this.use_metric_system);
            DetailActivity.use_metric_system = this.use_metric_system;
            loadWebResult();
        }
        if(key.equals(getString(R.string.settings_city_key))) {
            this.city = sharedPreferences.getString(key, "Wien");
            loadWebResult();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (itemId == R.id.refresh) {
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            Log.d(LOG_TAG, "Reloading");
            return true;
        }
        return super.onOptionsItemSelected(item);


    }

    private class LoadWebContentTask extends AsyncTask<URL, Void, List<WeatherData>> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected List<WeatherData> doInBackground(URL... urls) {
            URL url = urls[0];
            String resultString = "";
            try {
                resultString = getResponseFromHttpUrl(url);
            } catch (IOException e) {
                Log.e(LOG_TAG,  "IO Error", e);
            }
            if (resultString != null && resultString.length() != 0) {
                try {
                    List<WeatherData> weatherDataAll = new LinkedList<>();
                    JSONObject jsonRoot = new JSONObject(resultString);
                    JSONArray items = jsonRoot.getJSONArray("list");
                    for (int i = 0; i < items.length(); i++){
                        JSONObject item = items.getJSONObject(i);
                        int dt = item.getInt("dt");
                        String temp;
                        String speedUnit;
                        boolean unitSystem = getResources().getBoolean(R.bool.useMetricSystem);

                            temp = item.getJSONObject("main").getString("temp");


                        String icon = item.getJSONArray("weather").getJSONObject(0).getString("icon");
                        String condition = item.getJSONArray("weather").getJSONObject(0).getString("description");
                        String pressure = item.getJSONObject("main").getString("pressure") + "hPa";
                        String humidity = item.getJSONObject("main").getString("humidity") + "%";
                        String cloudCover = item.getJSONObject("clouds").getString("all") + "%";
                        if(unitSystem)
                            speedUnit = getResources().getString(R.string.speedMetric);
                        else
                            speedUnit = getResources().getString(R.string.speedImperial);
                        String windSpeed = item.getJSONObject("wind").getString("speed") + " " + speedUnit;
                        String windDirection = item.getJSONObject("wind").getString("deg") + "°";

                        String rain = getResources().getString(R.string.noRain);
                        String snow = getResources().getString(R.string.noSnow);

                        try {
                            rain = item.getJSONObject("rain").getString("rain.3h") + "mm";
                        }
                        catch(Exception ex){
                            Log.e(LOG_TAG, "No Rain", ex);
                        }
                        try {
                            snow = item.getJSONObject("snow").getString("snow.3h") + "mm";
                        }
                        catch(Exception ey){
                            Log.e(LOG_TAG, "No Snow", ey);
                        }
                        WeatherData weatherData = new WeatherData(dt, icon, temp, condition,
                                pressure, humidity, cloudCover, windSpeed, windDirection, rain, snow, language);

                        weatherDataAll.add(weatherData);
                    }
                    return weatherDataAll;
                } catch (JSONException ex) {
                    Log.e(LOG_TAG, "JSON Error", ex);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<WeatherData> result) {
            super.onPostExecute(result);
            if (result != null && result.size() > 0) {
                weatherData.clear();
                weatherData.addAll(result);
                mAdapter.swapData(result);
            }
            else {
                Log.e(LOG_TAG,  "Result empty");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}