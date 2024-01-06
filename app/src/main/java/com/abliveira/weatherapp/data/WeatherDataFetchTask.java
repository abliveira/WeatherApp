package com.abliveira.weatherapp.data;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class WeatherDataFetchTask extends AsyncTask<String, Void, String> {

    private Callback callback;

    // Callback interface
    public interface Callback {
        void onDownloadComplete();
    }

    // Setter for the callback
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... urls) {

        // This method is called on a background thread, so we can do
        // network operations here.

        // Create a string to store the result of the HTTP request.
        String result = "";

        // Create a URL object for the specified URL.
        URL url;
        try {
            url = new URL(urls[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        // Create a HttpURLConnection object to connect to the URL.
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Set the request method to GET.
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }

        // Connect to the URL.
        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Get the input stream from the connection.
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Create an InputStreamReader object to read the input stream.
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        // Create a char to store the current character being read.
        int data;

        // Read the input stream character by character.
        try {
            while ((data = inputStreamReader.read()) != -1) {
                // Convert the character to a string.
                char current = (char) data;

                // Add the string to the result string.
                result += current;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Return the result string.
        return result;
    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);

        // Get the WeatherData instance
        WeatherData weatherData = WeatherData.getInstance();

        // Accessing methods or properties
        boolean ret = weatherData.parseWeather(s);

        // Notify the callback that the download is complete
        if (ret & callback != null) {
            callback.onDownloadComplete();
        }
    }
}