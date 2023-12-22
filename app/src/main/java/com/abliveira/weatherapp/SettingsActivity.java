package com.abliveira.weatherapp;

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
    private boolean notificationsEnabled;
    private int notificationsInterval;

    private RadioGroup unitSystemRadioGroup;
    private RadioGroup languageRadioGroup;
    private RadioGroup notificationIntervalRadioGroup;
    private Button saveButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        unitSystem = "Metric";  // TODO Replace this with the stored value
        language = "Spanish";   // TODO Replace this with the stored value
//        intervalValue = "Once a day"; // TODO Replace this with the stored value

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

                // TODO Implement configuration saving here

                // Display a toast with the selected options
                String message = "Unit System: " + radioGroup1Value
                        + "\nLanguage: " + radioGroup2Value
                        + "\nNotification Interval: " + radioGroup3Value;

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
        String intervalValue = "Once a day"; // TODO Replace this with the a dynamic value

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
}
