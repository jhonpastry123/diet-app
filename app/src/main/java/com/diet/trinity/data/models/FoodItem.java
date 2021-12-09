package com.diet.trinity.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItem implements Parcelable {

    public int id;
    public String food_name;
    public float portion_in_grams;
    public float serving_size;
    public String serving_prefix;
    public float units;
    public float points;

    public FoodItem() {
    }

    protected FoodItem(Parcel in) {
        id = in.readInt();
        food_name = in.readString();
        portion_in_grams = in.readFloat();
        serving_size = in.readFloat();
        serving_prefix = in.readString();
        units = in.readFloat();
        points = in.readFloat();
    }

    public static final Creator<FoodItem> CREATOR = new Creator<FoodItem>() {
        @Override
        public FoodItem createFromParcel(Parcel in) {
            return new FoodItem(in);
        }

        @Override
        public FoodItem[] newArray(int size) {
            return new FoodItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(food_name);
        dest.writeFloat(portion_in_grams);
        dest.writeFloat(serving_size);
        dest.writeString(serving_prefix);
        dest.writeFloat(units);
        dest.writeFloat(points);
    }
}
