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

import com.example.m4lv2.utils.ModelHistory;
import com.example.m4lv2.utils.DBHelper;
import com.example.m4lv2.utils.Utils;
import com.example.m4lv2.utils.UtilsCalendar;
import com.example.m4lv2.utils.UtilsPreference;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 *  IntentService for handling incoming intents that are generated as a result of requesting
 *  activity updates using
 *  {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public class DetectedActivitiesIntentService extends IntentService implements SensorEventListener{

    protected static final String TAG = "DetectedActivitiesIS";
    UtilsPreference mPref;
    private static Context context;

    DBHelper mydb;

    private Timer _timer = new Timer();

    boolean debugger = false;
    long mDelay = 0;
    float currentStep = 0;
    SensorManager sensorManager;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mydb = new DBHelper(this);
        mPref = new UtilsPreference(this);
        context = getApplicationContext();
        if(debugger){
            startDebuugerTimer();
        }

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

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        int activityType = result.getMostProbableActivity().getType();
        if(activityType == DetectedActivity.RUNNING || activityType == DetectedActivity.WALKING) activityType = DetectedActivity.ON_FOOT;
        long activityTimeStamp = result.getTime();
        if(!debugger){
            if(mPref.getStart()){
                evaluateEvent(activityType, activityTimeStamp);
            }
        }

    }

    public void evaluateEvent(int activityType, long activityTimeStamp){

        int lastActivityType = mPref.getLastActivityType();

        if(mPref.getLastLogActivity() != activityType){
            float step  = currentStep - mPref.getLastStep();
            mydb.logInsert(activityType, step, mPref.getCurrentLocation(),0);
            mydb.logInsert(activityType, step, mPref.getCurrentLocation(),0);
            mPref.setLastLogActivity(activityType);
            broadCastDebugToast(Utils.getActivityString(context, activityType), "");
        }

        if(activityType != DetectedActivity.TILTING && activityType != DetectedActivity.UNKNOWN){

            if(activityType == DetectedActivity.IN_VEHICLE && lastActivityType != DetectedActivity.IN_VEHICLE){
                onVehicleStart(activityTimeStamp);
            }

            if(activityType != DetectedActivity.IN_VEHICLE && lastActivityType == DetectedActivity.IN_VEHICLE){
                onVehicleEnd(activityTimeStamp);
            }

            if(activityType == DetectedActivity.WALKING && lastActivityType != DetectedActivity.WALKING){
                onOtherActivityStart(activityTimeStamp);
            }
            if(activityType != DetectedActivity.WALKING && lastActivityType == DetectedActivity.WALKING){
                onOtherActivityEnd(activityTimeStamp, lastActivityType);
            }

            if(activityType == DetectedActivity.RUNNING && lastActivityType != DetectedActivity.RUNNING){
                onOtherActivityEnd(activityTimeStamp, lastActivityType);
            }
            if(activityType != DetectedActivity.RUNNING && lastActivityType == DetectedActivity.RUNNING){
                onOtherActivityEnd(activityTimeStamp, lastActivityType);
            }

            if(activityType == DetectedActivity.ON_FOOT && lastActivityType != DetectedActivity.ON_FOOT){
                onOtherActivityStart(activityTimeStamp);
            }
            if(activityType != DetectedActivity.ON_FOOT && lastActivityType == DetectedActivity.ON_FOOT){
                onOtherActivityEnd(activityTimeStamp, lastActivityType);
            }

            if(activityType == DetectedActivity.ON_BICYCLE && lastActivityType != DetectedActivity.ON_BICYCLE){
                onOtherActivityStart(activityTimeStamp);
            }
            if(activityType != DetectedActivity.ON_BICYCLE && lastActivityType == DetectedActivity.ON_BICYCLE){
                onOtherActivityEnd(activityTimeStamp, lastActivityType);
            }
            mPref.setLastActivityType(activityType);

        }


        checkingNewDay();

        mPref.setLastStep(currentStep);



    }


    private void onVehicleStart(long activityTimeStamp){

        mPref.setStartTime(UtilsCalendar.getCurrentTimeStampByFormate("HH:mm"));
        mPref.setStartStep(currentStep);

        //check resuming vehicle
        if(activityTimeStamp - mPref.getEndVehicleTimeStamp() < 3*60*1000){
            long vid = mPref.getLastVehicleID(); // last trek history id;
            mDelay = activityTimeStamp - mPref.getEndVehicleTimeStamp();
//            mydb.historyUpdate(mDelay / 1000, vid);
            mPref.setResummingFlag(true);
            broadCastDebugToast("CONTINUE PREVIOUS TREK!", "");
        }else{
            broadCastDebugToast("VEHICLE", "start");
        }
        mPref.setStartVehicleTimeStamp(activityTimeStamp);
    }

    private void onVehicleEnd(long activityTimeStamp){
        long duration =  activityTimeStamp - mPref.getStartVehicleTimeStamp();
        if(mPref.getResumingFlag()){
            long vid = mPref.getLastVehicleID(); // last trek history id;
            long lastDuration = mPref.getLastVehicleDuration();
            long newTime = (lastDuration +  duration) / 1000;
//            mydb.historyUpdate(newTime, vid);
            broadCastUpdateUI("ENDING TREK WITH STOPPAGE");
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

            broadCastUpdateUI("ENDING ACTIVITY OF VEHICLE");
        }
        mPref.setEndVehicleTimeStamp(activityTimeStamp);
    }

    private void onOtherActivityStart(long activityTimeStamp){
        mPref.setStartTime(UtilsCalendar.getCurrentTimeStampByFormate("HH:mm"));
        mPref.setStartStep(currentStep);
        mPref.setStartOtherTimeStamp(activityTimeStamp);
        broadCastDebugToast("OTHER", "start");
    }

    private void onOtherActivityEnd(long activityTimeStamp, int activityType){
        long duration =  activityTimeStamp - mPref.getStartOtherTimeStamp();
        float steps =  currentStep - mPref.getStartStep();
        if(steps > 500 || debugger){
            ModelHistory history = new ModelHistory();
            history.setType(activityType);
            history.setTimeStr(mPref.getStartTime());
            history.setTime((int)duration/1000);
            history.setStartLocation(mPref.getCurrentLocation());
            mydb.historyInsert(history);
            mPref.setEndOtherTimeStamp(activityTimeStamp);
            broadCastUpdateUI("ENDING ACTIVITY OF " + Utils.getActivityString(context, activityType));
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

    int process = 0;

    private void startDebuugerTimer(){

        final int[] debuggerHis = {
                DetectedActivity.STILL,
                DetectedActivity.ON_FOOT,
                DetectedActivity.ON_FOOT,
                DetectedActivity.ON_FOOT,
                DetectedActivity.ON_FOOT,
                DetectedActivity.ON_FOOT,
                DetectedActivity.ON_FOOT,
                DetectedActivity.ON_FOOT,
                DetectedActivity.ON_FOOT,
                DetectedActivity.ON_FOOT,
                DetectedActivity.STILL,
                DetectedActivity.STILL,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.STILL,
                DetectedActivity.STILL

        };

        _timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(process == debuggerHis.length){
                    _timer.cancel();
                    process = 0;
                }

                long currentTimeStamp = UtilsCalendar.getCurrentTime();
                evaluateEvent(debuggerHis[process], currentTimeStamp);

                if(process < debuggerHis.length){
                    process++;
                }

//                process = mPref.getProcess();
//                mPref.setProcess(process+1);

            }
        }, 0,30000);

    }



    private void broadCastUpdateUI(String message){
        Intent i = new Intent("broadCastUpdateActivity");
        // Data you need to pass to activity
        i.putExtra("transitionType", ActivityTransition.ACTIVITY_TRANSITION_EXIT);
        i.putExtra("message", message);
        context.sendBroadcast(i);
    }

    private void broadCastDebugToast(String message,  String se){
        Intent i = new Intent("broadCastUpdateActivity");
        i.putExtra("transitionType", ActivityTransition.ACTIVITY_TRANSITION_ENTER);
        i.putExtra("message", message);
        i.putExtra("start", se);
        context.sendBroadcast(i);
    }

    private void broadCastStartNewDay(){
        Intent i = new Intent("broadCastUpdateActivity");
        i.putExtra("transitionType", ActivityTransition.ACTIVITY_TRANSITION_ENTER);
        i.putExtra("message", "newday");
        context.sendBroadcast(i);
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent intent = new Intent("com.android.ServiceStopped");
        sendBroadcast(intent);
    }

    void checkingNewDay(){
        String currentTime = UtilsCalendar.getCurrentTimeStampByFormate("HH:mm");
        if(currentTime.equals("00:00")){
            broadCastStartNewDay();
        }
    }
}
