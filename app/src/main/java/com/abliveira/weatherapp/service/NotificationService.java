package com.abliveira.weatherapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.abliveira.weatherapp.ui.MainActivity;
import com.abliveira.weatherapp.R;
import com.abliveira.weatherapp.data.SettingsProvider;

import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "WeatherAppNotificationChannel";

    private Handler handler;
    private BroadcastReceiver intervalReceiver;

    public static final String ACTION_UPDATE_INTERVAL = "com.abliveira.weatherapp.action.UPDATE_INTERVAL";
    public static final String EXTRA_INTERVAL_STRING = "com.abliveira.weatherapp.extra.INTERVAL_STRING";

    // TODO Replace TimeUnit.SECONDS.toMillis to HOURS, and change the intervals below
    final int INTERVAL_ONE_HOUR = 1*10;
    final int INTERVAL_THREE_HOURS = 3*10;
    final int INTERVAL_TWELVE_HOURS = 12*10;
    final int INTERVAL_ONE_DAY = 24*10;

    int timeInterval = INTERVAL_ONE_HOUR; // Default interval is 1 hour
    boolean notificationEnabled = false; // Default notification is enabled

    @Override
    public void onCreate() {
        super.onCreate();

        loadSettings();

        // Create a notification channel (required for Android 8.0 and above)
        createNotificationChannel();

        // Schedule the first notification with the default value
        handler = new Handler(Looper.getMainLooper());
        if (notificationEnabled) {
            handler.postDelayed(notificationTask, TimeUnit.SECONDS.toMillis(timeInterval));
            Log.d("WA_DEBUG", "notificationEnabled true");
        } else {
            handler.removeCallbacks(notificationTask);
            Log.d("WA_DEBUG", "notificationEnabled false  ");
        }

        // Register the dynamic receiver
        intervalReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("WA_DEBUG", "onReceive: ");
                // Check if the intent has the ACTION_UPDATE_INTERVAL action
                if (intent.getAction() != null && intent.getAction().equals(ACTION_UPDATE_INTERVAL)) {
                    // Get the new interval string from the intent
                    String newIntervalString = intent.getStringExtra(EXTRA_INTERVAL_STRING);
                    Log.d("WA_DEBUG", "newInterval: " + newIntervalString);
                    assert newIntervalString != null;
                    processSettings(newIntervalString);

                    if (notificationEnabled) {
                        updateNotificationInterval();
                        Log.d("WA_DEBUG", "notificationEnabled true");
                    } else {
                        handler.removeCallbacks(notificationTask);
                        Log.d("WA_DEBUG", "notificationEnabled false  ");
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(ACTION_UPDATE_INTERVAL);
        registerReceiver(intervalReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the callback and unregister the receiver to prevent memory leaks
        handler.removeCallbacks(notificationTask);
        unregisterReceiver(intervalReceiver);
    }

    // When the time interval is completed
    private final Runnable notificationTask = new Runnable() {
        @Override
        public void run() {
            // Display a notification
            showNotification();

            // Schedule the next notification with the value from the settings
            if (notificationEnabled) {
                handler.postDelayed(notificationTask, TimeUnit.SECONDS.toMillis(timeInterval));
                Log.d("WA_DEBUG", "notificationEnabled true");
            } else {
                handler.removeCallbacks(notificationTask);
                Log.d("WA_DEBUG", "notificationEnabled false  ");
            }
        }
    };

    private void showNotification() {
        // Create an intent to open the main activity when the notification is clicked
        Intent intent = new Intent(NotificationService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                NotificationService.this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Weather App")
                .setContentText("Tap here to check the latest weather updates")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        // Create a notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Weather App Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateNotificationInterval() {
        Log.d("WA_DEBUG", "updateNotificationInterval newIntervalString: " + timeInterval);
        // Remove the existing callback
        handler.removeCallbacks(notificationTask);
        // Schedule the next notification with the updated interval
        handler.postDelayed(notificationTask, TimeUnit.SECONDS.toMillis(timeInterval));
    }

    protected void loadSettings() {

        // Read the notification interval setting from the database.
        String notificationIntervalString = SettingsProvider.readSetting(getContentResolver(), SettingsProvider.NOTIFICATION_INTERVAL_KEY);
        Log.d("WA_DEBUG", "Notification Interval: " + notificationIntervalString);

        // Process the notification interval setting.
        processSettings(notificationIntervalString);

        // Show a toast message to the user.
        Toast.makeText(this, "Interval loaded: " + timeInterval, Toast.LENGTH_SHORT).show();

    }

    private void processSettings(String intervalString) {
        // Comparing the variable with each string
        notificationEnabled = true;
        timeInterval = INTERVAL_ONE_HOUR; // The default value
        if (intervalString.equals(getString(R.string.label_disabled))) {
            notificationEnabled = false;
        } else
        if (intervalString.equals(getString(R.string.label_one_hour))) {
            timeInterval = INTERVAL_ONE_HOUR;
        } else if (intervalString.equals(getString(R.string.label_three_hours))) {
            timeInterval = INTERVAL_THREE_HOURS;
        } else if (intervalString.equals(getString(R.string.label_twelve_hours))) {
            timeInterval = INTERVAL_TWELVE_HOURS;
        } else if (intervalString.equals(getString(R.string.label_one_day))) {
            timeInterval = INTERVAL_ONE_DAY;
        } else {
            Toast.makeText(this, "Invalid interval", Toast.LENGTH_SHORT).show();
        }
    }
}
