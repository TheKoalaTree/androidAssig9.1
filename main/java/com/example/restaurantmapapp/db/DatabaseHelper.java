package com.example.restaurantmapapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "place.db";
    private static final int VERSION = 1;

    private static final String CREATE_TABLE_PLACE = "Create Table IF NOT EXISTS place_table(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "placeName TEXT,latitude FLOAT,longitude FLOAT)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_PLACE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
