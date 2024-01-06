package com.abliveira.weatherapp.data;

import org.json.JSONObject;

public class WeatherData {

    // Declare the variables.
    private String currTemp;
    private String maxTemp;
    private String minTemp;
    private String humidity;
    private String windSpeed;
    private String weatherDescription;
    private String city;

    // Singleton holder.
    private static class SingletonHolder {
        private static final WeatherData INSTANCE = new WeatherData();
    }

    // Get the singleton instance.
    public static WeatherData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // Setters.

    public void setCurrTemp(String currTemp) {
        this.currTemp = currTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Getters.

    public String getCurrTemp() {
        return currTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getCity() {
        return city;
    }

    // Parse the weather data.
    public boolean parseWeather(String s) {

        // Try to parse the data.
        try {
            // Create a JSONObject from the string.
            JSONObject jsonObject = new JSONObject(s);

            // Get the weather object.
            JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject main = jsonObject.getJSONObject("main");
            JSONObject wind = jsonObject.getJSONObject("wind");

            // Set the weather data.
            currTemp = main.getString("temp") + " °C";
            minTemp = main.getString("temp_min") + " °C";
            maxTemp = main.getString("temp_max") + " °C";
            humidity = main.getString("humidity") + " %";
            windSpeed = wind.getString("speed") + " m/s";
            weatherDescription = weather.getString("description");
            city = jsonObject.getString("name");
            return true;

        } catch (Exception e){
            // Print the stack trace if an exception occurs.
            e.printStackTrace();
            return false;
        }
    }
}
