package com.diet.trinity.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Sport implements Parcelable {
    public int id;
    public String name;
    public Double coefficient;

    public Sport() {

    }

    protected Sport(Parcel in) {
        id = in.readInt();
        name = in.readString();
        coefficient = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(coefficient);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sport> CREATOR = new Creator<Sport>() {
        @Override
        public Sport createFromParcel(Parcel in) {
            return new Sport(in);
        }

        @Override
        public Sport[] newArray(int size) {
            return new Sport[size];
        }
    };
}
