package com.example.m4lv2.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

import com.example.m4lv2.R;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Utils {

    public static final String[] nameOfMonth = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", " "};
    public static final String[] nameOfWeekday = new String[] {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    public static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        //Find the screen dimensions to create bitmap in the same size.
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(strFileName);
            b.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int calculateTotalActivityMinues(ArrayList<ActivityHistoryModel> list){

        int totalActivityMinutes = 0;
//        for (ActivityHistoryModel item:list) {
//            if(item.type != DetectedActivity.IN_VEHICLE){
//                totalActivityMinutes += item.getActive_time_in_minutes();
//            }
//        }

        return totalActivityMinutes;
    }

    public static float calculateTotalActivityCost(ArrayList<ActivityHistoryModel> list, float h){
        h = 0.4f;
        float totalActivityCost = 0.0f;
//        for (ActivityHistoryModel item:list) {
//            if(item.type != DetectedActivity.IN_VEHICLE){
//                totalActivityCost += Math.max(30-item.getActive_time_in_minutes(), 0) * h;
//            }
//
//        }

        return totalActivityCost;
    }


    public static int calculateTotalTreckMinues(ArrayList<ActivityHistoryModel> list){

        int totalTreckMinutes = 0;
//        for (ActivityHistoryModel item:list) {
//            totalTreckMinutes += item.getTreck_time_in_minutes();
//        }
        return totalTreckMinutes;
    }

    public static int calculateTotalTreckCount(ArrayList<ActivityHistoryModel> list){

        int totalTreckcnt = 0;
//        for (ActivityHistoryModel item:list) {
//            totalTreckcnt += item.treck_count;
//        }
        return totalTreckcnt;
    }

    public static int calculateTotalShortTreckMinues(ArrayList<ActivityHistoryModel> list, int limit){

        float totalShortTreckMinutes = 0.0f;
//        for (ActivityHistoryModel item:list) {
//            if(item.treck_time < limit * 60){
//                totalShortTreckMinutes += item.treck_time;
//            }
//        }

        return (int)Math.ceil(totalShortTreckMinutes / 60);
    }

    public static int calculateTotalShortTreckCount(ArrayList<ActivityHistoryModel> list, int limit){

        int totalShortTreckCnt = 0;
//        for (ActivityHistoryModel item:list) {
//            if(item.treck_time < limit * 60){
//                totalShortTreckCnt += item.treck_count;
//            }
//        }
        return totalShortTreckCnt;
    }


    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    public static boolean checkAvailableActivity(int activityType){
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return true;
            case DetectedActivity.ON_BICYCLE:
                return true;
            case DetectedActivity.ON_FOOT:
                return true;
            case DetectedActivity.RUNNING:
                return true;
            case DetectedActivity.STILL:
                return false;
            case DetectedActivity.TILTING:
                return false;
            case DetectedActivity.UNKNOWN:
                return false;
            case DetectedActivity.WALKING:
                return true;
            default:
                return false;
        }
    }

    public static String detectedActivitiesToJson(ArrayList<DetectedActivity> detectedActivitiesList) {
        Type type = new TypeToken<ArrayList<DetectedActivity>>() {}.getType();
        return new Gson().toJson(detectedActivitiesList, type);
    }

    public static ArrayList<DetectedActivity> detectedActivitiesFromJson(String jsonArray) {
        Type listType = new TypeToken<ArrayList<DetectedActivity>>(){}.getType();
        ArrayList<DetectedActivity> detectedActivities = new Gson().fromJson(jsonArray, listType);
        if (detectedActivities == null) {
            detectedActivities = new ArrayList<>();
        }
        return detectedActivities;
    }

}