package com.abliveira.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
