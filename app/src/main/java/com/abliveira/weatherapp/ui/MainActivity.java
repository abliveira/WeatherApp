package com.abliveira.weatherapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abliveira.weatherapp.BuildConfig;
import com.abliveira.weatherapp.R;
import com.abliveira.weatherapp.data.WeatherDataFetchTask;
import com.abliveira.weatherapp.data.SettingsDbHelper;
import com.abliveira.weatherapp.data.SettingsProvider;
import com.abliveira.weatherapp.service.NotificationService;
import com.abliveira.weatherapp.data.WeatherData;

public class MainActivity extends AppCompatActivity {

    // Declare the UI variables that will be used to display the weather information
    TextView cityNameTextView, weatherDescriptionTextView, currTempTextView, minTempTextView, maxTempTextview, windSpeedTextView, humidityTextView;
    EditText locationEditText;
    LinearLayout widgetsContainer;

    // Get the weather data from the WeatherData singleton.
    WeatherData weatherData = WeatherData.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the references to the views in the layout.
        cityNameTextView = (TextView) findViewById(R.id.cityNameTextView);
        weatherDescriptionTextView = (TextView) findViewById(R.id.weatherDescriptionTextView);
        currTempTextView = (TextView) findViewById(R.id.currTempTextView);
        minTempTextView = (TextView) findViewById(R.id.minTempTextView);
        maxTempTextview = (TextView) findViewById(R.id.maxTempTextview);
        windSpeedTextView = (TextView) findViewById(R.id.windSpeedTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        locationEditText = (EditText) findViewById(R.id.locationEditText);
        widgetsContainer = findViewById(R.id.widgetsContainer);

        initializeSettings();

        startService(new Intent(this, NotificationService.class));
    }

    private void initializeSettings() {
        // Get the ContentResolver object.
        ContentResolver resolver = getContentResolver();

        // Define the columns to be retrieved from the database.
        String[] projection = {
                SettingsDbHelper.COLUMN_ID,
                SettingsDbHelper.COLUMN_KEY,
                SettingsDbHelper.COLUMN_VALUE
        };

        // Check if the settings are empty and insert them with default values if they are.
        saveSettingIfEmpty(resolver, SettingsProvider.UNIT_SYSTEM_KEY, getString(R.string.label_metric), projection);
        saveSettingIfEmpty(resolver, SettingsProvider.LANGUAGE_KEY, getString(R.string.label_english), projection);
        saveSettingIfEmpty(resolver, SettingsProvider.NOTIFICATION_INTERVAL_KEY, getString(R.string.label_disabled), projection);

        // Query the database and display all settings.
        Cursor cursor = resolver.query(
                SettingsProvider.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            // Iterate over the cursor and display each setting.
            do {
                long id = cursor.getLong(cursor.getColumnIndex(SettingsDbHelper.COLUMN_ID));
                String key = cursor.getString(cursor.getColumnIndex(SettingsDbHelper.COLUMN_KEY));
                String value = cursor.getString(cursor.getColumnIndex(SettingsDbHelper.COLUMN_VALUE));

                Log.d("WA_DEBUG", "Setting ID: " + id + ", Key: " + key + ", Value: " + value);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.d("WA_DEBUG", "No settings found");
        }
    }

    private void saveSettingIfEmpty(ContentResolver resolver, String key, String value, String[] projection) {
        // Check if the setting already exists
        Uri uri = Uri.withAppendedPath(SettingsProvider.CONTENT_URI, key);
        Cursor cursor = resolver.query(uri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Setting exists, check if the value is empty before updating
            String existingValue = cursor.getString(cursor.getColumnIndex(SettingsDbHelper.COLUMN_VALUE));
            if (existingValue == null || existingValue.isEmpty()) {
                ContentValues updateValues = new ContentValues();
                updateValues.put(SettingsDbHelper.COLUMN_VALUE, value);
                resolver.update(uri, updateValues, null, null);
            }
            cursor.close();
        } else {
            // Setting doesn't exist, insert a new one
            ContentValues insertValues = new ContentValues();
            insertValues.put(SettingsDbHelper.COLUMN_KEY, key);
            insertValues.put(SettingsDbHelper.COLUMN_VALUE, value);
            resolver.insert(SettingsProvider.CONTENT_URI, insertValues);
        }
    }

    public void getWeatherFromAPI(View view) {
        hideKeyboard();
        WeatherDataFetchTask task = new WeatherDataFetchTask();
        task.setCallback(new WeatherDataFetchTask.Callback() {
            @Override
            public void onTaskComplete(boolean isSuccessful) {
                if (!isSuccessful) {
                    Toast.makeText(MainActivity.this, getString(R.string.fetch_fail_message), Toast.LENGTH_SHORT).show();
                } else {
                    updateWeatherUI();
                }
            }
        });
        // Execute the task with the API URL, API key, and location.
        String url = getString(R.string.api_url) + BuildConfig.API_KEY + "&q=" + locationEditText.getText().toString() + getConfigurationString();
        Log.d("WA_DEBUG", "URL: " + url);
        task.execute(url);
    }

    private void updateWeatherUI() {
        // Configure the UI elements according to the weather data.
        cityNameTextView.setText(weatherData.getCity());
        weatherDescriptionTextView.setText(weatherData.getWeatherDescription());
        currTempTextView.setText(weatherData.getCurrTemp());
        maxTempTextview.setText(weatherData.getMaxTemp());
        minTempTextView.setText(weatherData.getMinTemp());
        windSpeedTextView.setText(String.valueOf(weatherData.getWindSpeed()));
        humidityTextView.setText(weatherData.getHumidity());
        widgetsContainer.setVisibility(View.VISIBLE);
    }

    private String getConfigurationString() {

        // Get the unit system setting from the SettingsProvider.
        String unitSystemString = SettingsProvider.readSetting(getContentResolver(), SettingsProvider.UNIT_SYSTEM_KEY);
        Log.d("WA_DEBUG", "Unit System: " + unitSystemString);

        // Get the language setting from the SettingsProvider.
        String languageString = SettingsProvider.readSetting(getContentResolver(), SettingsProvider.LANGUAGE_KEY);
        Log.d("WA_DEBUG", "Language: " + languageString);

        // Set the unit system on the weather data object.
        weatherData.setUnitSystem(unitSystemString);

        // Convert the unit system string to the format required by the API.
        if (unitSystemString.equals(getString(R.string.label_metric))) {
            unitSystemString = "&units=metric";
        } else if (unitSystemString.equals(getString(R.string.label_imperial))) {
            unitSystemString = "&units=imperial";
        }

        // Convert the language string to the format required by the API.
        if (languageString.equals(getString(R.string.label_english))) {
            languageString = "&lang=en";
        } else if (languageString.equals(getString(R.string.label_portuguese))) {
            languageString = "&lang=pt";
        } else if (languageString.equals(getString(R.string.label_spanish))) {
            languageString = "&lang=es";
        }

        Log.d("WA_DEBUG", "Unit System: " + unitSystemString + ", Language: " + languageString);

        // Return the combined unit system and language string.
        return (unitSystemString + languageString);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the settings menu.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item selections.
        switch (item.getItemId()) {
            case R.id.settings_menu:
                startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSettingsActivity() {
        // Create an intent and start the settings activity.
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
