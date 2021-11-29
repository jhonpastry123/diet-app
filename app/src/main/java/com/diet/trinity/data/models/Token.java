package com.diet.trinity.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Token implements Parcelable {
    public boolean success;
    public String token;
    public int id;

    public Token() {

    }

    protected Token(Parcel in) {
        success = in.readByte() != 0;
        token = in.readString();
        id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(token);
        dest.writeInt(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };
}
