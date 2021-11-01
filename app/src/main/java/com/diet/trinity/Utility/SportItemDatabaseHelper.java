package com.diet.trinity.Utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SportItemDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="SportItems.db";
    public static final String TABLE_NAME="SportItem";
    public static final String COL_1="ID";
    public static final String COL_2="SportName";
    public static final String COL_3="SportCoefficient";
    public static final String COL_4="SportType";


    public SportItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, SportName TEXT, SportCoefficient TEXT, SportType TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

}
