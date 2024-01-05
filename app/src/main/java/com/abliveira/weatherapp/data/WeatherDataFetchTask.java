package com.abliveira.weatherapp.data;

import android.os.AsyncTask;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherDataFetchTask extends AsyncTask<String, Void, String> {

    private Callback callback;

    public interface Callback {
        void onDownloadComplete();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... urls) {

        String result = "";
        URL url;
        HttpURLConnection connection = null;

        try {
            url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            int data = inputStreamReader.read();
            while (data != -1){
                char current = (char) data;
                result += current;
                data = inputStreamReader.read();
            } return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);

        WeatherData weatherData = WeatherData.getInstance();

        // Accessing methods or properties
        weatherData.parseWeather(s);

        // Notify the callback that the download is complete
        if (callback != null) {
            callback.onDownloadComplete();
        }
    }
}