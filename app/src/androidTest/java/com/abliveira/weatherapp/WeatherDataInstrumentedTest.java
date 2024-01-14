package com.abliveira.weatherapp;

import static org.junit.Assert.assertEquals;

import com.abliveira.weatherapp.data.WeatherData;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class WeatherDataInstrumentedTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testWeatherDataParsing() {
        // Mock JSON data for testing
        String jsonData = "{" +
                "\"coord\":{\"lon\":126.9778,\"lat\":37.5683}," +
                "\"weather\":[{" +
                "\"id\":501," +
                "\"main\":\"Rain\"," +
                "\"description\":\"moderate rain\"," +
                "\"icon\":\"10d\"" +
                "}]," +
                "\"base\":\"stations\"," +
                "\"main\":{" +
                "\"temp\":1.36," +
                "\"feels_like\":-1.94," +
                "\"temp_min\":-1.34," +
                "\"temp_max\":4.69," +
                "\"pressure\":1019," +
                "\"humidity\":84" +
                "}," +
                "\"visibility\":4000," +
                "\"wind\":{" +
                "\"speed\":3.09," +
                "\"deg\":10" +
                "}," +
                "\"rain\":{\"1h\":2.05}," +
                "\"clouds\":{\"all\":100}," +
                "\"dt\":1705207709," +
                "\"sys\":{" +
                "\"type\":1," +
                "\"id\":8105," +
                "\"country\":\"KR\"," +
                "\"sunrise\":1705185975," +
                "\"sunset\":1705221291" +
                "}," +
                "\"timezone\":32400," +
                "\"id\":1835848," +
                "\"name\":\"Seoul\"," +
                "\"cod\":200" +
                "}";

        // Create the WeatherData instance and parse JSON data
        WeatherData weatherData = new WeatherData();
        weatherData.setUnitSystem("Metric");
        boolean parsingResult = weatherData.parseWeather(jsonData);

        // Assert parsing success
        assertTrue("Parsing failed", parsingResult);

        // Assert parsed results
        assertEquals("1.36 °C", weatherData.getCurrTemp());
        assertEquals("moderate rain", weatherData.getWeatherDescription());
        assertEquals("84 %", weatherData.getHumidity());
        assertEquals("-1.34 °C", weatherData.getMinTemp());
        assertEquals("4.69 °C", weatherData.getMaxTemp());
        assertEquals("3.09 m/s", weatherData.getWindSpeed());
        assertEquals("Seoul", weatherData.getCity());

        // Set additional values and assert them
        weatherData.setCurrTemp("25.5 °C");
        weatherData.setWeatherDescription("clear sky");
        weatherData.setHumidity("80 %");
        weatherData.setMinTemp("20.0 °C");
        weatherData.setMaxTemp("30.0 °C");
        weatherData.setWindSpeed("5.0 m/s");

        // Assert additional results
        assertEquals("25.5 °C", weatherData.getCurrTemp());
        assertEquals("clear sky", weatherData.getWeatherDescription());
        assertEquals("80 %", weatherData.getHumidity());
        assertEquals("20.0 °C", weatherData.getMinTemp());
        assertEquals("30.0 °C", weatherData.getMaxTemp());
        assertEquals("5.0 m/s", weatherData.getWindSpeed());
    }
}
