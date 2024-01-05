package com.abliveira.weatherapp.data;

import org.json.JSONObject;

public class WeatherData {

    private String currTemp;
    private String maxTemp;
    private String minTemp;
    private String humidity;
    private String windSpeed;
    private String weatherDescription;
    private String city;

    private static class SingletonHolder {
        private static final WeatherData INSTANCE = new WeatherData();
    }

    public static WeatherData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public String getCurrTemp() {
        return currTemp;
    }

    public void setCurrTemp(String currTemp) {
        this.currTemp = currTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public void parseWeather(String s) {

        try {

            JSONObject jsonObject = new JSONObject(s);
            JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject main = jsonObject.getJSONObject("main");
            JSONObject wind = jsonObject.getJSONObject("wind");
            currTemp = main.getString("temp") + " °C";
            minTemp = main.getString("temp_min") + " °C";
            maxTemp = main.getString("temp_max") + " °C";
            humidity = main.getString("humidity") + " %";
            windSpeed = wind.getString("speed") + " m/s";
            weatherDescription = weather.getString("description");
            city = jsonObject.getString("name");

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
