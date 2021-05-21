package com.example.restaurantmapapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DatabaseAdapter {
    private DatabaseHelper dbHelper;

    public DatabaseAdapter(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public void addPlace(Place place) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "insert into place_table(placeName,latitude,longitude) values(?,?,?)";
        Object[] args = {place.getName(), place.getLatitude(), place.getLongitude()};
        db.execSQL(sql, args);
        db.close();
    }

    public ArrayList<Place> findAllPlace() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select * from place_table";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Place> placeArrayList = new ArrayList<>();
        Place place = null;
        while (cursor.moveToNext()) {
            place = new Place();
            place.setName(cursor.getString(cursor.getColumnIndexOrThrow("placeName")));
            place.setLatitude(cursor.getFloat(cursor.getColumnIndexOrThrow("latitude")));
            place.setLongitude(cursor.getFloat(cursor.getColumnIndexOrThrow("longitude")));
            placeArrayList.add(place);
        }
        cursor.close();
        db.close();
        return placeArrayList;
    }
}
