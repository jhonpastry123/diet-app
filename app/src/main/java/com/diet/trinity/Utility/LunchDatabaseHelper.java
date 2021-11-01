package com.diet.trinity.Utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LunchDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="LunchDB.db";
    public static final String TABLE_NAME="LunchTable";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "foodname";
    public static final String COL_3 = "categoryid";
    public static final String COL_4 = "date";
    public static final String COL_5 = "days";
    public LunchDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,foodname TEXT, categoryid TEXT, date TEXT, days INTEGER)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }
}
