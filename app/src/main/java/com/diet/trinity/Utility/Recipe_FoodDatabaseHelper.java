package com.diet.trinity.Utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Recipe_FoodDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="Receip_FoodItems.db";
    public static final String TABLE_NAME="Recipe_Food";
    public static final String COL_1="ID";
    public static final String COL_2="Amount";
    public static final String COL_3="Food_ID";
    public static final String COL_4="Food_Category_ID";
    public static final String COL_5="FoodName";
    public static final String COL_6="Carbon";
    public static final String COL_7="Protein";
    public static final String COL_8="Fat";
    public static final String COL_9="Grams";
    public static final String COL_10="kcal";
    public Recipe_FoodDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER, Amount Integer, Food_ID float, Food_Category_ID Text, FoodName String, Carbon float, Protein float, Fat float, Grams float, kcal float)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }
}