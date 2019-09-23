package com.example.m4lv2;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
import com.example.m4lv2.CutomComponents.SpinnerAdapter;
import com.example.m4lv2.CutomComponents.ListViewAdapter;
import com.example.m4lv2.Service.BackgroundService;
import com.example.m4lv2.Service.TransitionService;
import com.example.m4lv2.utils.Constants;
import com.example.m4lv2.databinding.ActivityMainBinding;
import com.example.m4lv2.utils.DBHelper;
import com.example.m4lv2.utils.ModelHistory;
import com.example.m4lv2.utils.SelectedGroup;
import com.example.m4lv2.utils.TrekHistoryModel;
import com.example.m4lv2.utils.Utils;
import com.example.m4lv2.utils.UtilsCalendar;
import com.example.m4lv2.utils.UtilsPreference;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_ENTER;
import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_EXIT;

public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private static final String TAG = "MainActivity";

    DBHelper mydb;
    UtilsPreference mPref;
    String pattern = "yyyy-MM-dd";
    String mStart;
    List<TrekHistoryModel> list = new ArrayList<>();

    int REQUEST_SETTING_CODE = 100;
    int FETCH_COUNT = 7;
    boolean debugger = false;
    Timer _timer = new Timer();
    ActivityMainBinding activityMainBinding;
    String topDate = UtilsCalendar.getCurrentTimeStampByFormate(pattern);
    String initialDate = UtilsCalendar.getCurrentTimeStampByFormate(pattern);

    float currentStep = 0;
    SensorManager sensorManager;
    LocationManager mLocationManager;

    private static final long MIN_TIME = 30;
    private static final float MIN_DISTANCE = 3;

    ListViewAdapter trekHistoryListAdapter;
    int mFirstVisibleItem = 0;
    List<SelectedGroup> selectedGroups = new ArrayList<>();
    boolean isFullLoaded = false;
    int selectedTrendMode = 0;
    SpinnerAdapter mSpinnerAdapter;


    // Add this inside your class
    BroadcastReceiver broadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle b = intent.getExtras();
            String message = b.getString("message");
            int activityType = b.getInt("activityType");
            int transitionType = b.getInt("transitionType");
            long elapsedRealTime = b.getLong("elapsedRealTime");

            detectEvent(activityType, transitionType, elapsedRealTime);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);



        mydb = new DBHelper(this);
//        importCSVData();

        initIniTialDae();

        mPref = new UtilsPreference(this);
        mStart = initialDate;
        trekHistoryListAdapter = new ListViewAdapter(this, list);

        initElement();

        registerReceiver(broadcastReceiver, new IntentFilter("broadCastDetectActivity"));



        activityMainBinding.btnPlayStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startBackGroundService();
                }else{
                    stopBackgroundService();
                }
            }
        });
        activityMainBinding.btnPlayStop.setChecked(mPref.getStart());


        DebugDB.getAddressLog();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor counterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(counterSensor != null){
            sensorManager.registerListener(this, counterSensor, SensorManager.SENSOR_DELAY_UI);
        }


        if(debugger) startDebuugerTimer();


    }

    void startBackGroundService(){
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
    }

    void stopBackgroundService(){
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableLocation(true);
        checkLocationPermission();
        if(mydb.CheckhistoryByDate(UtilsCalendar.getCurrentTimeStampByFormate(pattern), mPref.getCurrentLocation())){
            list = new ArrayList<>();
            mStart = initialDate;
        }
        updateElements();
    }

    private void initElement(){
        isFullLoaded = false;
        String end = mStart;
        mStart = initialDate;
        getData(mStart, end);

        final ExpandableListView recyclerView = activityMainBinding.recyclerView;
        recyclerView.setGroupIndicator(getDrawable(R.drawable.group_indicator));

        recyclerView.setAdapter(trekHistoryListAdapter);


        recyclerView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if(list.size() > 0){
                    recyclerView.smoothScrollToPosition(mFirstVisibleItem);
                    trekHistoryListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                mFirstVisibleItem = firstVisibleItem;

                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    //call API
                    if(!isFullLoaded){
                        Log.e("ListView ","=======" + " Hit API");
                        String newEnd = mStart;
                        mStart = UtilsCalendar.getDateBefore(-FETCH_COUNT,newEnd, pattern);
                        getData(mStart, newEnd);
                        trekHistoryListAdapter.notifyDataSetChanged();
                    }

                }

                long longposition = recyclerView.getExpandableListPosition(firstVisibleItem);
                int groupPosition = recyclerView.getPackedPositionGroup(longposition);
                int childPosition = recyclerView.getPackedPositionChild(longposition);
                Log.d("Test","group: " + groupPosition + " and child: " + childPosition );
                if(groupPosition >= 0 && list.size() > 0){
                    TrekHistoryModel topitem = list.get(groupPosition);
                    if(!topDate.equals(topitem.date)){
                        topDate = topitem.date;
                        topitem.isTop = true;
                        updateDashBoard();
                        trekHistoryListAdapter.setTopIndex(groupPosition);
                    }
                }
            }
        });

        initSnipperElement();
        updateDashBoard();


