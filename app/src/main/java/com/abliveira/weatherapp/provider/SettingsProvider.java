package com.abliveira.weatherapp.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abliveira.weatherapp.data.SettingsDbHelper;

public class SettingsProvider extends ContentProvider {

    private static final String AUTHORITY = "com.abliveira.weatherapp.settingsprovider";
    private static final String PATH_SETTINGS = "settings";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_SETTINGS);

    private static final int SETTINGS = 1;
    private static final int SETTINGS_KEY = 2;

    public static final String UNIT_SYSTEM_KEY = "unitSystem";
    public static final String LANGUAGE_KEY = "language";
    public static final String NOTIFICATION_INTERVAL_KEY = "notificationInterval";

    private SQLiteOpenHelper dbHelper;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, PATH_SETTINGS, SETTINGS);
        URI_MATCHER.addURI(AUTHORITY, PATH_SETTINGS + "/*", SETTINGS_KEY);
    }

    @Override
    public boolean onCreate() {
        // Create the database helper.
        dbHelper = new SettingsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get the database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;

        // Match the URI to a specific query.
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case SETTINGS:
                // Query all settings.
                cursor = db.query(SettingsDbHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SETTINGS_KEY:
                // Query a specific setting by key.
                String key = uri.getLastPathSegment();
                String keySelection = SettingsDbHelper.COLUMN_KEY + "=?";
                String[] keySelectionArgs = {key};
                cursor = db.query(SettingsDbHelper.TABLE_NAME, projection, keySelection, keySelectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Set the notification URI on the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Get the match for the given URI.
        int match = URI_MATCHER.match(uri);
        // Switch on the match to determine which table to insert the data into.
        switch (match) {
            case SETTINGS:
                // Insert the data into the settings table.
                return insertSetting(uri, values);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private Uri insertSetting(Uri uri, ContentValues values) {
        // Get a writable database connection.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Insert the data into the settings table.
        long id = db.insert(SettingsDbHelper.TABLE_NAME, null, values);

        // Check if the insert was successful.
        if (id == -1) {
            throw new SQLException("Failed to insert row into " + uri);
        }

        // Notify the content resolver that the data has changed.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the URI for the newly inserted row.
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // Get the match for the URI.
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case SETTINGS_KEY:
                String key = uri.getLastPathSegment();
                // Create a selection clause for the key.
                String keySelection = SettingsDbHelper.COLUMN_KEY + "=?";
                // Create an array of selection arguments for the key.
                String[] keySelectionArgs = {key};
                // Update the settings table with the new values.
                int rowsUpdated = dbHelper.getWritableDatabase().update(
                        SettingsDbHelper.TABLE_NAME,
                        values,
                        keySelection,
                        keySelectionArgs
                );

                // If any rows were updated,
                if (rowsUpdated != 0) {
                    // Notify the content resolver that the data has changed.
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                // Return the number of rows updated.
                return rowsUpdated;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get the match for the URI.
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case SETTINGS_KEY:
                // Get the key from the URI.
                String key = uri.getLastPathSegment();
                // Create a selection clause for the key.
                String keySelection = SettingsDbHelper.COLUMN_KEY + "=?";
                // Create an array of selection arguments for the key.
                String[] keySelectionArgs = {key};
                // Delete the settings row with the matching key.
                int rowsDeleted = dbHelper.getWritableDatabase().delete(
                        SettingsDbHelper.TABLE_NAME,
                        keySelection,
                        keySelectionArgs
                );

                // If any rows were deleted,
                if (rowsDeleted != 0) {
                    // Notify the content resolver that the data has changed.
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                // Return the number of rows deleted.
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Get the match for the URI.
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case SETTINGS:
                // Return the MIME type for a directory of settings.
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + PATH_SETTINGS;
            case SETTINGS_KEY:
                // Return the MIME type for a single settings key.
                return "vnd.android.cursor.item/" + AUTHORITY + "." + PATH_SETTINGS;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    public static String readSetting(ContentResolver resolver, String key) {
        // Create a URI for the setting.
        Uri uri = Uri.withAppendedPath(SettingsProvider.CONTENT_URI, key);

        // The projection for the query.
        String[] projection = {SettingsDbHelper.COLUMN_VALUE};

        // Perform the query.
        Cursor cursor = resolver.query(uri, projection, null, null, null);

        // Get the value of the setting.
        String value = null;
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getString(cursor.getColumnIndex(SettingsDbHelper.COLUMN_VALUE));
            cursor.close();
        }

        // Return the value of the setting.
        return value;
    }
}
