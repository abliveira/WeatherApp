package com.abliveira.weatherapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.abliveira.weatherapp.MainActivity;
import com.abliveira.weatherapp.R;
import com.abliveira.weatherapp.SettingsDbHelper;
import com.abliveira.weatherapp.SettingsProvider;

import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "WeatherAppNotificationChannel";

    private Handler handler;
    private BroadcastReceiver intervalReceiver;

    public static final String ACTION_UPDATE_INTERVAL = "com.abliveira.weatherapp.action.UPDATE_INTERVAL";
    public static final String EXTRA_INTERVAL_STRING = "com.abliveira.weatherapp.extra.INTERVAL_STRING";

    int intervalSeconds = 10;
    boolean notificationEnabled = true;

    @Override
    public void onCreate() {
        super.onCreate();

        loadSettings();

        // Create a notification channel (required for Android 8.0 and above)
        createNotificationChannel();

        // Schedule notification task every 10 seconds by default
        handler = new Handler(Looper.getMainLooper());
        if (notificationEnabled == true) {
            handler.postDelayed(notificationTask, TimeUnit.SECONDS.toMillis(intervalSeconds));
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
                if (intent.getAction() != null && intent.getAction().equals(ACTION_UPDATE_INTERVAL)) {
                    String newIntervalString = intent.getStringExtra(EXTRA_INTERVAL_STRING);
                    Log.d("WA_DEBUG", "newInterval: " + newIntervalString);
                    processSettings(newIntervalString);

                    if (notificationEnabled == true) {
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

    private final Runnable notificationTask = new Runnable() {
        @Override
        public void run() {
            // Display a notification
            showNotification();

            // Schedule the next notification after the default interval (10 seconds)
            if (notificationEnabled == true) {
                handler.postDelayed(notificationTask, TimeUnit.SECONDS.toMillis(intervalSeconds));
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
        Log.d("WA_DEBUG", "updateNotificationInterval newIntervalString: " + intervalSeconds);
        // Remove the existing callback
        handler.removeCallbacks(notificationTask);
        // Schedule the next notification with the updated interval
        handler.postDelayed(notificationTask, TimeUnit.SECONDS.toMillis(intervalSeconds));
    }

    protected void loadSettings () {

        ContentResolver resolver = getContentResolver();

        String notificationIntervalKey = "notificationInterval";

        String notificationIntervalString = readSetting(resolver, notificationIntervalKey);
        Log.d("WA_DEBUG", "Notification Interval: " + notificationIntervalString);
        processSettings(notificationIntervalString);
        Toast.makeText(this, "Interval loaded: " + intervalSeconds, Toast.LENGTH_SHORT).show();

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

    private void processSettings(String intervalString) {
        // Comparing the variable with each string
        notificationEnabled = true;
        intervalSeconds = 10;
        if (intervalString.equals(getString(R.string.label_disabled))) {
            notificationEnabled = false;
        } else
        if (intervalString.equals(getString(R.string.label_one_hour))) {
            intervalSeconds = 10;//1;
        } else if (intervalString.equals(getString(R.string.label_three_hours))) {
            intervalSeconds = 30;//3;
        } else if (intervalString.equals(getString(R.string.label_twelve_hours))) {
            intervalSeconds = 120;//12;
        } else if (intervalString.equals(getString(R.string.label_one_day))) {
            intervalSeconds = 24;
        } else {
            Toast.makeText(this, "Invalid interval", Toast.LENGTH_SHORT).show();
        }
    }
}
