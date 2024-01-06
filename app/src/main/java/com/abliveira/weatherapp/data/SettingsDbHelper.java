package com.abliveira.weatherapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SettingsDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "settings.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "settings";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_VALUE = "value";

    // The SQL statement to create the settings table
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_KEY + " TEXT," +
                    COLUMN_VALUE + " TEXT)";

    // The constructor for the SettingsDbHelper
    public SettingsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // The onCreate method is called when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the settings table
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades
    }
}

