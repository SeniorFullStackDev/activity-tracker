/*
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

package com.example.m4lv2.utils;

import com.google.android.gms.location.DetectedActivity;

/**
 * Constants used in this sample.
 */
public final class Constants {

    private Constants() {}

    private static final String PACKAGE_NAME =
            "com.google.android.gms.location.activityrecognition";

    public static final String KEY_ACTIVITY_UPDATES_REQUESTED = PACKAGE_NAME +
            ".ACTIVITY_UPDATES_REQUESTED";

    public  static final String KEY_DETECTED_ACTIVITIES = PACKAGE_NAME + ".DETECTED_ACTIVITIES";
    public  static final String LAST_VEHICLE_ID = PACKAGE_NAME + ".LAST_VEHICLE_ID";
    public  static final String LAST_ACTIVITY_ID = PACKAGE_NAME + ".LAST_ACTIVITY_ID";
    public  static final String LAST_ACTIVITY_LOG_ID = PACKAGE_NAME + ".LAST_ACTIVITY_LOG_ID";
    public  static final String LAST_VEHICLE_DURATION = PACKAGE_NAME + ".LAST_VEHICLE_DURATION";
    public  static final String LAST_VEHICLE_DISTANCE = PACKAGE_NAME + ".LAST_VEHICLE_DISTANCE";
    public  static final String KEY_LAST_ACTIVE_TYPE = PACKAGE_NAME + ".LAST_ACTIVE_TYPE";
    public  static final String LAST_LOG_ACTIVE_TYPE = PACKAGE_NAME + ".LAST_LOG_ACTIVE_TYPE";
    public  static final String KEY_START_VEHICL_TIMESTAMP = PACKAGE_NAME + ".KEY_START_VEHICL_TIMESTAMP";
    public  static final String KEY_END_VEHICL_TIMESTAMP = PACKAGE_NAME + ".KEY_END_VEHICL_TIMESTAMP";
    public  static final String KEY_START_OTHER_TIMESTAMP = PACKAGE_NAME + ".KEY_START_OTHER_TIMESTAMP";
    public  static final String KEY_END_OTHER_TIMESTAMP = PACKAGE_NAME + ".KEY_END_OTHER_TIMESTAMP";
    public  static final String KEY_RESUMING_FLAG = PACKAGE_NAME + ".KEY_RESUMING_FLAG";
    public  static final String KEY_START_STEP = PACKAGE_NAME + ".KEY_START_STEP";
    public  static final String KEY_LAST_STEP = PACKAGE_NAME + ".KEY_LAST_STEP";
    public  static final String KEY_START_FLAG = PACKAGE_NAME + ".KEY_START_FLAG";
    public  static final String KEY_START_TIME_FLAG = PACKAGE_NAME + ".KEY_START_TIME_FLAG";

    public  static final String KEY_CURRENT_LOCATION_LAT = PACKAGE_NAME + ".KEY_CURRENT_LOCATION_LAT";
    public  static final String KEY_CURRENT_LOCATION_LNG = PACKAGE_NAME + ".KEY_CURRENT_LOCATION_LNG";
    public  static final String KEY_PREV_LOCATION_LAT = PACKAGE_NAME + ".KEY_PREV_LOCATION_LAT";
    public  static final String KEY_PREV_LOCATION_LNG = PACKAGE_NAME + ".KEY_PREV_LOCATION_LNG";

    public  static final String KEY_LAST_LOCATION_LAT = PACKAGE_NAME + ".KEY_LAST_LOCATION_LAT";
    public  static final String KEY_LAST_LOCATION_LNG = PACKAGE_NAME + ".KEY_LAST_LOCATION_LNG";
    public  static final String KEY_LAST_DISTANCE = PACKAGE_NAME + ".KEY_LAST_DISTANCE";
    public  static final String KEY_EXCEED_DISTANCE = PACKAGE_NAME + ".KEY_EXCEED_DISTANCE";

    public  static final int LOCATION_PERMISSION_REQUEST = 1;
    public  static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 103;

    public  static final int FOREGROUND_SERVICE_NOTIFICATION_ID  = 200;

    public  static final float PRICE_C  = 0.5f;

    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate.
     */
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 30 * 1000; // 30 seconds
    /**
     * List of DetectedActivity types that we monitor in this sample.
     */
    static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };
}