//        recyclerView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                TrekHistoryModel trekHistoryModel = list.get(groupPosition);
//                selectedGroups.add(new SelectedGroup(groupPosition,trekHistoryModel));
//            }
//        });
//
//        recyclerView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                for(int i = 0; i < selectedGroups.size(); i++){
//                    SelectedGroup selectedGroup = selectedGroups.get(i);
//                    if(selectedGroup.getGroupPosition() == groupPosition){
//                        selectedGroups.remove(groupPosition);
//                    }
//                }
//            }
//        });









//        final ActivityHistoryListViewAdapter adapter = new ActivityHistoryListViewAdapter(this, list);
//        recyclerView.setAdapter(adapter);
//
//        final LinearLayoutManager finallinearLayoutManager = linearLayoutManager;
//
//        final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getBaseContext()) {
//            @Override protected int getVerticalSnapPreference() {
//                return LinearSmoothScroller.SNAP_TO_START;
//            }
//        };
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if(list.size() > 0){
//                    int firstVisiblePosition = finallinearLayoutManager.findFirstVisibleItemPosition();
//                    if (!recyclerView.canScrollVertically(1)) {
//                        String newEnd = mStart;
//                        mStart = UtilsCalendar.getDateBefore(-7,newEnd, pattern);
//                        getData(mStart, newEnd);
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    if(firstVisiblePosition == list.size() && firstVisiblePosition > 0 ) firstVisiblePosition --;
//
//                    if(newState == 0){
//                        Log.e("FIRST", firstVisiblePosition+""+newState);
//                        finallinearLayoutManager.scrollToPositionWithOffset(firstVisiblePosition, 0);
//
////                    smoothScroller.setTargetPosition(firstVisiblePosition);
////                    finallinearLayoutManager.startSmoothScroll(smoothScroller);
//                        if(firstVisiblePosition < 0) return;
//
//                        adapter.setTopIndex(firstVisiblePosition);
//                        Log.e("ERROR","=====>"+firstVisiblePosition);
//                        ActivityHistoryModel topitem = list.get(firstVisiblePosition);
//                        topDate = topitem.date;
//                        topitem.isTop = true;
//                        updateDashBoard();
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//
//            }
//        });

    }

    private void updateDashBoard(){

        if(mSpinnerAdapter == null){
            return;
        }

        String daybefore7 = UtilsCalendar.getDateBefore(-FETCH_COUNT, topDate, pattern);
        String daybefore91 = UtilsCalendar.getDateBefore(-91, topDate, pattern);

        int totalWeeklyActivityMins = mydb.get_weekly_activty_time(daybefore7, topDate);
        int[] totalWeeklyShortTrekData = mydb.get_weekly_short_trek_time(daybefore7, topDate);
        int[] totalWeeklyLongTrekData = mydb.get_weekly_long_trek_time(daybefore7, topDate);

//        int annual_cost_activity  = (int)mydb.get_annual_cost_of_activity(daybefore91, topDate) ;
//        int annual_cost_short  = (int) mydb.get_annual_cost_of_short_trek(daybefore91, topDate) ;
//        int annual_cost_long  = (int)mydb.get_annual_cost_of_long_trek(daybefore91, topDate) ;
//        int total_cost = annual_cost_activity+ annual_cost_short + annual_cost_long;

//        DecimalFormat decimalFormat = new DecimalFormat("#,###");

//        String annual_cost_activity_str = decimalFormat.format(annual_cost_activity);
//        String annual_cost_short_str = decimalFormat.format(annual_cost_short);
//        String annual_cost_long_str = decimalFormat.format(annual_cost_long);
//        String total_cost_str = decimalFormat.format(total_cost);


        float h = mPref.getSettingHealthCost();
        float c = mPref.getSettingTreckCost();


        activityMainBinding.textActiveMinWeekly.setText(String.format("%d", totalWeeklyActivityMins));
//        activityMainBinding.textActiveCostAnnual.setText(String.format("$%s",annual_cost_activity_str));

        activityMainBinding.textShortTrekMinWeekly.setText(String.format("%d/%d", totalWeeklyShortTrekData[0], totalWeeklyShortTrekData[1]));
//        activityMainBinding.textShortTrekCostAnnual.setText(String.format("$%s", annual_cost_short_str));

        activityMainBinding.textLongTrekMinWeekly.setText(String.format("%d/%d", totalWeeklyLongTrekData[0], totalWeeklyLongTrekData[1]));
//        activityMainBinding.textLongTrekCostWeekly.setText(String.format("$%s", annual_cost_long_str));

        activityMainBinding.textTotalActiveMin.setText(String.format("%d/%d", totalWeeklyShortTrekData[0] + totalWeeklyLongTrekData[0], totalWeeklyShortTrekData[1] + totalWeeklyLongTrekData[1]));
//        activityMainBinding.textTotalCost.setText(String.format("$%S", total_cost_str));

        String daybefore = UtilsCalendar.getDateBefore(-7, topDate, pattern);



        //spinner
        int count = mydb.get_all_activity_acounts(topDate);

        String[] options = getAvailableTrendOptions(count);

        mSpinnerAdapter.setItems(options);
        int days = 7;
        switch (selectedTrendMode){
            case 0:
                days = getValidDays(count, selectedTrendMode);
                daybefore = UtilsCalendar.getDateBefore(-days, topDate, pattern);
                break;
            case 1:
                days = getValidDays(count, selectedTrendMode);
                daybefore = UtilsCalendar.getDateBefore(-days, topDate, pattern);
                break;
            case 2:
                days = getValidDays(count, selectedTrendMode);
                daybefore = UtilsCalendar.getDateBefore(-days, topDate, pattern);
                break;
            case 3:
                days = getValidDays(count, selectedTrendMode);
                daybefore = UtilsCalendar.getDateBefore(-days, topDate, pattern);
                break;
        }

        Log.e("DAYS", "DAYS_DIFF:" + days + "topdate:"+topDate+"startDATE:"+daybefore);

        int annual_cost_activity  = (int)mydb.get_annual_cost_of_activity(daybefore, topDate) ;
        int annual_cost_short  = (int) mydb.get_annual_cost_of_short_trek(daybefore, topDate) ;
        int annual_cost_long  = (int)mydb.get_annual_cost_of_long_trek(daybefore, topDate) ;
        int total_cost = annual_cost_activity+ annual_cost_short + annual_cost_long;
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        String annual_cost_activity_str = decimalFormat.format(annual_cost_activity);
        String annual_cost_short_str = decimalFormat.format(annual_cost_short);
        String annual_cost_long_str = decimalFormat.format(annual_cost_long);
        String total_cost_str = decimalFormat.format(total_cost);

        activityMainBinding.textActiveCostAnnual.setText(String.format("$%s",annual_cost_activity_str));
        activityMainBinding.textShortTrekCostAnnual.setText(String.format("$%s", annual_cost_short_str));
        activityMainBinding.textLongTrekCostWeekly.setText(String.format("$%s", annual_cost_long_str));
        activityMainBinding.textTotalCost.setText(String.format("$%S", total_cost_str));
    }

    private void updateElements(){

        isFullLoaded = false;
        list = new ArrayList<>();
        mStart = UtilsCalendar.getCurrentTimeStampByFormate(pattern);
        String end = mStart;
        mStart = UtilsCalendar.getDateBefore(-FETCH_COUNT, end,pattern);
        getData(mStart, end);
        updateDashBoard();
        trekHistoryListAdapter.setLstGroups(list);

    }

    private void getData(String start, String end){
        ArrayList<TrekHistoryModel> loadedData = mydb.getAllHistory(start, end);
        Log.e("size","UPDATE UI:"+"size:"+list.size());
        list.addAll(loadedData);
        if(list.size() == 0 && loadedData.size() == 0){
            String newEnd = mStart;
            mStart = UtilsCalendar.getDateBefore(-FETCH_COUNT,newEnd, pattern);
            getData(mStart, newEnd);
            Log.e("DATALOADS", "LOADS MORE");
            trekHistoryListAdapter.notifyDataSetChanged();
        }

        if(loadedData.size() == 0 && list.size() > 0){
            isFullLoaded = true;
        }


//        if(loadedData.size() == 0){
//            isFullLoaded = true;
//            if(!isFullLoaded){
//                String newEnd = mStart;
//                mStart = UtilsCalendar.getDateBefore(-30,newEnd, pattern);
//                getData(mStart, newEnd);
//                Log.e("DATALOADS", "LOADS MORE");
//                trekHistoryListAdapter.notifyDataSetChanged();
//            }
//
//        }else{
//            list.addAll(loadedData);
//            if(!isFullLoaded){
//                String newEnd = mStart;
//                mStart = UtilsCalendar.getDateBefore(-30,newEnd, pattern);
//                getData(mStart, newEnd);
//                Log.e("DATALOADS", "LOADS MORE");
//                trekHistoryListAdapter.notifyDataSetChanged();
//            }
//        }



//        shortTrek = mydb.getAllShortTrek(start, UtilsCalendar.getCurrentTimeStampByFormate(pattern), mPref.getSettingShotTrek() * 60);

        if(debugger){

//            for(int i = 0; i < 5; i ++){
//                String start1 = UtilsCalendar.getDateBefore(-i, end, pattern);
////                ActivityHistoryModel treckHisModel1 = new ActivityHistoryModel(start1, 100, 10,2, 20.0f);
////                list.add(treckHisModel1);
//                ModelHistory history = new ModelHistory();
//                history.setDate(start1);
//                history.setTime(1200);
//                history.setType(DetectedActivity.IN_VEHICLE);
//                mydb.historyInsert(history);
//
//                history = new ModelHistory();
//                history.setDate(start1);
//                history.setTime(600);
//                history.setType(DetectedActivity.ON_BICYCLE);
//                mydb.historyInsert(history);
//
//
//                history = new ModelHistory();
//                history.setDate(start1);
//                history.setTime(300);
//                history.setType(DetectedActivity.WALKING);
//                mydb.historyInsert(history);
//
//            }

//            mydb.historyUpdate(100, 1);
        }
    }

    public void shareScreen(View v){
        share();
    }



    public void goSettingScreen(View v){
        Intent i = new Intent(MainActivity.this, SettingActivity.class);
        startActivityForResult(i, REQUEST_SETTING_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_SETTING_CODE) {
            if(resultCode == Activity.RESULT_OK){
                initElement();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        mPref.setCurrentLocation(location);

        mPref.setLastActivityDistance(location);



        //dubtger;
//        Toast.makeText(this, "Distance:"+savedata[0], Toast.LENGTH_LONG).show();
//        debuggerDataArr.add(new MyAdapter.Dataset(savedata[1]+"", savedata[2]+"", savedata[0]+""));
//        debuggerAdapter.notifyDataSetChanged();
        ///////////////


//        enableLocation(false);
//        saveLastActivityWithLocation(mPref.getLastActivityType(), new LatLng(location.getLatitude(), location.getLongitude()));

//        long lastctivity_id = mPref.getLastActivityID();
//        mydb.historyLocationUpdate(lastctivity_id, new LatLng(location.getLatitude(), location.getLongitude()));
//
//        long lastlogid = mPref.getlastLogActivityID();
//        mydb.logLocationUpdate(lastlogid, new LatLng(location.getLatitude(), location.getLongitude()));



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST);
            return;
        }


        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case Constants.LOCATION_PERMISSION_REQUEST:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    checkLocationPermission();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    private void share(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            takeScreenshot();
            // Permission has already been granted
        }

    }

    private void takeScreenshot() {
        Date now = new Date();
//        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_hh_mm_ss");
        String strNow = format.format(now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + strNow + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            sendData(imageFile);
//            sendSMS(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    public void sendData(File vcfFile){
        Uri imageUri = FileProvider.getUriForFile(
                this,
                "com.trekpricer.provider", //(use your app signature + ".provider" )
                vcfFile);
        int num = 12345;
        //  String fileString = "..."; //put the location of the file here
        Intent mmsIntent = new Intent(Intent.ACTION_SEND);
        mmsIntent.putExtra("sms_body", "input some text");
        mmsIntent.putExtra("address", num);
        mmsIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        mmsIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(mmsIntent, "Send"));
    }

    void enableLocation(boolean flag){

        if(ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST);
            return;
        }

        if(flag){
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        }else{
            mLocationManager.removeUpdates(this);
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

    void initSnipperElement(){

        int count = mydb.get_all_activity_acounts(topDate);
        Log.e("COUNT", "ACTITIVITY COUNT: ==> " + count);
        Spinner spinner = (Spinner) findViewById(R.id.options_spinner);
        String[] options = getAvailableTrendOptions(count);
        mSpinnerAdapter = new SpinnerAdapter(getApplicationContext(), options);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTrendMode = position;
                updateDashBoard();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    void detectEvent(int activityType, int transitionType, long elapsedTime){

        if(transitionType == ACTIVITY_TRANSITION_ENTER){

            if(activityType == DetectedActivity.IN_VEHICLE){
                onVehicleStart(elapsedTime);
            }

            if(activityType == DetectedActivity.WALKING || activityType == DetectedActivity.RUNNING || activityType == DetectedActivity.ON_BICYCLE){
                onOtherActivityStart();
            }

            mPref.setStartTime(UtilsCalendar.getCurrentTimeStampByFormate("HH:mm"));
            mPref.setStartStep(currentStep);
            mPref.initDistanceLocationValues();

            Toast.makeText(getBaseContext(), Utils.getActivityString(getBaseContext(),activityType)+"--->ENTER", Toast.LENGTH_LONG).show();

        }

        if(transitionType == ACTIVITY_TRANSITION_EXIT){

            mPref.setLastActivityType(activityType);
            Location currentLocation = new Location("current_location");
            currentLocation.setLatitude(mPref.getCurrentLocation().latitude);
            currentLocation.setLongitude(mPref.getCurrentLocation().longitude);
            Location prevLocation = mPref.getPrevEndLocation();
            float distance = mPref.getLastActivityDistance();
            float step = currentStep - mPref.getStartStep();

            if(activityType != DetectedActivity.STILL){
                long logid = mydb.logInsert(activityType, step, new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), distance);
                mPref.setlastLogActivityID(logid);

            }else{
                mydb.logInsert(activityType, step, new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude()), 0);
            }

            saveLastActivityWithLocation(mPref.getLastActivityType(), new LatLng(mPref.getCurrentLocation().latitude, mPref.getCurrentLocation().longitude), distance);

            mPref.setPrevEndLocation(currentLocation);


            //save exceed distance
            if(activityType == DetectedActivity.WALKING){
                float exdis = distance - step;
                mPref.setExceedDisance(exdis);
            }else{
                mPref.setExceedDisance(0);
            }

        }

        updateLocationService(transitionType, activityType);

    }

    void updateLocationService(int transitionType, int activityType){
        if(transitionType == ACTIVITY_TRANSITION_ENTER){
            if(activityType != DetectedActivity.STILL){
                enableLocation(true);
            }
        }else{
            enableLocation(false);
        }
    }

    private void onVehicleStart(long activityTimeStamp){
        //check resuming vehicle
        long currentTime = UtilsCalendar.getCurrentTime();
        if(currentTime - mPref.getEndVehicleTimeStamp() < 3*60*1000){
            long vid = mPref.getLastVehicleID(); // last trek history id;
            long mDelay = currentTime - mPref.getEndVehicleTimeStamp();
            float newDistance = mPref.getLastVehicleDistance();
            mydb.historyUpdate(mDelay / 1000, newDistance, vid);
            mPref.setResummingFlag(true);
            Toast.makeText(this, "CONTINUE PREVIOUS TREK!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "START VEHICLE TREK!", Toast.LENGTH_LONG).show();
        }

        mPref.setStartVehicleTimeStamp(currentTime);

    }

    private void onVehicleEnd(float distance){

        long currentTime = UtilsCalendar.getCurrentTime();
        long duration =  currentTime - mPref.getStartVehicleTimeStamp();
        if(mPref.getResumingFlag()){
            long vid = mPref.getLastVehicleID(); // last trek history id;
            long lastDuration = mPref.getLastVehicleDuration();
            long newTime = (lastDuration +  duration) / 1000;
            float newDistance = mPref.getLastVehicleDistance() + distance;
            mydb.historyUpdate(newTime, newDistance, vid);
            Toast.makeText(this, "ENDING TREK WITH STOPPAGE", Toast.LENGTH_LONG).show();
            mPref.setResummingFlag(false);
            mPref.setLastVehicleDuration(lastDuration +  duration);
            mPref.setLastVehicleDistance(newDistance);
        }else{

            float cdis = mPref.getExceedDisance();
            distance += cdis;

            if(distance  < 250 && !debugger){
                return;
            }

            ModelHistory history = new ModelHistory();
            history.setType(DetectedActivity.IN_VEHICLE);
            history.setTime((int)duration/1000);
            history.setStartLocation(mPref.getCurrentLocation());
            history.setTimeStr(mPref.getStartTime());
            history.setDistance(distance);

            long id = mydb.historyInsert(history);
            mPref.setLastVehicleID(id);
            mPref.setLastVehicleDuration(duration);
            mPref.setLastVehicleDistance(distance);
            mPref.setLastActivityID(id);

            Toast.makeText(this, "ENDING ACTIVITY OF VEHICLE", Toast.LENGTH_LONG).show();
        }

        mPref.setEndVehicleTimeStamp(currentTime);
    }

    private void onOtherActivityStart(){
        long currentTime = UtilsCalendar.getCurrentTime();
        mPref.setStartOtherTimeStamp(currentTime);
    }


    private void onOtherActivityEnd(int activityType, float distance){
        long currentTime = UtilsCalendar.getCurrentTime();
        long duration =  currentTime - mPref.getStartOtherTimeStamp();
//        Toast.makeText(this, "Duration"+ duration, Toast.LENGTH_LONG).show();
//        activityMainBinding.textView.setText(duration+"");

        float steps =  currentStep - mPref.getStartStep();
        float limit_step = 500;
        if(activityType == DetectedActivity.ON_BICYCLE){
            limit_step = 0;
        }

        if(steps > limit_step || debugger){
            ModelHistory history = new ModelHistory();
            history.setType(activityType);
            history.setTimeStr(mPref.getStartTime());
            history.setTime((int)duration/1000);
            history.setStep(steps);
            history.setDistance(distance);
            history.setStartLocation(mPref.getCurrentLocation());
            long insertedID = mydb.historyInsert(history);
            mPref.setLastActivityID(insertedID);
            Toast.makeText(this, "ENDING ACTIVITY OF " + Utils.getActivityString(getBaseContext(), activityType), Toast.LENGTH_LONG).show();


        }
    }

    void saveLastActivityWithLocation(int activityType, LatLng location, float distance){

        if(activityType == DetectedActivity.IN_VEHICLE){
            onVehicleEnd(distance);
        }
        if(activityType == DetectedActivity.WALKING || activityType == DetectedActivity.RUNNING || activityType == DetectedActivity.ON_FOOT){
            onOtherActivityEnd(DetectedActivity.ON_FOOT, distance);
        }

        if(activityType == DetectedActivity.ON_BICYCLE){
            onOtherActivityEnd(DetectedActivity.ON_BICYCLE, distance);
        }

        //update start
        updateElements();
        //update end

        Toast.makeText(getBaseContext(), Utils.getActivityString(getBaseContext(),activityType)+"--->EXIST", Toast.LENGTH_LONG).show();
    }



    int process = 0;

    private void startDebuugerTimer(){

        final int[][] debuggerHis = {
                {DetectedActivity.STILL, ACTIVITY_TRANSITION_ENTER, 0},
                {DetectedActivity.STILL, ACTIVITY_TRANSITION_EXIT, 30},
                {DetectedActivity.ON_FOOT, ACTIVITY_TRANSITION_ENTER, 31},
                {DetectedActivity.ON_FOOT, ACTIVITY_TRANSITION_EXIT, 100},
                {DetectedActivity.IN_VEHICLE, ACTIVITY_TRANSITION_ENTER, 101},
                {DetectedActivity.IN_VEHICLE, ACTIVITY_TRANSITION_EXIT, 400},
                {DetectedActivity.STILL, ACTIVITY_TRANSITION_ENTER, 401},
                {DetectedActivity.STILL, ACTIVITY_TRANSITION_EXIT, 520},
                {DetectedActivity.IN_VEHICLE, ACTIVITY_TRANSITION_ENTER, 521},
                {DetectedActivity.IN_VEHICLE, ACTIVITY_TRANSITION_EXIT, 700},
                {DetectedActivity.STILL, ACTIVITY_TRANSITION_ENTER, 701},
                {DetectedActivity.STILL, ACTIVITY_TRANSITION_EXIT, 820},
                {DetectedActivity.IN_VEHICLE, ACTIVITY_TRANSITION_ENTER, 821},
                {DetectedActivity.IN_VEHICLE, ACTIVITY_TRANSITION_EXIT, 1000},
                {DetectedActivity.STILL, ACTIVITY_TRANSITION_ENTER, 1001}
        };

        _timer.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        int[] selectedActivity = null;

                        for(int i = 0;  i < debuggerHis.length; i++){
                            int[] obj = debuggerHis[i];
                            if(obj[2] == process) selectedActivity = obj;
                        }

                        if(selectedActivity != null){
                            detectEvent(selectedActivity[0], selectedActivity[1], selectedActivity[2]);
                        }

                        Log.e("Debugger:", "PROCESS - Debugger"+process);
                        process++;
                    }
                });


            }
        }, 0,1000);

    }


    String[] getAvailableTrendOptions(int count){

        if(count >= 196) {
            return new String[]{"W","M","Q", "Y"};

        }

        if(count >= 35){
            return new String[]{"W","M","Q"};
        }

        if(count >= 14){
            return new String[]{"W","M"};
        }

        return new String[]{"W"};

    }

    int getValidDays(int count, int mode){
        if(mode == 0){
            return Math.min(7, count);
        }
        if(mode == 1){
            return Math.min(28, 7 * (count/7));
        }

        if(mode == 2){
            return Math.min(91, 7 * (count/7));
        }

        if(count > 300){
            return Math.min(196, 7 * (count/7));
        }

        if(count > 200){
            return Math.min(294, 7 * (count/7));
        }
        return 0;

    }

    void importCSVData(){

        InputStream is = getResources().openRawResource(R.raw.history);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line = "";
        List<TrekHistoryModel> savedArr = new ArrayList<>();
        int index = 0;
        try {
            while((line = reader.readLine()) != null){
                if(index > 0){
                    String[] stringArr = line.split(",");
                    String distance = distance = stringArr[9];
                    ModelHistory history = new ModelHistory();
                    history.setType(Integer.valueOf(stringArr[1]));
                    history.setTime(Integer.valueOf(stringArr[2]));
                    history.setDate(stringArr[3]);

                    history.setDistance(Float.valueOf(distance));
                    history.setTimeStr(stringArr[5]);
                    history.setStartLocation(new LatLng(Float.valueOf(stringArr[7]), Float.valueOf(stringArr[6])));

                    long id = mydb.historyInsert(history);
                    Log.e("DB INSERT", "INSERTED ID" + id);
                }
                index ++;
            }
        }catch (IOException e){
            Log.e("MainActivity", "Error:" + line + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    void initIniTialDae(){

        String end = initialDate;
        String start = UtilsCalendar.getDateBefore(-FETCH_COUNT, end,pattern);
        ArrayList<TrekHistoryModel> loadedData = mydb.getAllHistory(start, end);

        if(loadedData.size() == 0){
            initialDate = mydb.getStartDate();
        }
    }



}
