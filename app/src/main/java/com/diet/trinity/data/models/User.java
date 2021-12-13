package com.diet.trinity.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public int id;
    public String email;
    public int type;
    public String purchase_time;
    public String created_at;
    public String updated_at;

    public User() {

    }

    protected User(Parcel in) {
        id = in.readInt();
        email = in.readString();
        type = in.readInt();
        purchase_time = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeInt(type);
        dest.writeString(purchase_time);
        dest.writeString(created_at);
        dest.writeString(updated_at);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
