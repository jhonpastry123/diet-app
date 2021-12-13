package com.diet.trinity.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Information implements Parcelable {
    public int id;
    public int customer_id;
    public int goal;
    public float initial_weight;
    public float weight;
    public int gender;
    public float height;
    public String birthday;
    public int gym_type;
    public int sport_type1;
    public int sport_type2;
    public int sport_type3;
    public int sport_time1;
    public int sport_time2;
    public int sport_time3;
    public float goal_weight;
    public float weekly_goal;
    public int diet_mode;
    public float neck;
    public float waist;
    public float thigh;
    public String date;
    public Information() {

    }

    protected Information(Parcel in) {
        id = in.readInt();
        customer_id = in.readInt();
        goal = in.readInt();
        initial_weight = in.readFloat();
        weight = in.readFloat();
        gender = in.readInt();
        height = in.readFloat();
        birthday = in.readString();
        gym_type = in.readInt();
        sport_type1 = in.readInt();
        sport_type2 = in.readInt();
        sport_type3 = in.readInt();
        sport_time1 = in.readInt();
        sport_time2 = in.readInt();
        sport_time3 = in.readInt();
        goal_weight = in.readFloat();
        weekly_goal = in.readFloat();
        diet_mode = in.readInt();
        date = in.readString();
        neck = in.readFloat();
        waist = in.readFloat();
        thigh = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(customer_id);
        dest.writeInt(goal);
        dest.writeFloat(initial_weight);
        dest.writeFloat(weight);
        dest.writeInt(gender);
        dest.writeFloat(height);
        dest.writeString(birthday);
        dest.writeInt(gym_type);
        dest.writeInt(sport_type1);
        dest.writeInt(sport_type2);
        dest.writeInt(sport_type3);
        dest.writeInt(sport_time1);
        dest.writeInt(sport_time2);
        dest.writeInt(sport_time3);
        dest.writeFloat(goal_weight);
        dest.writeFloat(weekly_goal);
        dest.writeInt(diet_mode);
        dest.writeString(date);
        dest.writeFloat(neck);
        dest.writeFloat(waist);
        dest.writeFloat(thigh);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Information> CREATOR = new Creator<Information>() {
        @Override
        public Information createFromParcel(Parcel in) {
            return new Information(in);
        }

        @Override
        public Information[] newArray(int size) {
            return new Information[size];
        }
    };
}
