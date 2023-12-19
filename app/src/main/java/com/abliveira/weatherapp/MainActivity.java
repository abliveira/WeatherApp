package com.abliveira.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    }
}
