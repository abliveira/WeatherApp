package com.abliveira.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.abliveira.weatherapp.service.NotificationService;

public class MainActivity extends AppCompatActivity {

    TextView cityNameTextView, weatherDescriptionTextView, currTempTextView, minTempTextView, maxTempTextview, windSpeedTextView, humidityTextView;
    EditText locationEditText;
    Button searchButton;
    LinearLayout widgetsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityNameTextView = (TextView) findViewById(R.id.cityNameTextView);
        weatherDescriptionTextView = (TextView) findViewById(R.id.weatherDescriptionTextView);
        currTempTextView = (TextView) findViewById(R.id.currTempTextView);
        minTempTextView = (TextView) findViewById(R.id.minTempTextView);
        maxTempTextview = (TextView) findViewById(R.id.maxTempTextview);
        windSpeedTextView = (TextView) findViewById(R.id.windSpeedTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        locationEditText = (EditText) findViewById(R.id.locationEditText);
        searchButton = (Button) findViewById(R.id.searchButton);
        widgetsContainer = findViewById(R.id.widgetsContainer);

        initializeSettings();

        // Start the NotificationService
        startService(new Intent(this, NotificationService.class));
    }

    private void initializeSettings(){
        ContentResolver resolver = getContentResolver();

        String[] projection = {
                SettingsDbHelper.COLUMN_ID,
                SettingsDbHelper.COLUMN_KEY,
                SettingsDbHelper.COLUMN_VALUE
        };

        // Initialize with default values
        String unitSystemValue = getString(R.string.label_metric);
        String languageValue = getString(R.string.label_english);
        String notificationIntervalValue = getString(R.string.label_disabled);

        // Check and insert settings
        saveSettingIfEmpty(resolver, "unitsystem", unitSystemValue, projection);
        saveSettingIfEmpty(resolver, "language", languageValue, projection);
        saveSettingIfEmpty(resolver, "notificationInterval", notificationIntervalValue, projection);

        // Query and display all settings
        Cursor cursor = resolver.query(
                SettingsProvider.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_menu:
                startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
