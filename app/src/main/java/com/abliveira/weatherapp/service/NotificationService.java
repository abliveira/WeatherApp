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

import androidx.core.app.NotificationCompat;

import com.abliveira.weatherapp.MainActivity;
import com.abliveira.weatherapp.R;

import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "WeatherAppNotificationChannel";

    private Handler handler;
    private BroadcastReceiver intervalReceiver;

    public static final String ACTION_UPDATE_INTERVAL = "com.abliveira.weatherapp.action.UPDATE_INTERVAL";
    public static final String EXTRA_INTERVAL_SECONDS = "com.abliveira.weatherapp.extra.INTERVAL_SECONDS";
    public static final String EXTRA_NOTIFICATION_ENABLED = "com.abliveira.weatherapp.extra.NOTIFICATION_ENABLED";

    int intervalSeconds = 10; // TODO Load intervalSeconds from Storage

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a notification channel (required for Android 8.0 and above)
        createNotificationChannel();

        // Schedule notification task every 10 seconds by default
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(notificationTask, TimeUnit.SECONDS.toMillis(intervalSeconds));

        // Register the dynamic receiver
        intervalReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("WA_DEBUG", "onReceive: ");
                if (intent.getAction() != null && intent.getAction().equals(ACTION_UPDATE_INTERVAL)) {
                    boolean notificationEnabled = intent.getBooleanExtra(EXTRA_NOTIFICATION_ENABLED, true);
                    int newIntervalSeconds = intent.getIntExtra(EXTRA_INTERVAL_SECONDS, 10);
                    Log.d("WA_DEBUG", "newInterval: " + newIntervalSeconds);

                    if (notificationEnabled == true) {
                        updateNotificationInterval(newIntervalSeconds);
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
            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(intervalSeconds));
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

    private void updateNotificationInterval(int newIntervalSeconds) {
        intervalSeconds = newIntervalSeconds;
        Log.d("WA_DEBUG", "updateNotificationInterval newIntervalSeconds: " + intervalSeconds);
        // Remove the existing callback
        handler.removeCallbacks(notificationTask);
        // Schedule the next notification with the updated interval
        handler.postDelayed(notificationTask, TimeUnit.SECONDS.toMillis(intervalSeconds));
    }
}
