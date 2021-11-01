package com.diet.trinity.Utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReceipeDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="ReceipItems.db";
    public static final String TABLE_NAME = "ReceipTable";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Title";
    public static final String COL_3 = "Category_id";
    public static final String COL_4 = "Description";
    public static final String COL_5 = "Image";
    public static final String COL_6 = "Category_name";
    public static final String COL_7 = "Points";
    public static final String COL_8 = "Units";
    public ReceipeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER ,Title TEXT,Category_id TEXT, Description TEXT, Image TEXT, Category_name TEXT, Points Float, Units Float)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }
}
