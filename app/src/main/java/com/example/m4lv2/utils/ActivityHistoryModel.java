package com.example.m4lv2.utils;



import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.DetectedActivity;

public class ActivityHistoryModel implements Parcelable {

    public String start_time;
    public int active_time;
    public float distance;
    public int activity_type;
    public int trekLvl = 0;
    public float cost = 0;

    public ActivityHistoryModel(String start_time, int active_time, float distance, int activity_type, int isShort, float c) {
        this.start_time = start_time;
        this.active_time = active_time;
        this.distance = distance;
        this.activity_type = activity_type;
        if(activity_type == DetectedActivity.IN_VEHICLE){
            if(isShort == 0){
                trekLvl = 2;
                cost = c * distance/1000;
            }else{
                trekLvl = 1;
                cost = c * distance/1000;
            }
        }else{
            cost =  Math.min(30.0f, (float) get_time_in_minutes(active_time)) * Constants.PRICE_C;
        }
    }

    protected ActivityHistoryModel(Parcel in) {
        start_time = in.readString();
        active_time = in.readInt();
        distance = in.readFloat();
        activity_type = in.readInt();
        
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(start_time);
        dest.writeInt(active_time);
        dest.writeInt(active_time);
        dest.writeFloat(distance);
        dest.writeInt(activity_type);
        
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ActivityHistoryModel> CREATOR = new Creator<ActivityHistoryModel>() {
        @Override
        public ActivityHistoryModel createFromParcel(Parcel in) {
            return new ActivityHistoryModel(in);
        }

        @Override
        public ActivityHistoryModel[] newArray(int size) {
            return new ActivityHistoryModel[size];
        }
    };



    public void setActive_time(int active_time) {
        this.active_time += active_time;
    }

    public void setActivityLogData(String startTime, float distance, int time){

    }

    public int getActive_time_in_minutes(){
        return get_time_in_minutes(active_time);
    }

    public int get_time_in_minutes(int time) {
        return (int)(Math.ceil((float)time/60));
    }

    public float getCost(){
        return  cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
}
