package com.diet.trinity.data.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.List;

public class Meal implements Parcelable {

    public int id;
    public String food_name;
    public int timing_id;
    public String serving_prefix;
    public float serving_size;
    public float serving_count;
    public int flag_carbon;
    public int flag_protein;
    public int flag_fat;
    public float carbon;
    public float protein;
    public float fat;
    public float units;
    public float points;
    public float amount;
    public int pasta_num;
    public int legumes_num;
    public int oily_num;
    public int junk_img_num;
    public int fruit_num;
    public int meat_num;
    public int oily_img_num;


    public Meal() {
    }

    protected Meal(Parcel in) {
        id = in.readInt();
        food_name = in.readString();
        timing_id = in.readInt();
        serving_prefix = in.readString();
        serving_size = in.readFloat();
        serving_count = in.readFloat();
        flag_carbon = in.readInt();
        flag_protein = in.readInt();
        flag_fat = in.readInt();
        carbon = in.readFloat();
        protein = in.readFloat();
        fat = in.readFloat();
        units = in.readFloat();
        points = in.readFloat();
        amount = in.readFloat();
        pasta_num = in.readInt();
        legumes_num = in.readInt();
        oily_num = in.readInt();
        junk_img_num = in.readInt();
        fruit_num = in.readInt();
        meat_num = in.readInt();
        oily_img_num = in.readInt();
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
        dest.writeInt(timing_id);
        dest.writeString(serving_prefix);
        dest.writeFloat(serving_size);
        dest.writeFloat(serving_count);
        dest.writeInt(flag_carbon);
        dest.writeInt(flag_protein);
        dest.writeInt(flag_fat);
        dest.writeFloat(carbon);
        dest.writeFloat(protein);
        dest.writeFloat(fat);
        dest.writeFloat(units);
        dest.writeFloat(points);
        dest.writeFloat(amount);
        dest.writeInt(pasta_num);
        dest.writeInt(legumes_num);
        dest.writeInt(oily_num);
        dest.writeInt(junk_img_num);
        dest.writeInt(fruit_num);
        dest.writeInt(meat_num);
        dest.writeInt(oily_img_num);
    }
}
