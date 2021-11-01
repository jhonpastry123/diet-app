package com.diet.trinity.Utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SportsDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="Sports.db";
    public static final String TABLE_NAME="Sports";
    public static final String COL_1="ID";
    public static final String COL_2="UserID";
    public static final String COL_3="Sport1_type";
    public static final String COL_4="Sport1_min";
    public static final String COL_5="Sport1_efficient";

    public static final String COL_6="Sport2_type";
    public static final String COL_7="Sport2_min";
    public static final String COL_8="Sport2_efficient";

    public static final String COL_9="Sport3_type";
    public static final String COL_10="Sport3_min";
    public static final String COL_11="Sport3_efficient";

    public SportsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, UserID TEXT, Sport1_type TEXT, Sport1_min TEXT, Sport1_efficient TEXT, Sport2_type TEXT, Sport2_min TEXT, Sport2_efficient TEXT, Sport3_type TEXT, Sport3_min TEXT, Sport3_efficient TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

}
