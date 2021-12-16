package com.diet.trinity.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

public class GraphValue implements Parcelable {
    public int id;
    public String date;
    public float weight;
    public GraphValue() {

    }

    protected GraphValue(Parcel in) {
        id = in.readInt();
        weight = in.readFloat();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeFloat(weight);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GraphValue> CREATOR = new Creator<GraphValue>() {
        @Override
        public GraphValue createFromParcel(Parcel in) {
            return new GraphValue(in);
        }

        @Override
        public GraphValue[] newArray(int size) {
            return new GraphValue[size];
        }
    };
}
