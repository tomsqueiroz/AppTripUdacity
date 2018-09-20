package com.example.tom.apptripudacity.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tom on 20/09/18.
 */

public class PlaceDbHelper extends SQLiteOpenHelper{


    public static final String DATABASE_NAME = "place.db";
    private static final int DATABASE_VERSION = 1;

    public PlaceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_PLACE_TABLE =

                "CREATE TABLE " + PlaceContract.PlaceEntry.TABLE_NAME + " (" +

                        PlaceContract.PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PlaceContract.PlaceEntry.COLUMN_NAME + " VARCHAR(30), " +
                        PlaceContract.PlaceEntry.COLUMN_LAT + " REAL, " +
                        PlaceContract.PlaceEntry.COLUMN_LNG + " REAL, " +
                        PlaceContract.PlaceEntry.COLUMN_PLACE_ID + " VARCHAR(50), " +
                        PlaceContract.PlaceEntry.COLUMN_PHOTOS + " VARCHAR(200), " +
                        PlaceContract.PlaceEntry.COLUMN_RATING + " REAL, " +
                        " UNIQUE (" + PlaceContract.PlaceEntry.COLUMN_PLACE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_PLACE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PlaceContract.PlaceEntry.TABLE_NAME);
        onCreate(db);
    }
}
