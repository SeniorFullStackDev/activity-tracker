package com.example.m4lv2.utils;


import com.example.m4lv2.utils.Utils;
import com.example.m4lv2.utils.UtilsCalendar;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;

public class ModelHistory {
    private int id;
    private int type;
    private int time;
    private String date;
    private String timeStr;
    private LatLng startLocation;
    private float distance = 0;
    float step;

    public ModelHistory()
    {
        id = 0;
        type = DetectedActivity.WALKING;
        time = 0;
        date = UtilsCalendar.getCurrentTimeStampByFormate("yyyy-MM-dd");
        timeStr = UtilsCalendar.getCurrentTimeStampByFormate("HH:mm");
        step = 0;
    }

    public ModelHistory(int _id, int _type, int _time, String _date)
    {
        id = _id;
        type = _type;
        time = _time;
        date = _date;
    }
    public void setModelHistory(int _id, int _type, int _time, String _date)
    {
        id = _id;
        type = _type;
        time = _time;
        date = _date;
    }
    public int getId(){
        return id;
    }
    public int getType(){
        return type;
    }
    public int getTime(){
        return time;
    }
    public String getDate(){
        return date;
    }
    public String getTimeStr(){
        return timeStr;
    }

    public void setId(int _id){
        id = _id;
    }
    public void setType(int arg){
        type = arg;
    }
    public void setTime(int arg){
        time = arg;
    }

    public void setDate(String arg){
        date = arg;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public float getStep() {
        return step;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
