package com.abliveira.weatherapp;

import android.content.ContentProvider;
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

public class SettingsProvider extends ContentProvider {

    private static final String AUTHORITY = "com.abliveira.weatherapp.settingsprovider";
    private static final String PATH_SETTINGS = "settings";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_SETTINGS);

    private static final int SETTINGS = 1;
    private static final int SETTINGS_KEY = 2;

    private SQLiteOpenHelper dbHelper;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, PATH_SETTINGS, SETTINGS);
        URI_MATCHER.addURI(AUTHORITY, PATH_SETTINGS + "/*", SETTINGS_KEY);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new SettingsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = URI_MATCHER.match(uri);
        switch (match) {
            case SETTINGS:
                cursor = db.query(SettingsDbHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SETTINGS_KEY:
                String key = uri.getLastPathSegment();
                String keySelection = SettingsDbHelper.COLUMN_KEY + "=?";
                String[] keySelectionArgs = {key};
                cursor = db.query(SettingsDbHelper.TABLE_NAME, projection, keySelection, keySelectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case SETTINGS:
                return insertSetting(uri, values);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private Uri insertSetting(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(SettingsDbHelper.TABLE_NAME, null, values);

        if (id == -1) {
            throw new SQLException("Failed to insert row into " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case SETTINGS_KEY:
                String key = uri.getLastPathSegment();
                String keySelection = SettingsDbHelper.COLUMN_KEY + "=?";
                String[] keySelectionArgs = {key};
                int rowsUpdated = dbHelper.getWritableDatabase().update(
                        SettingsDbHelper.TABLE_NAME,
                        values,
                        keySelection,
                        keySelectionArgs
                );

                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsUpdated;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case SETTINGS_KEY:
                String key = uri.getLastPathSegment();
                String keySelection = SettingsDbHelper.COLUMN_KEY + "=?";
                String[] keySelectionArgs = {key};
                int rowsDeleted = dbHelper.getWritableDatabase().delete(
                        SettingsDbHelper.TABLE_NAME,
                        keySelection,
                        keySelectionArgs
                );

                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case SETTINGS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + PATH_SETTINGS;
            case SETTINGS_KEY:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + PATH_SETTINGS;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
