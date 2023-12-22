package com.abliveira.weatherapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private String unitSystem;
    private String language;
    private String notificationInterval;
//    private boolean notificationEnabled;
//    private int notificationIntervalHours;

    private RadioGroup unitSystemRadioGroup;
    private RadioGroup languageRadioGroup;
    private RadioGroup notificationIntervalRadioGroup;
    private Button saveButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        loadSettings();
        loadUI();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieve selected radio button from Unit System RadioGroup
                int selectedRadioButtonId1 = unitSystemRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton1 = findViewById(selectedRadioButtonId1);
                String radioGroup1Value = radioButton1 != null ? radioButton1.getText().toString() : "No option selected";
                unitSystem = radioGroup1Value;

                // Retrieve selected radio button from Language RadioGroup
                int selectedRadioButtonId2 = languageRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton2 = findViewById(selectedRadioButtonId2);
                String radioGroup2Value = radioButton2 != null ? radioButton2.getText().toString() : "No option selected";
                language = radioGroup2Value;

                // Retrieve selected radio button from Notification Interval RadioGroup
                int selectedRadioButtonId3 = notificationIntervalRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton3 = findViewById(selectedRadioButtonId3);
                String radioGroup3Value = radioButton3 != null ? radioButton3.getText().toString() : "No option selected";
                notificationInterval = radioGroup3Value;

                ContentResolver resolver = getContentResolver();

                String unitSystemKey = "unitsystem";
                String languageKey = "language";
                String notificationIntervalKey = "notificationInterval";

                storeSetting(resolver, unitSystemKey, unitSystem);
                storeSetting(resolver, languageKey, language);
                storeSetting(resolver, notificationIntervalKey, notificationInterval);

                // Read and display stored settings

                unitSystem= readSetting(resolver, unitSystemKey);
                Log.d("WA_DEBUG", "Unit System: " + unitSystem);

                language = readSetting(resolver, languageKey);
                Log.d("WA_DEBUG", "Language: " + language);

                notificationInterval= readSetting(resolver, notificationIntervalKey);
                Log.d("WA_DEBUG", "Notification Interval: " + notificationInterval);

                // Display a toast with the stored options
                String message = "Unit System: " + unitSystem
                        + "\nLanguage: " + language
                        + "\nNotification Interval: " + notificationInterval;

                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

                finish(); // Close the activity and return to the main
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity and return to the main
            }
        });
    }

    private void loadUI() {

        unitSystemRadioGroup = findViewById(R.id.unitSystemRadioGroup);
        languageRadioGroup = findViewById(R.id.languageRadioGroup);
        notificationIntervalRadioGroup = findViewById(R.id.notificationIntervalRadioGroup);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        RadioGroup unitSystemRadioGroup = findViewById(R.id.unitSystemRadioGroup);
        RadioGroup languageRadioGroup = findViewById(R.id.languageRadioGroup);
        RadioGroup notificationIntervalRadioGroup = findViewById(R.id.notificationIntervalRadioGroup);

        String unitSystemValue = unitSystem;
        String languageValue = language;
        String intervalValue = notificationInterval;

        // Select RadioButton based on the string value
        selectRadioButtonByText(unitSystemRadioGroup, unitSystemValue);
        selectRadioButtonByText(languageRadioGroup, languageValue);
        selectRadioButtonByText(notificationIntervalRadioGroup, intervalValue);
    }

    private void selectRadioButtonByText(RadioGroup radioGroup, String selectedValue) {

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View view = radioGroup.getChildAt(i);
            if (view instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) view;
                if (selectedValue.equals(radioButton.getText().toString())) {
                    radioGroup.check(radioButton.getId());
                    break;
                }
            }
        }
    }

    protected void loadSettings () {

        ContentResolver resolver = getContentResolver();

        String unitSystemKey = "unitsystem";
        String languageKey = "language";
        String notificationIntervalKey = "notificationInterval";

        // Read and display settings
        unitSystem = readSetting(resolver, unitSystemKey);
        Log.d("WA_DEBUG", "Unit System: " + unitSystem);

        language = readSetting(resolver, languageKey);
        Log.d("WA_DEBUG", "Language: " + language);

        notificationInterval = readSetting(resolver, notificationIntervalKey);
        Log.d("WA_DEBUG", "Notification Interval: " + notificationInterval);
    }

    private String readSetting(ContentResolver resolver, String key) {
        Uri uri = Uri.withAppendedPath(SettingsProvider.CONTENT_URI, key);
        String[] projection = {SettingsDbHelper.COLUMN_VALUE};

        Cursor cursor = resolver.query(uri, projection, null, null, null);

        String value = null;
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getString(cursor.getColumnIndex(SettingsDbHelper.COLUMN_VALUE));
            cursor.close();
        }

        return value;
    }

    private void storeSetting(ContentResolver resolver, String key, String value) {

        String[] projection = {
                SettingsDbHelper.COLUMN_ID,
                SettingsDbHelper.COLUMN_KEY,
                SettingsDbHelper.COLUMN_VALUE
        };

        Uri uri = Uri.withAppendedPath(SettingsProvider.CONTENT_URI, key);
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        cursor.moveToFirst();

        ContentValues updateValues = new ContentValues();
        updateValues.put(SettingsDbHelper.COLUMN_VALUE, value);
        resolver.update(uri, updateValues, null, null);
    }
}
