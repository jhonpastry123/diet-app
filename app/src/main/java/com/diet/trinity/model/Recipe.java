package com.diet.trinity.model;

public class Recipe{
    public int categoryID;
    public int foodID;
    public String foodTitle;
    public String photoResId;
    public float points = 0;
    public float units = 0;

    public Recipe(int categoryID,int foodID, String name, String resId, float points, float units){
        this.categoryID = categoryID;
        this.foodID = foodID;
        this.foodTitle = name;
        this.photoResId = resId;
        this.points = points;
        this.units = units;
    }
}
