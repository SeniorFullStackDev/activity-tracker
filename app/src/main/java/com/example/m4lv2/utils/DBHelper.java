package com.example.m4lv2.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.m4lv2.R;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TrekDBName.db";

    public static final String TABLE_NAME_HISTORY = "history";
    public static final String HISTORY_COLUMN_ID = "id";
    public static final String HISTORY_COLUMN_TYPE = "type";
    public static final String HISTORY_COLUMN_TIME = "time";
    public static final String HISTORY_COLUMN_DATE = "date";
    public static final String HISTORY_COLUMN_IS_SHORT = "is_short";
    public static final String HISTORY_COLUMN_START_LNG = "start_lng";
    public static final String HISTORY_COLUMN_START_LAT = "start_lat";
    public static final String HISTORY_COLUMN_START_TIME = "start_time";
    public static final String HISTORY_COLUMN_DISTANCE = "distance";


    String currentDBPath= "//data//" + "com.example.trekpricer"+ "//databases//" + "TrekDBName.db";
    String backupDBPath  = "/BackupFolder/TrekDBName.db";

    Context mcontext;
    public static final int DB_VERSION = 12;

//    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DB_VERSION);
        mcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + TABLE_NAME_HISTORY
                        + " (" + HISTORY_COLUMN_ID +" integer primary key, "
                        + HISTORY_COLUMN_TYPE + " integer,"
                        + HISTORY_COLUMN_TIME + " integer,"
                        + HISTORY_COLUMN_IS_SHORT + " integer,"
                        + HISTORY_COLUMN_START_TIME + " TEXT,"
                        + HISTORY_COLUMN_START_LAT + " float,"
                        + HISTORY_COLUMN_START_LNG + " float,"
                        + HISTORY_COLUMN_DATE +" TEXT,"
                        + HISTORY_COLUMN_DISTANCE +" float )");

        db.execSQL(
                "create table tbl_log (id integer primary key," +
                        " timestamp TEXT, " +
                        "type integer, " +
                        "step_diff integer, " +
                        "start_lng float," +
                        "start_lat float, " +
                        "distance float)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        String upgradeQuery = "ALTER TABLE history ADD COLUMN distance float";
        db.execSQL(upgradeQuery);

    }



    // History functions
    public long historyInsert (ModelHistory arg) {
        Log.e("MODEL SET ==>", "type:"+arg.getType()+"time:"+arg.getTime()+"Date:"+arg.getDate());

        int mins = arg.getTime() / 60;
        float distance = arg.getDistance();

        int is_short = 0;

        if(arg.getType() == DetectedActivity.IN_VEHICLE && mins > 0){

            if(mins < 15){
                is_short = 1;
            }

            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(HISTORY_COLUMN_TYPE, arg.getType());
            contentValues.put(HISTORY_COLUMN_TIME, arg.getTime());
            contentValues.put(HISTORY_COLUMN_DATE, arg.getDate());
            contentValues.put(HISTORY_COLUMN_START_TIME, arg.getTimeStr());
            contentValues.put(HISTORY_COLUMN_DISTANCE, String.format("%.0f",distance));
            contentValues.put(HISTORY_COLUMN_IS_SHORT, is_short);
            contentValues.put(HISTORY_COLUMN_START_LAT, String.format("%.4f", arg.getStartLocation().latitude));
            contentValues.put(HISTORY_COLUMN_START_LNG, String.format("%.4f", arg.getStartLocation().longitude));

            return db.insert(TABLE_NAME_HISTORY, null, contentValues);
        }

        if(arg.getType() != DetectedActivity.IN_VEHICLE && mins >= 5){

            int stempmins = (int)(arg.getStep()/100);
            int resultMin = Math.min(stempmins, mins);

            if(arg.getType() == DetectedActivity.ON_BICYCLE){
                resultMin = mins;
            }

            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(HISTORY_COLUMN_TYPE, arg.getType());
            contentValues.put(HISTORY_COLUMN_TIME, resultMin * 60);
            contentValues.put(HISTORY_COLUMN_DATE, arg.getDate());
            contentValues.put(HISTORY_COLUMN_START_TIME, arg.getTimeStr());
            contentValues.put(HISTORY_COLUMN_DISTANCE, String.format("%.0f",distance));
            contentValues.put(HISTORY_COLUMN_START_LAT, String.format("%.4f", arg.getStartLocation().latitude));
            contentValues.put(HISTORY_COLUMN_START_LNG, String.format("%.4f", arg.getStartLocation().longitude));

            return db.insert(TABLE_NAME_HISTORY, null, contentValues);
        }

        return 0;
    }

    public boolean CheckhistoryByDate(String date, LatLng location){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {date};
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME_HISTORY, "date = ?", args);
        if(numRows == 0){
            ContentValues contentValues = new ContentValues();
            contentValues.put(HISTORY_COLUMN_TYPE, DetectedActivity.WALKING);
            contentValues.put(HISTORY_COLUMN_TIME, 0);
            contentValues.put(HISTORY_COLUMN_DATE,date);
            contentValues.put(HISTORY_COLUMN_START_TIME, "00:00");
            contentValues.put(HISTORY_COLUMN_START_LAT, String.format("%.4f", location.latitude));
            contentValues.put(HISTORY_COLUMN_START_LNG, String.format("%.4f", location.longitude));
            db.insert(TABLE_NAME_HISTORY, null, contentValues);

            db.delete("tbl_log", null, null);

            return true;
        }
        return false;
    }

    public boolean historyUpdate (long time, float distance, long id) {

        Log.e("TIME","==========>"+time+"");
        int is_short_trek = 0;
        if(time < 15 * 60){
            is_short_trek = 1;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_NAME_HISTORY + " set time = ?, distance = ?, is_short = ? where id = ? ", new String[] { Long.toString(time ), String.format("%.0f", distance), Long.toString(is_short_trek), Long.toString(id) });

        return true;
    }

    public boolean historyLocationUpdate(long id,  LatLng location){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_NAME_HISTORY + " set start_lat = ?, start_lng = ? where id = ? ", new String[] { String.format("%.4f", location.latitude), String.format("%.4f", location.longitude), Long.toString(id) });
        return  true;
    }

    public boolean logLocationUpdate(long id,  LatLng location){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE tbl_log set start_lat = ?, start_lng = ? where id = ? ", new String[] { String.format("%.4f", location.latitude), String.format("%.4f", location.longitude), Long.toString(id) });
        return  true;
    }

    public ArrayList<TrekHistoryModel> getAllHistory(String strStart, String strEnd){

        float c = new UtilsPreference(mcontext).getSettingTreckCost();

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<TrekHistoryModel> array_list = new ArrayList<TrekHistoryModel>();

        String firstDateStr = "";

        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME_HISTORY + " where date > '" + strStart + "' and date <= '" + strEnd + "' ORDER BY "+HISTORY_COLUMN_DATE+" DESC, id ASC", null );
        res.moveToFirst();

        TrekHistoryModel trekHisModel = new TrekHistoryModel(strEnd);

        while(res.isAfterLast() == false){

            ModelHistory history = new ModelHistory();

            int  type = res.getInt(res.getColumnIndex(HISTORY_COLUMN_TYPE));
            int time = res.getInt(res.getColumnIndex(HISTORY_COLUMN_TIME));
            float distance = res.getFloat(res.getColumnIndex(HISTORY_COLUMN_DISTANCE));
            int id = res.getInt(res.getColumnIndex(HISTORY_COLUMN_ID));
            int is_short = res.getInt(res.getColumnIndex(HISTORY_COLUMN_IS_SHORT));
            String date = res.getString(res.getColumnIndex(HISTORY_COLUMN_DATE));
            String activity_start_time = res.getString(res.getColumnIndex(HISTORY_COLUMN_START_TIME));

            if(date.equals(trekHisModel.date)){
                trekHisModel.setDate(date);

                if(type == DetectedActivity.IN_VEHICLE && time > 0){
                    if(is_short == 1){
                        trekHisModel.set_short_trek_count(1);
                        trekHisModel.set_short_trek_time(time);
                        trekHisModel.setShort_trek_distance(distance);
                    }else{
                        trekHisModel.set_long_trek_count(1);
                        trekHisModel.set_long_trek_time(time);
                        trekHisModel.setLong_trek_distance(distance);
                    }
                }else{
                    trekHisModel.setActive_time(time);
                    trekHisModel.setDistance(distance);
                }

                trekHisModel.setDistance(distance);

                trekHisModel.setActivityLogData(activity_start_time, distance, time, type, is_short, c);

                if(res.isLast()){
                    array_list.add(trekHisModel);
                }

                res.moveToNext();
            }else{
                trekHisModel.resetOrder();
                array_list.add(trekHisModel);
                trekHisModel = new TrekHistoryModel(date);

                if(type == DetectedActivity.IN_VEHICLE && time > 0){
                    if(is_short == 1){
                        trekHisModel.set_short_trek_count(1);
                        trekHisModel.set_short_trek_time(time);
                        trekHisModel.setShort_trek_distance(distance);
                    }else{
                        trekHisModel.set_long_trek_count(1);
                        trekHisModel.set_long_trek_time(time);
                        trekHisModel.setLong_trek_distance(distance);
                    }
                }else{
                    trekHisModel.setActive_time(time);
                    trekHisModel.setDistance(distance);
                }

                trekHisModel.setActivityLogData(activity_start_time, distance, time, type, is_short, c);

                if(res.isLast()){
                    array_list.add(trekHisModel);
                }
                res.moveToNext();
            }
        }

        Log.e("array_LIST", array_list.toString());
        return array_list;

    }


    public int get_all_activity_acounts(String topdate){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME_HISTORY + " where date <= '" + topdate + "' GROUP BY " + HISTORY_COLUMN_DATE , null );
        res.moveToFirst();
        return res.getCount();
    }


    public int get_weekly_activty_time(String strStart, String strEnd){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select "+" SUM("+HISTORY_COLUMN_TIME+") as active_time" + " from " + TABLE_NAME_HISTORY + " where date > '" + strStart + "' and date <= '" + strEnd + "' and " + HISTORY_COLUMN_TYPE + "!=" + DetectedActivity.IN_VEHICLE, null );
        res.moveToFirst();

        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        int result = 0;

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            int active_time = res.getInt(res.getColumnIndex("active_time"));
            result = active_time;
            res.moveToNext();
        }
        return (int)Math.ceil(result/60);
    }

    public float get_weekly_activty_distance(String strStart, String strEnd){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select "+" SUM("+HISTORY_COLUMN_DISTANCE+") as active_distance" + " from " + TABLE_NAME_HISTORY + " where date > '" + strStart + "' and date <= '" + strEnd + "' and " + HISTORY_COLUMN_TYPE + "!=" + DetectedActivity.IN_VEHICLE, null );
        res.moveToFirst();

        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        float result = 0;

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            float active_distance = res.getFloat(res.getColumnIndex("active_distance"));
            result = active_distance / 1000;
            res.moveToNext();
        }
        return result;
    }


    public int[] get_weekly_short_trek_time(String strStart, String strEnd){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select " + " SUM("+HISTORY_COLUMN_TIME+") as active_time ," + " COUNT("+HISTORY_COLUMN_TYPE+") as count " + " from " + TABLE_NAME_HISTORY + " where date > '" + strStart + "' and date <= '" + strEnd + "' and " + HISTORY_COLUMN_TYPE + "=" + DetectedActivity.IN_VEHICLE + " and " + HISTORY_COLUMN_IS_SHORT + "=1", null );
        res.moveToFirst();

        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        int result = 0;
        int count = 0;

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            int active_time = res.getInt(res.getColumnIndex("active_time"));
            count = res.getInt(res.getColumnIndex("count"));
            result = active_time;
            res.moveToNext();
        }
        return new int[]{ result / 60, count};
    }


    public float[] get_weekly_short_trek_distance(String strStart, String strEnd){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select " + " SUM("+HISTORY_COLUMN_DISTANCE+") as active_distance ," + " COUNT("+HISTORY_COLUMN_TYPE+") as count " + " from " + TABLE_NAME_HISTORY + " where date > '" + strStart + "' and date <= '" + strEnd + "' and " + HISTORY_COLUMN_TYPE + "=" + DetectedActivity.IN_VEHICLE + " and " + HISTORY_COLUMN_IS_SHORT + "=1", null );
        res.moveToFirst();

        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        float result = 0;
        int count = 0;

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            float active_distance = res.getFloat(res.getColumnIndex("active_distance"));
            count = res.getInt(res.getColumnIndex("count"));
            result = active_distance / 1000;
            res.moveToNext();
        }
        return new float[]{ result, count};
    }

    public int[] get_weekly_long_trek_time(String strStart, String strEnd){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select " + " SUM("+HISTORY_COLUMN_TIME+") as active_time ," + " COUNT("+HISTORY_COLUMN_TYPE+") as count " + " from " + TABLE_NAME_HISTORY + " where date > '" + strStart + "' and date <= '" + strEnd + "' and " + HISTORY_COLUMN_TYPE + "=" + DetectedActivity.IN_VEHICLE + " and " + HISTORY_COLUMN_IS_SHORT + "!=1", null );
        res.moveToFirst();

        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        int result = 0;
        int count = 0;

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            int active_time = res.getInt(res.getColumnIndex("active_time"));
            count = res.getInt(res.getColumnIndex("count"));
            result = active_time;
            res.moveToNext();
        }
        return new int[]{ result / 60, count};
    }


    public float[] get_weekly_long_trek_distance(String strStart, String strEnd){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select " + " SUM("+HISTORY_COLUMN_DISTANCE+") as active_distance ," + " COUNT("+HISTORY_COLUMN_TYPE+") as count " + " from " + TABLE_NAME_HISTORY + " where date > '" + strStart + "' and date <= '" + strEnd + "' and " + HISTORY_COLUMN_TYPE + "=" + DetectedActivity.IN_VEHICLE + " and " + HISTORY_COLUMN_IS_SHORT + "!=1", null );
        res.moveToFirst();

        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        float result = 0;
        int count = 0;

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            float active_distance = res.getFloat(res.getColumnIndex("active_distance"));
            count = res.getInt(res.getColumnIndex("count"));
            result = active_distance / 1000;
            res.moveToNext();
        }
        return new float[]{ result, count};
    }


    public float get_annual_cost_of_activity(String strStart, String strEnd){

        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        float cost = 0;
        float average = 0;
        float result = 0;
        String firstDate = getStartDate();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select " +
                HISTORY_COLUMN_DATE + "," +
                " SUM("+HISTORY_COLUMN_TIME+") as active_time " +
                " from " + TABLE_NAME_HISTORY +
                " where date > '" + strStart + "' and date <= '" + strEnd + "'" +
                " and type != "+ DetectedActivity.IN_VEHICLE +
                " GROUP BY " + HISTORY_COLUMN_DATE +
                " ORDER BY " + HISTORY_COLUMN_DATE + " DESC";

        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst();

        int realcount = 0;

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            int active_time = res.getInt(res.getColumnIndex("active_time")) / 60;
            cost += (30 - Math.min(active_time, 30))*Constants.PRICE_C;
            realcount ++;
            res.moveToNext();
        }



        int count = (int)ChronoUnit.DAYS.between(LocalDate.parse(strStart), LocalDate.parse(strEnd));

        cost += 30 * Constants.PRICE_C * (count - realcount);

        average = cost/count;
        result =  365 * average;

        Log.e("DB", "RESUTL: ==> " + cost + " COUNT: => " +count+ "START => " + strStart + " END: => " + strEnd);
        return result;
    }


    public float get_annual_cost_of_activity_by_distance(String strStart, String strEnd){

        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        float cost = 0;
        float average = 0;
        float result = 0;
        String firstDate = getStartDate();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select " +
                HISTORY_COLUMN_DATE + "," +
                " SUM("+HISTORY_COLUMN_DISTANCE+") as active_distance " +
                " from " + TABLE_NAME_HISTORY +
                " where date > '" + strStart + "' and date <= '" + strEnd + "'" +
                " and type != "+ DetectedActivity.IN_VEHICLE +
                " GROUP BY " + HISTORY_COLUMN_DATE +
                " ORDER BY " + HISTORY_COLUMN_DATE + " DESC";

        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            float active_distance = res.getFloat(res.getColumnIndex("active_distance")) / 1000;
            cost += (30 - Math.min(active_distance, 30))*Constants.PRICE_C;
            res.moveToNext();
        }

        int count = Math.min((int)ChronoUnit.DAYS.between(LocalDate.parse(firstDate), LocalDate.parse(strEnd)) + 1, 91);

        average = cost/count;
        result =  365 * average;
        return result;
    }


    public float get_annual_cost_of_short_trek(String strStart, String strEnd){


        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        float c = new UtilsPreference(mcontext).getSettingTreckCost();
        float average = 0;
        float result = 0;
        float totalDistance = 0;
        String firstDate = getStartDate();


        Log.e("date", "START:" + strStart);
        Log.e("date", "END:" + strEnd);


        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select " +
                HISTORY_COLUMN_DATE + "," +
                " SUM("+HISTORY_COLUMN_DISTANCE+") as active_distance ," +
                " COUNT("+ HISTORY_COLUMN_DATE+") as count " +
                " from " + TABLE_NAME_HISTORY +
                " where date > '" + strStart + "' and date <= '" + strEnd + "'" +
                " and type = "+ DetectedActivity.IN_VEHICLE +
                " and is_short = 1" +
                " GROUP BY " + HISTORY_COLUMN_DATE +
                " ORDER BY " + HISTORY_COLUMN_DATE + " DESC";

        Cursor res =  db.rawQuery(query, null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            float active_distance = res.getFloat(res.getColumnIndex("active_distance"));
            totalDistance += active_distance;
            res.moveToNext();
        }



        int count = (int)ChronoUnit.DAYS.between(LocalDate.parse(strStart), LocalDate.parse(strEnd));

        Log.e("COUNT", count+"");

        int distanceinKM = Math.round(totalDistance / 1000);
        average = c * distanceinKM / count;

        result =  365 * average;
        return result;
    }


    public float get_annual_cost_of_short_trek_by_distance(String strStart, String strEnd){


        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        float c = new UtilsPreference(mcontext).getSettingTreckCost();
        float average = 0;
        float result = 0;
        float totalT = 0;
        String firstDate = getStartDate();


        Log.e("date", "START:" + strStart);
        Log.e("date", "END:" + strEnd);


        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select " +
                HISTORY_COLUMN_DATE + "," +
                " SUM("+HISTORY_COLUMN_DISTANCE+") as active_distance ," +
                " COUNT("+ HISTORY_COLUMN_DATE+") as count " +
                " from " + TABLE_NAME_HISTORY +
                " where date > '" + strStart + "' and date <= '" + strEnd + "'" +
                " and type = "+ DetectedActivity.IN_VEHICLE +
                " and is_short = 1" +
                " GROUP BY " + HISTORY_COLUMN_DATE +
                " ORDER BY " + HISTORY_COLUMN_DATE + " DESC";

        Cursor res =  db.rawQuery(query, null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            float active_distance = res.getFloat(res.getColumnIndex("active_distance"));
            totalT += active_distance;
            res.moveToNext();
        }



        int count = Math.min((int)ChronoUnit.DAYS.between(LocalDate.parse(firstDate), LocalDate.parse(strEnd)) + 1, 91);

        Log.e("COUNT", count+"");

        float distance = totalT / 1000;
        average = c * distance / count;

        result =  365 * average;
        return result;
    }


    public float get_annual_cost_of_long_trek(String strStart, String strEnd){
        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        float c = new UtilsPreference(mcontext).getSettingTreckCost();
        float average = 0;
        float result = 0;
        float totalDistance = 0;
        String firstDate = getStartDate();


        Log.e("date", "START:" + strStart);
        Log.e("date", "END:" + strEnd);


        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select " +
                HISTORY_COLUMN_DATE + "," +
                " SUM("+HISTORY_COLUMN_DISTANCE+") as active_distance ," +
                " COUNT("+ HISTORY_COLUMN_DATE+") as count " +
                " from " + TABLE_NAME_HISTORY +
                " where date > '" + strStart + "' and date <= '" + strEnd + "'" +
                " and type = "+ DetectedActivity.IN_VEHICLE +
                " and is_short != 1" +
                " GROUP BY " + HISTORY_COLUMN_DATE +
                " ORDER BY " + HISTORY_COLUMN_DATE + " DESC";

        Cursor res =  db.rawQuery(query, null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            float active_distance = res.getFloat(res.getColumnIndex("active_distance"));
            totalDistance += active_distance;
            res.moveToNext();
        }



        int count = (int)ChronoUnit.DAYS.between(LocalDate.parse(strStart), LocalDate.parse(strEnd));


        int distanceinKM = Math.round(totalDistance / 1000);
        Log.e("COUNT", count+"===>"+distanceinKM);

        average = c * distanceinKM / count;
        result =  365 * average;
        return result;
    }


    public float get_annual_cost_of_long_trek_by_distance(String strStart, String strEnd){
        TrekHistoryModel hisModel = new TrekHistoryModel(strEnd);
        float c = new UtilsPreference(mcontext).getSettingTreckCost();
        float average = 0;
        float result = 0;
        float totalT = 0;
        String firstDate = getStartDate();


        Log.e("date", "START:" + strStart);
        Log.e("date", "END:" + strEnd);


        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select " +
                HISTORY_COLUMN_DATE + "," +
                " SUM("+HISTORY_COLUMN_DISTANCE+") as active_distance ," +
                " COUNT("+ HISTORY_COLUMN_DATE+") as count " +
                " from " + TABLE_NAME_HISTORY +
                " where date > '" + strStart + "' and date <= '" + strEnd + "'" +
                " and type = "+ DetectedActivity.IN_VEHICLE +
                " and is_short != 1" +
                " GROUP BY " + HISTORY_COLUMN_DATE +
                " ORDER BY " + HISTORY_COLUMN_DATE + " DESC";

        Cursor res =  db.rawQuery(query, null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            ModelHistory history = new ModelHistory();
            float active_distance = res.getFloat(res.getColumnIndex("active_distance"));
            totalT += active_distance;
            res.moveToNext();
        }



        int count = Math.min((int)ChronoUnit.DAYS.between(LocalDate.parse(firstDate), LocalDate.parse(strEnd)) + 1, 91);


        Log.e("COUNT", count+"");

        float distance = totalT / 1000;
        average = c * distance / count;

        result =  365 * average;
        return result;
    }

    public String getStartDate(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select " +
                HISTORY_COLUMN_DATE +
                " from " + TABLE_NAME_HISTORY +
                " ORDER BY " + HISTORY_COLUMN_DATE + " ASC LIMIT 1";
        Cursor res =  db.rawQuery(query, null );
        res.moveToFirst();
        String result = UtilsCalendar.getCurrentTimeStampByFormate("yyyy-MM-dd");
        while(res.isAfterLast() == false){
            result = res.getString(res.getColumnIndex(HISTORY_COLUMN_DATE));
            res.moveToNext();
        }

        return result;
    }


    // History functions
    public long logInsert (int activity_type, float step, LatLng location, float distance) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", UtilsCalendar.getCurrentTimeStampByFormate("yyyy-MM-dd HH:mm"));
        contentValues.put("type", Utils.getActivityString(mcontext, activity_type));
        contentValues.put("step_diff", Math.round(step));
        contentValues.put("start_lat", String.format("%.4f",location.latitude));
        contentValues.put("start_lng", String.format("%.4f",location.longitude));
        contentValues.put("distance", String.format("%.1f",distance));
        return db.insert("tbl_log", null, contentValues);
    }

    public void upgradeDatabase(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_NAME_HISTORY + " SET is_short = ? where time <= 900 and type = 0 ", new String[] { Long.toString(1)});
        db.needUpgrade(2);
    }

    public float getCompenSatableDistance(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from tbl_log " +
                " ORDER BY timestamp ASC LIMIT 1 OFFSET 1 ";
        Cursor res =  db.rawQuery(query, null );
        res.moveToFirst();
        float distance = 0;
        float step_diff = 0;
        String activity_type = "";
        float result = 0;
        while(res.isAfterLast() == false){
            distance = res.getFloat(res.getColumnIndex("distance"));
            step_diff = res.getFloat(res.getColumnIndex("step_diff"));
            activity_type = res.getString(res.getColumnIndex("type"));
            res.moveToNext();
        }

        if(!activity_type.equals(mcontext.getResources().getString(R.string.in_vehicle)) && !activity_type.equals(mcontext.getResources().getString(R.string.still))){
            result = distance - step_diff;
        }

        return result;
    }


}