/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.m4lv2.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.example.m4lv2.utils.DBHelper;
import com.example.m4lv2.utils.ModelHistory;
import com.example.m4lv2.utils.Utils;
import com.example.m4lv2.utils.UtilsCalendar;
import com.example.m4lv2.utils.UtilsPreference;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_ENTER;
import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_EXIT;

/**
 *  IntentService for handling incoming intents that are generated as a result of requesting
 *  activity updates using
 *  {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public class TransitionService extends IntentService implements SensorEventListener {

    protected static final String TAG = "DetectedActivitiesIS";
    UtilsPreference mPref;
    private static Context context;
    DBHelper mydb;
    boolean debugger = false;
    float currentStep = 0;
    SensorManager sensorManager;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public TransitionService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mydb = new DBHelper(this);
        mPref = new UtilsPreference(this);
        context = getApplicationContext();
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor counterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(counterSensor != null){
            sensorManager.registerListener(this, counterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {

        if(ActivityRecognitionResult.hasResult(intent)){
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                // chronological sequence of events....
                if( event.getTransitionType() == DetectedActivity.STILL) continue;

                Log.i(TAG, "ActivityType: " + event.getActivityType());
                Log.i(TAG, "TransitionType: " + event.getTransitionType());
                String str = "Receive: " + event.getActivityType() + " - " + event.getTransitionType();

                detectEvent(event.getActivityType(), event.getTransitionType(), event.getElapsedRealTimeNanos());

            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        currentStep = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    void detectEvent(int activityType, int transitionType, long elapsedTime){

        checkingNewDay();

        if(transitionType == ACTIVITY_TRANSITION_ENTER){

            if(activityType == DetectedActivity.IN_VEHICLE){
                onVehicleStart(elapsedTime);
            }

            if(activityType == DetectedActivity.WALKING || activityType == DetectedActivity.RUNNING || activityType == DetectedActivity.ON_BICYCLE){
                onOtherActivityStart();
            }
            mPref.setStartStep(currentStep);
            mPref.setStartTime(UtilsCalendar.getCurrentTimeStampByFormate("HH:mm"));
        }

        if(transitionType == ACTIVITY_TRANSITION_EXIT){
            if(activityType == DetectedActivity.IN_VEHICLE){
                onVehicleEnd(elapsedTime);
            }

            if(activityType == DetectedActivity.WALKING || activityType == DetectedActivity.RUNNING || activityType == DetectedActivity.ON_FOOT){
                onOtherActivityEnd(elapsedTime, DetectedActivity.ON_FOOT);
            }

            if(activityType == DetectedActivity.ON_BICYCLE){
                onOtherActivityEnd(elapsedTime, DetectedActivity.ON_BICYCLE);
            }

            float step = currentStep - mPref.getStartStep();
            mydb.logInsert(activityType, step, mPref.getCurrentLocation(),0);
        }

        broadCastDetectionActivity(activityType, transitionType, elapsedTime);

    }

    private void onVehicleStart(long activityTimeStamp){
        //check resuming vehicle
//        long currentTime = UtilsCalendar.getCurrentTime();
//        if(currentTime - mPref.getEndVehicleTimeStamp() < 3*60*1000){
//            long vid = mPref.getLastVehicleID(); // last trek history id;
//            long mDelay = currentTime - mPref.getEndVehicleTimeStamp();
//            mydb.historyUpdate(mDelay / 1000, vid);
//            mPref.setResummingFlag(true);
//            Toast.makeText(this, "CONTINUE PREVIOUS TREK!", Toast.LENGTH_LONG).show();
//        }else{
//            Toast.makeText(this, "START VEHICLE TREK!", Toast.LENGTH_LONG).show();
//        }

//        mPref.setStartVehicleTimeStamp(currentTime);

    }

    private void onVehicleEnd(long activityTimeStamp){

        long currentTime = UtilsCalendar.getCurrentTime();
        long duration =  currentTime - mPref.getStartVehicleTimeStamp();
        if(mPref.getResumingFlag()){
            long vid = mPref.getLastVehicleID(); // last trek history id;
            long lastDuration = mPref.getLastVehicleDuration();
            long newTime = (lastDuration +  duration) / 1000;
//            mydb.historyUpdate(newTime, vid);
            Toast.makeText(this, "ENDING TREK WITH STOPPAGE", Toast.LENGTH_LONG).show();
            mPref.setResummingFlag(false);
        }else{
            ModelHistory history = new ModelHistory();
            history.setType(DetectedActivity.IN_VEHICLE);
            history.setTime((int)duration/1000);
            history.setStartLocation(mPref.getCurrentLocation());
            history.setTimeStr(mPref.getStartTime());

            long id = mydb.historyInsert(history);
            mPref.setLastVehicleID(id);
            mPref.setLastVehicleDuration(duration);
            Toast.makeText(this, "ENDING ACTIVITY OF VEHICLE", Toast.LENGTH_LONG).show();
        }

        mPref.setEndVehicleTimeStamp(currentTime);
    }

    private void onOtherActivityStart(){
        long currentTime = UtilsCalendar.getCurrentTime();
        mPref.setStartOtherTimeStamp(currentTime);
    }

    void checkingNewDay(){
        String currentTime = UtilsCalendar.getCurrentTimeStampByFormate("HH:mm");
        if(currentTime.equals("00:00")){
            broadCastStartNewDay();
        }
    }

    private void onOtherActivityEnd(long activityTimeStamp, int activityType){
        long currentTime = UtilsCalendar.getCurrentTime();
        long duration =  currentTime - mPref.getStartOtherTimeStamp();
//        Toast.makeText(this, "Duration"+ duration, Toast.LENGTH_LONG).show();
//        activityMainBinding.textView.setText(duration+"");

        float steps =  currentStep - mPref.getStartStep();
        if(steps > 500 || debugger){
            ModelHistory history = new ModelHistory();
            history.setType(activityType);
            history.setTimeStr(mPref.getStartTime());
            history.setTime((int)duration/1000);
            history.setStep(steps);
            history.setStartLocation(mPref.getCurrentLocation());
            mydb.historyInsert(history);
            Toast.makeText(this, "ENDING ACTIVITY OF " + Utils.getActivityString(getBaseContext(), activityType), Toast.LENGTH_LONG).show();
        }
    }

    void broadCastStartNewDay(){
        Intent i = new Intent("broadCastDetectActivity");
        i.putExtra("message", "newday");
        context.sendBroadcast(i);
    }

    void broadCastDetectionActivity(int activityType, int transitionType, long time){
        Intent i = new Intent("broadCastDetectActivity");
        // Data you need to pass to activity
        i.putExtra("message", "activity");
        i.putExtra("activityType", activityType);
        i.putExtra("transitionType", transitionType);
        i.putExtra("elapsedRealTime", time / 1000);
        context.sendBroadcast(i);
    }

}
