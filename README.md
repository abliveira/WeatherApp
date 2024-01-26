# Weather App

The Weather App is an Android application designed to provide users with real-time weather forecasts on their Android devices. Users can choose a city by name and receive weather conditions, including the current weather condition, temperature, the maximum and minimum temperatures, wind speed, and humidity. The Weather App integrates a notification service to deliver regular updates on weather conditions. In addition, users can customize the app settings by choosing their preferred temperature units, language, and notification options.

## OpenWeather API

The OpenWeather's One Call API (https://openweathermap.org/data/2.5/weather) provides weather data through HTTP requests, in JSON format. The request must contain a location and the API key, along with optional parameters such as language and units. The API response contains essential weather data in short-term and long-term forecasts, such as temperature, humidity, wind speed, and atmospheric conditions. The JSON data must be parsed to extract the weather data that will be presented to the user.

## Design Considerations

It's important to note that this app is a learning exercise for different Android functionalities within the context of a weather application. The architectural design employed is not suitable for real-world use.

## Getting Started

To run the Weather App locally, follow these steps:
1. Clone the repository.
2. Add your OpenWeather API key to the app. In the `local.properties` file, add the line `api.key=<your_key_here>`.
3. Build and run the app on your Android device.

## Credits

This app was developed by abliveira. Weather data is provided by OpenWeather API.