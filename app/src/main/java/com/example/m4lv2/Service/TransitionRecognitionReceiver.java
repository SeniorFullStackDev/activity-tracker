package com.example.m4lv2.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;


public class TransitionRecognitionReceiver extends BroadcastReceiver {

    private String TAG = "----";

    public static final String INTENT_ACTION = "com.trekpricer.activityrecognition" +
            ".ACTION_PROCESS_ACTIVITY_TRANSITIONS";
    private int ACTIVITY_TRANSITION_ENTER = 0;
    private int ACTIVITY_TRANSITION_EXIT = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                // chronological sequence of events....
                if( event.getTransitionType() == DetectedActivity.STILL) continue;

                Log.i(TAG, "ActivityType: " + event.getActivityType());
                Log.i(TAG, "TransitionType: " + event.getTransitionType());
                String str = "Receive: " + event.getActivityType() + " - " + event.getTransitionType();

                Intent i = new Intent("broadCastDetectActivity");
                // Data you need to pass to activity
                i.putExtra("message", str);
                i.putExtra("activityType", event.getActivityType());
                i.putExtra("transitionType", event.getTransitionType());
                i.putExtra("elapsedRealTime", event.getElapsedRealTimeNanos() / 1000);
                context.sendBroadcast(i);

            }
        }
    }
}