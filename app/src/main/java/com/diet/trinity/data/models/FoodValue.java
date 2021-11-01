package com.diet.trinity.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class FoodValue implements Parcelable {

    public int id;
    public int recipes_id;
    public int food_items_id;
    public int amount;
    public FoodItem food_item;

    public FoodValue() {
    }

    protected FoodValue(Parcel in) {
        id = in.readInt();
        recipes_id = in.readInt();
        food_items_id = in.readInt();
        amount = in.readInt();
        food_item = in.readTypedObject(FoodItem.CREATOR);
    }

    public static final Creator<FoodValue> CREATOR = new Creator<FoodValue>() {
        @Override
        public FoodValue createFromParcel(Parcel in) {
            return new FoodValue(in);
        }

        @Override
        public FoodValue[] newArray(int size) {
            return new FoodValue[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(recipes_id);
        dest.writeInt(food_items_id);
        dest.writeInt(amount);
        dest.writeTypedObject(food_item, flags);
    }
}
