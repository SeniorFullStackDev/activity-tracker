package com.example.m4lv2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;

public class UtilsPreference {

    SharedPreferences sharedPreferences;
    Context context;

    public UtilsPreference(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

    public String getSettingEmail() {
        return sharedPreferences.getString("setting_email", "");
    }

    public void setSettingEmail(String str) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("setting_email", str);
        editor.commit();
    }

    public float getSettingHealthCost() {
        return sharedPreferences.getFloat("setting_health_cost", 0.3f);
    }

    public void setSettingHealthCost(float h) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("setting_health_cost", h);
        editor.commit();
    }

    public float getSettingTreckCost() {
        return sharedPreferences.getFloat("setting_treck_cost", 0.6f);
    }

    public void setSettingTreckCost(float c) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("setting_treck_cost", c);
        editor.commit();
    }

    public int getSettingShotTrek() {
        return sharedPreferences.getInt("setting_short_treck", 15);
    }

    public void setSettingShorTrek(int st) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("setting_short_treck", st);
        editor.commit();
    }

    public String getSettingHelpInfo() {
        return sharedPreferences.getString("setting_helpinfo", "http://www.treck.pricer.com");
    }

    public void setSettingHelpInfo(String infourl) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("setting_helpinfo", infourl);
        editor.commit();
    }

    public void setDetectedActivities(String json){
        sharedPreferences.edit()
                .putString(Constants.KEY_DETECTED_ACTIVITIES, json)
                .apply();
    }

    public long getLastVehicleID(){
        return  sharedPreferences.getLong(Constants.LAST_VEHICLE_ID, 0);
    }

    public void setLastVehicleID(long id){
        sharedPreferences.edit()
                .putLong(Constants.LAST_VEHICLE_ID, id)
                .apply();
    }

    public long getLastActivityID(){
        return  sharedPreferences.getLong(Constants.LAST_ACTIVITY_ID, 0);
    }

    public void setLastActivityID(long id){
        sharedPreferences.edit()
                .putLong(Constants.LAST_ACTIVITY_ID, id)
                .apply();
    }


    public long getlastLogActivityID(){
        return  sharedPreferences.getLong(Constants.LAST_ACTIVITY_LOG_ID, 0);
    }

    public void setlastLogActivityID(long id){
        sharedPreferences.edit()
                .putLong(Constants.LAST_ACTIVITY_LOG_ID, id)
                .apply();
    }

    public long getLastVehicleDuration(){
        return  sharedPreferences.getLong(Constants.LAST_VEHICLE_DURATION, 0);
    }

    public void setLastVehicleDuration(long time){
        sharedPreferences.edit()
                .putLong(Constants.LAST_VEHICLE_DURATION, time)
                .apply();
    }


    public float getLastVehicleDistance(){
        return  sharedPreferences.getFloat(Constants.LAST_VEHICLE_DISTANCE, 0);
    }

    public void setLastVehicleDistance(float distance){
        sharedPreferences.edit()
                .putFloat(Constants.LAST_VEHICLE_DISTANCE, distance)
                .apply();
    }

    public void setLastActivityType(int type){
        sharedPreferences.edit()
                .putInt(Constants.KEY_LAST_ACTIVE_TYPE, type)
                .apply();
    }

    public int getLastLogActivity(){
        return sharedPreferences.getInt(Constants.LAST_LOG_ACTIVE_TYPE, DetectedActivity.STILL);
    }


    public void setLastLogActivity(int type){
        sharedPreferences.edit()
                .putInt(Constants.LAST_LOG_ACTIVE_TYPE, type)
                .apply();
    }

    public int getLastActivityType(){
        return sharedPreferences.getInt(Constants.KEY_LAST_ACTIVE_TYPE, DetectedActivity.STILL);
    }


    //for vehicle
    public void setStartVehicleTimeStamp(long timestamp){
        sharedPreferences.edit()
                .putLong(Constants.KEY_START_VEHICL_TIMESTAMP, timestamp)
                .apply();
    }

    public long getStartVehicleTimeStamp(){
        return sharedPreferences.getLong(Constants.KEY_START_VEHICL_TIMESTAMP,UtilsCalendar.getCurrentTime());
    }


    public void setEndVehicleTimeStamp(long timestamp){
        sharedPreferences.edit()
                .putLong(Constants.KEY_END_VEHICL_TIMESTAMP, timestamp)
                .apply();
    }

    public long getEndVehicleTimeStamp(){
        return sharedPreferences.getLong(Constants.KEY_END_VEHICL_TIMESTAMP, 0);
    }

    //for other
    public void setStartOtherTimeStamp(long timestamp){
        sharedPreferences.edit()
                .putLong(Constants.KEY_START_OTHER_TIMESTAMP, timestamp)
                .apply();
    }

    public long getStartOtherTimeStamp(){
        return sharedPreferences.getLong(Constants.KEY_START_OTHER_TIMESTAMP, UtilsCalendar.getCurrentTime());
    }


    public void setEndOtherTimeStamp(long timestamp){
        sharedPreferences.edit()
                .putLong(Constants.KEY_END_OTHER_TIMESTAMP, timestamp)
                .apply();
    }

    public long getEndOtherTimeStamp(){
        return sharedPreferences.getLong(Constants.KEY_END_OTHER_TIMESTAMP, UtilsCalendar.getCurrentTime());
    }


    public void setResummingFlag(boolean flag){
        sharedPreferences.edit()
                .putBoolean(Constants.KEY_RESUMING_FLAG, flag)
                .apply();
    }

    public boolean getResumingFlag(){
        return sharedPreferences.getBoolean(Constants.KEY_RESUMING_FLAG, false);
    }


    public void setStartStep(float step){
        sharedPreferences.edit()
                .putFloat(Constants.KEY_START_STEP, step)
                .apply();
    }

    public float getStartStep(){
        return sharedPreferences.getFloat(Constants.KEY_START_STEP, 0);
    }


    public void setLastStep(float step){
        sharedPreferences.edit()
                .putFloat(Constants.KEY_LAST_STEP, step)
                .apply();
    }

    public float getLastStep(){
        return sharedPreferences.getFloat(Constants.KEY_LAST_STEP, 0);
    }


    public void setStart(boolean flag){
        sharedPreferences.edit().putBoolean(Constants.KEY_START_FLAG, flag).apply();
    }

    public boolean getStart(){
        return sharedPreferences.getBoolean(Constants.KEY_START_FLAG, false);
    }


    public void setStartTime(String startime){
        sharedPreferences.edit().putString(Constants.KEY_START_TIME_FLAG, startime).apply();
    }

    public String getStartTime(){
        return sharedPreferences.getString(Constants.KEY_START_TIME_FLAG, UtilsCalendar.getCurrentTimeStampByFormate("HH:mm"));
    }

    public void setProcess(int process){
        sharedPreferences.edit().putInt("process", process).apply();
    }

    public int getProcess(){
        return sharedPreferences.getInt("process", 0);
    }


    public void setCurrentLocation(Location location){
        sharedPreferences.edit().putFloat(Constants.KEY_CURRENT_LOCATION_LAT, (float) location.getLatitude()).putFloat(Constants.KEY_CURRENT_LOCATION_LNG, (float)location.getLongitude()).apply();
    }

    public LatLng getCurrentLocation(){
        double log = sharedPreferences.getFloat(Constants.KEY_CURRENT_LOCATION_LNG, 0);
        double lat = sharedPreferences.getFloat(Constants.KEY_CURRENT_LOCATION_LAT, 0);
        return new LatLng(lat, log);
    }


    public void setPrevEndLocation(Location location){
        sharedPreferences.edit().putFloat(Constants.KEY_PREV_LOCATION_LAT, (float) location.getLatitude()).putFloat(Constants.KEY_PREV_LOCATION_LNG, (float)location.getLongitude()).apply();
    }

    public Location getPrevEndLocation(){
        double lng = sharedPreferences.getFloat(Constants.KEY_PREV_LOCATION_LNG, 0);
        double lat = sharedPreferences.getFloat(Constants.KEY_PREV_LOCATION_LAT, 0);
        Location location = new Location("prev_location");
        location.setLatitude(lat);
        location.setLongitude(lng);
        return location;
    }

    public void initDistanceLocationValues(){

       LatLng currentlatlng =  getCurrentLocation();
        sharedPreferences.edit()
                .putFloat(Constants.KEY_LAST_DISTANCE, 0)
                .putFloat(Constants.KEY_LAST_LOCATION_LNG, (float) currentlatlng.longitude)
                .putFloat(Constants.KEY_LAST_LOCATION_LAT, (float) currentlatlng.latitude);

    }

    public float[] setLastActivityDistance(Location location){

        float newDistance = sharedPreferences.getFloat(Constants.KEY_LAST_DISTANCE, 0);
        float log = sharedPreferences.getFloat(Constants.KEY_LAST_LOCATION_LNG, 0);
        float lat = sharedPreferences.getFloat(Constants.KEY_LAST_LOCATION_LAT, 0);

        Location lastLocation = new Location("last_location");
        lastLocation.setLatitude(lat);
        lastLocation.setLongitude(log);

        if(log != 0){
            newDistance = newDistance + lastLocation.distanceTo(location);
        }

        sharedPreferences.edit().
                putFloat(Constants.KEY_LAST_DISTANCE, newDistance)
                .putFloat(Constants.KEY_LAST_LOCATION_LNG, (float) location.getLongitude())
                .putFloat(Constants.KEY_LAST_LOCATION_LAT, (float) location.getLatitude())
                .apply();

        return  new float[]{ newDistance, log, lat};

    }

    public float getLastActivityDistance(){
        float distance = sharedPreferences.getFloat(Constants.KEY_LAST_DISTANCE, 0);
        sharedPreferences.edit().putFloat(Constants.KEY_LAST_DISTANCE, 0).apply();
        return distance;
    }

    public void setExceedDisance(float distance){
        sharedPreferences.edit().putFloat(Constants.KEY_EXCEED_DISTANCE, distance).apply();
    }

    public float getExceedDisance(){
        return  sharedPreferences.getFloat(Constants.KEY_EXCEED_DISTANCE, 0);
    }

}
