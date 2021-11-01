package com.diet.trinity.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class FoodItem implements Parcelable {

    public int id;
    public String food_name;
    public float carbon;
    public float protein;
    public float fat;
    public float portion_in_grams;
    public float kcal;
    public float serving_size;
    public List<Category> categories;
    public List<FoodRelation> food_relations;

    public FoodItem() {
    }

    protected FoodItem(Parcel in) {
        id = in.readInt();
        food_name = in.readString();
        carbon = in.readFloat();
        protein = in.readFloat();
        fat = in.readFloat();
        portion_in_grams = in.readFloat();
        kcal = in.readFloat();
        serving_size = in.readFloat();
        categories = in.createTypedArrayList(Category.CREATOR);
        food_relations = in.createTypedArrayList(FoodRelation.CREATOR);
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
        dest.writeFloat(carbon);
        dest.writeFloat(protein);
        dest.writeFloat(fat);
        dest.writeFloat(portion_in_grams);
        dest.writeFloat(kcal);
        dest.writeFloat(serving_size);
        dest.writeTypedList(categories);
        dest.writeTypedList(food_relations);
    }
}
