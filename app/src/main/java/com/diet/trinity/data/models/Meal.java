package com.diet.trinity.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Meal implements Parcelable {

    public int id;
    public String food_name;
    public String serving_prefix;
    public float serving_size;
    public float serving_count;
    public float carbon;
    public float protein;
    public float fat;
    public float carbon_gram;
    public float protein_gram;
    public float fat_gram;
    public float units;
    public float points;
    public float amount;

    public Meal() {
    }

    protected Meal(Parcel in) {
        id = in.readInt();
        food_name = in.readString();
        serving_prefix = in.readString();
        serving_size = in.readFloat();
        serving_count = in.readFloat();
        carbon = in.readFloat();
        protein = in.readFloat();
        fat = in.readFloat();
        carbon_gram = in.readFloat();
        protein_gram = in.readFloat();
        fat_gram = in.readFloat();
        units = in.readFloat();
        points = in.readFloat();
        amount = in.readFloat();
    }

    public static final Creator<Meal> CREATOR = new Creator<Meal>() {
        @Override
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }

        @Override
        public Meal[] newArray(int size) {
            return new Meal[size];
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
        dest.writeString(serving_prefix);
        dest.writeFloat(serving_size);
        dest.writeFloat(serving_count);
        dest.writeFloat(carbon);
        dest.writeFloat(protein);
        dest.writeFloat(fat);
        dest.writeFloat(carbon_gram);
        dest.writeFloat(protein_gram);
        dest.writeFloat(fat_gram);
        dest.writeFloat(units);
        dest.writeFloat(points);
        dest.writeFloat(amount);
    }
}
