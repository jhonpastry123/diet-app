package com.diet.trinity.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class FoodRelation implements Parcelable {
    public int id;
    public int food_item_id;
    public int food_category_id;
    public FoodCategory food_category;

    public FoodRelation() {

    }

    protected FoodRelation(Parcel in) {
        id = in.readInt();
        food_item_id = in.readInt();
        food_category_id = in.readInt();
        food_category = in.readParcelable(FoodCategory.class.getClassLoader());
    }

    public static final Creator<FoodRelation> CREATOR = new Creator<FoodRelation>() {
        @Override
        public FoodRelation createFromParcel(Parcel in) {
            return new FoodRelation(in);
        }

        @Override
        public FoodRelation[] newArray(int size) {
            return new FoodRelation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(food_item_id);
        dest.writeInt(food_category_id);
        dest.writeParcelable(food_category, flags);
    }
}
