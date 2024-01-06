package com.abliveira.weatherapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.abliveira.weatherapp.data.SettingsDbHelper;
import com.abliveira.weatherapp.provider.SettingsProvider;
import com.abliveira.weatherapp.service.NotificationService;

public class SettingsActivity extends AppCompatActivity {

    // Declare settings data.
    private String unitSystem;
    private String language;
    private String notificationInterval;

    // Declare UI elements.
    private RadioGroup unitSystemRadioGroup;
    private RadioGroup languageRadioGroup;
    private RadioGroup notificationIntervalRadioGroup;
    private Button saveButton;
    private Button cancelButton;
    private Button helpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        loadSettings();
        loadUI();

        // Set the onClickListener for the saveButton.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieve the selected radio button from the Unit System RadioGroup.
                int selectedRadioButtonId1 = unitSystemRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton1 = findViewById(selectedRadioButtonId1);
                unitSystem = radioButton1 != null ? radioButton1.getText().toString() : "No option selected";

                // Retrieve the selected radio button from the Language RadioGroup.
                int selectedRadioButtonId2 = languageRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton2 = findViewById(selectedRadioButtonId2);
                language = radioButton2 != null ? radioButton2.getText().toString() : "No option selected";

                // Retrieve the selected radio button from the Notification Interval RadioGroup.
                int selectedRadioButtonId3 = notificationIntervalRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton3 = findViewById(selectedRadioButtonId3);
                notificationInterval = radioButton3 != null ? radioButton3.getText().toString() : "No option selected";

                // Get the ContentResolver.
                ContentResolver resolver = getContentResolver();

                // Store the settings in the ContentResolver.
                storeSetting(resolver, SettingsProvider.UNIT_SYSTEM_KEY, unitSystem);
                storeSetting(resolver, SettingsProvider.LANGUAGE_KEY, language);
                storeSetting(resolver, SettingsProvider.NOTIFICATION_INTERVAL_KEY, notificationInterval);

                // Read and display the stored settings.

                unitSystem = readSetting(resolver, SettingsProvider.UNIT_SYSTEM_KEY);
                Log.d("WA_DEBUG", "Unit System: " + unitSystem);

                language = readSetting(resolver, SettingsProvider.LANGUAGE_KEY);
                Log.d("WA_DEBUG", "Language: " + language);

                notificationInterval = readSetting(resolver, SettingsProvider.NOTIFICATION_INTERVAL_KEY);
                Log.d("WA_DEBUG", "Notification Interval: " + notificationInterval);

                // Display a toast with the stored options.
                String message = "Unit System: " + unitSystem
                        + "\nLanguage: " + language
                        + "\nNotification Interval: " + notificationInterval;

                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

                // Update the app configurations.
                sendUpdateIntervalBroadcast(notificationInterval);

                // Close the activity and return to the main.
                finish();
            }
        });

        // Set the onClickListener for the cancelButton.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity and return to the main.
            }
        });

        // Set the onClickListener for the helpButton.
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog.
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Help");
                builder.setMessage("developed by Arthur Oliveira (abliveira)\n" +
                        "https://github.com/abliveira");

                // Set positive button.
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Show the AlertDialog.
                builder.show();
            }
        });
    }

    private void loadUI() {

        // Get the RadioGroup objects.
        unitSystemRadioGroup = findViewById(R.id.unitSystemRadioGroup);
        languageRadioGroup = findViewById(R.id.languageRadioGroup);
        notificationIntervalRadioGroup = findViewById(R.id.notificationIntervalRadioGroup);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        helpButton = findViewById(R.id.helpButton);

        // Get the current settings.
        String unitSystemValue = unitSystem;
        String languageValue = language;
        String intervalValue = notificationInterval;

        // Select the RadioButtons based on the current settings.
        selectRadioButtonByText(unitSystemRadioGroup, unitSystemValue);
        selectRadioButtonByText(languageRadioGroup, languageValue);
        selectRadioButtonByText(notificationIntervalRadioGroup, intervalValue);
    }

    private void selectRadioButtonByText(RadioGroup radioGroup, String selectedValue) {

        // Iterate over the RadioButtons in the RadioGroup.
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View view = radioGroup.getChildAt(i);
            if (view instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) view;
                if (selectedValue.equals(radioButton.getText().toString())) {
                    // If the RadioButton's text matches the selected value, check it.
                    radioGroup.check(radioButton.getId());
                    break;
                }
            }
        }
    }

    protected void loadSettings() {

        // Get the ContentResolver.
        ContentResolver resolver = getContentResolver();

        // Read the settings from the database.
        unitSystem = readSetting(resolver, SettingsProvider.UNIT_SYSTEM_KEY);
        Log.d("WA_DEBUG", "Unit System: " + unitSystem);

        language = readSetting(resolver, SettingsProvider.LANGUAGE_KEY);
        Log.d("WA_DEBUG", "Language: " + language);

        notificationInterval = readSetting(resolver, SettingsProvider.NOTIFICATION_INTERVAL_KEY);
        Log.d("WA_DEBUG", "Notification Interval: " + notificationInterval);
    }

    private String readSetting(ContentResolver resolver, String key) {
        // Get the URI for the setting.
        Uri uri = Uri.withAppendedPath(SettingsProvider.CONTENT_URI, key);

        // Get the projection for the setting.
        String[] projection = {SettingsDbHelper.COLUMN_VALUE};

        // Query the database for the setting.
        Cursor cursor = resolver.query(uri, projection, null, null, null);

        // Get the value of the setting.
        String value = null;
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getString(cursor.getColumnIndex(SettingsDbHelper.COLUMN_VALUE));
            cursor.close();
        }
        return value;
    }

    private void storeSetting(ContentResolver resolver, String key, String value) {

        // Get the projection for the setting.
        String[] projection = {
                SettingsDbHelper.COLUMN_ID,
                SettingsDbHelper.COLUMN_KEY,
                SettingsDbHelper.COLUMN_VALUE
        };

        // Get the URI for the setting.
        Uri uri = Uri.withAppendedPath(SettingsProvider.CONTENT_URI, key);

        // Query the database for the setting.
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        cursor.moveToFirst();

        // Create a ContentValues object to store the new value of the setting.
        ContentValues updateValues = new ContentValues();
        updateValues.put(SettingsDbHelper.COLUMN_VALUE, value);

        // Update the setting in the database.
        resolver.update(uri, updateValues, null, null);
    }

    private void sendUpdateIntervalBroadcast(String notificationInterval) {

        Log.d("WA_DEBUG", "sendUpdateIntervalBroadcast: " + notificationInterval);

        // Create an Intent to broadcast the update interval.
        Intent updateIntent = new Intent(NotificationService.ACTION_UPDATE_INTERVAL);

        // Add the notification interval to the Intent.
        updateIntent.putExtra(NotificationService.EXTRA_INTERVAL_STRING, notificationInterval);
        sendBroadcast(updateIntent);

        // Show a toast message to the user.
        Toast.makeText(this, "Interval updated", Toast.LENGTH_SHORT).show();
    }
}
