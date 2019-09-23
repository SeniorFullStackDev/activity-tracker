package com.example.m4lv2.utils;


import android.util.Log;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrekHistoryModel {

    public String date;
    public int active_time;
    public int short_treck_time;
    public int short_treck_count;
    public int long_trek_time;
    public int long_trek_count;
    public boolean isTop = false;
    public int step = 0;
    private float distance = 0;
    private float short_trek_distance = 0;
    private float long_trek_distance = 0;

    List<ActivityHistoryModel> activityHistoryList = new ArrayList<>();


    public TrekHistoryModel(String date) {
        this.date = date;
        active_time = 0;
        this.active_time = 0;
        this.short_treck_time = 0;
        this.short_treck_count = 0;
        this.long_trek_time = 0;
        this.long_trek_count = 0;
    }


    public void setActive_time(int active_time) {
        this.active_time += active_time;
    }
    public void setDistance(float distance) {
        this.distance += distance;
    }

    public void setActivityLogData(String startTime, float distance, int time, int activity_type, int isShort, float c){

        ActivityHistoryModel activityHistoryModel = new ActivityHistoryModel(startTime, time, distance, activity_type, isShort, c);
        float cost = 30 * Constants.PRICE_C;
        for(int i = 0; i < activityHistoryList.size(); i ++){
            ActivityHistoryModel elemment = activityHistoryList.get(i);
            if(elemment != null){
                if(elemment.activity_type != DetectedActivity.IN_VEHICLE){
                    cost -= elemment.cost;
                }
            }
        }

        Log.e("TEST:", "START TIME"+startTime+" --> LEFT COST: "+ cost);

        if(activityHistoryModel.activity_type != DetectedActivity.IN_VEHICLE){
            if(cost > 0){
                activityHistoryModel.setCost(Math.min(cost, activityHistoryModel.cost));
            }else{
                activityHistoryModel.setCost(0);
            }
        }

        activityHistoryList.add(activityHistoryModel);
        
    }

    public void resetOrder(){

        if(activityHistoryList.size() > 0){
            if(activityHistoryList.get(0) != null){
                if(activityHistoryList.get(0).start_time.equals("00:00")){
                    activityHistoryList.get(0).setCost(30 * Constants.PRICE_C);
                }
            }
        }
        Collections.reverse(activityHistoryList);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void set_short_trek_time(int treck_time) {
        this.short_treck_time += treck_time;
    }

    public void set_short_trek_count(int trek_count){
        this.short_treck_count += trek_count;
    }

    public void set_long_trek_time(int trek_time){
        this.long_trek_time += trek_time;
    }

    public void set_long_trek_count(int trek_count){
        this.long_trek_count += trek_count;
    }

    public int getActive_time_in_minutes(){
        return get_time_in_minutes(active_time);
    }

    public int get_time_in_minutes(int time) {
        return (int)(Math.ceil((float)time/60));
    }

    public float getDailyCost(float c){
        float healthcost = (30.0f - (float) get_time_in_minutes(active_time)) * Constants.PRICE_C;
        if(healthcost < 0) healthcost = 0.0f;

        float longTrekCost =  c * long_trek_distance / 1000;
        float shortTrekCost = c * short_trek_distance / 1000;
        healthcost = healthcost + longTrekCost + shortTrekCost;

        Log.e("HEALTH COST", "COST => " + healthcost);
        Log.e("LONG-TREK", "COST => " + longTrekCost);
        Log.e("SHORT-TREK", "COST => " + shortTrekCost);

        return  healthcost;
    }

    public int get_total_trek_count(){
        return long_trek_count + short_treck_count;
    }

    public int get_total_trek_mins(){
        return get_time_in_minutes(short_treck_time + long_trek_time);
    }

    public List<ActivityHistoryModel> getActivityHistoryList() {
        return activityHistoryList;
    }


    public void setShort_trek_distance(float short_trek_distance) {
        this.short_trek_distance += short_trek_distance;
    }

    public void setLong_trek_distance(float long_trek_distance) {
        this.long_trek_distance += long_trek_distance;
    }
}
