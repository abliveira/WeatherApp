package com.abliveira.weatherapp;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.isEmptyOrNullString;

import com.abliveira.weatherapp.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityInstrumentedTest {

    // Use the ActivityScenarioRule to launch the activity under test
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    // Context for the app
    private Context context;

    @Before
    public void setUp() {
        // Set up the context
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testInsertData() {

        // Type text into the locationEditText
        String testLocation = "Seoul";
        Espresso.onView(ViewMatchers.withId(R.id.locationEditText))
                .perform(ViewActions.typeText(testLocation), ViewActions.closeSoftKeyboard());

        // Check if the text in locationEditText matches the inserted text
        Espresso.onView(ViewMatchers.withId(R.id.locationEditText))
                .check(ViewAssertions.matches(ViewMatchers.withText(testLocation)));
    }

    @Test
    public void testWeatherDataUI() {

        // Type text into the locationEditText
        String testLocation = "Seoul";
        Espresso.onView(ViewMatchers.withId(R.id.locationEditText))
                .perform(ViewActions.typeText(testLocation), ViewActions.closeSoftKeyboard());

        // Tap the searchButton
        Espresso.onView(ViewMatchers.withId(R.id.searchButton)).perform(ViewActions.click());

        // Check if the UI loaded weather data from the API, by checking if TextView elements have non-empty text
        onView(ViewMatchers.withId(R.id.cityNameTextView)).check(ViewAssertions.matches(not(ViewMatchers.withText(isEmptyOrNullString()))));
        onView(ViewMatchers.withId(R.id.weatherDescriptionTextView)).check(matches(not(withText(isEmptyOrNullString()))));
        onView(ViewMatchers.withId(R.id.currTempTextView)).check(matches(not(withText(isEmptyOrNullString()))));
        onView(ViewMatchers.withId(R.id.minTempTextView)).check(matches(not(withText(isEmptyOrNullString()))));
        onView(ViewMatchers.withId(R.id.maxTempTextview)).check(matches(not(withText(isEmptyOrNullString()))));
        onView(ViewMatchers.withId(R.id.windSpeedTextView)).check(matches(not(withText(isEmptyOrNullString()))));
        onView(ViewMatchers.withId(R.id.humidityTextView)).check(matches(not(withText(isEmptyOrNullString()))));
    }
}