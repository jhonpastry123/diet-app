package com.diet.trinity.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class Recipe implements Parcelable {

    public int id;
    public String title;
    public int categories_id;
    public String description;
    public String image;
    public List<Category> category_name;
    public List<FoodValue> foodvalues;

    public Recipe() {
    }

    protected Recipe(Parcel in) {
        id = in.readInt();
        title = in.readString();
        categories_id = in.readInt();
        description = in.readString();
        image = in.readString();
        category_name = in.createTypedArrayList(Category.CREATOR);
        foodvalues = in.createTypedArrayList(FoodValue.CREATOR);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeInt(categories_id);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeTypedList(category_name);
        dest.writeTypedList(foodvalues);
    }
}
