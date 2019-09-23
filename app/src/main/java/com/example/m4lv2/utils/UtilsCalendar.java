package com.example.m4lv2.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class UtilsCalendar {

    public static Calendar getFistdayOfWeek(int week, int month, int year){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.WEEK_OF_MONTH, week);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_WEEK,
                cal.getActualMinimum(Calendar.DAY_OF_WEEK));
        return cal;
    }

    public static int compare(Calendar cal1, Calendar cal2){

        int mm = cal1.get(Calendar.MONTH);
        int yy = cal1.get(Calendar.YEAR);
        int dd = cal1.get(Calendar.DAY_OF_MONTH);

        int mm2 = cal2.get(Calendar.MONTH);
        int yy2 = cal2.get(Calendar.YEAR);
        int dd2 = cal2.get(Calendar.DAY_OF_MONTH);

        if(yy > yy2) return 1;
        if(yy < yy2) return -1;

        if(mm > mm2) return 1;
        if(mm < mm2) return -1;

        if(dd > dd2) return 1;
        if(dd < dd2) return -1;

        return 0;
    }

    public static int isToday(Calendar cal1){ // 0 = true

        Calendar cal2 = Calendar.getInstance();
        int mm = cal1.get(Calendar.MONTH);
        int yy = cal1.get(Calendar.YEAR);
        int dd = cal1.get(Calendar.DAY_OF_MONTH);

        int mm2 = cal2.get(Calendar.MONTH);
        int yy2 = cal2.get(Calendar.YEAR);
        int dd2 = cal2.get(Calendar.DAY_OF_MONTH);

        if(yy > yy2) return 1;
        if(yy < yy2) return -1;

        if(mm > mm2) return 1;
        if(mm < mm2) return -1;

        if(dd > dd2) return 1;
        if(dd < dd2) return -1;

        return 0;
    }

    public static int compareMonth(Calendar cal1, Calendar cal2){

        int mm = cal1.get(Calendar.MONTH);
        int yy = cal1.get(Calendar.YEAR);
        int dd = cal1.get(Calendar.DAY_OF_MONTH);

        int mm2 = cal2.get(Calendar.MONTH);
        int yy2 = cal2.get(Calendar.YEAR);
        int dd2 = cal2.get(Calendar.DAY_OF_MONTH);

        if(yy > yy2) return 1;
        if(yy < yy2) return -1;

        if(mm > mm2) return 1;
        if(mm < mm2) return -1;

        return 0;
    }

    public static int compareWithCurrentWeek(Calendar cal){
        Calendar current = Calendar.getInstance();

        int mm = cal.get(Calendar.MONTH);
        int yy = cal.get(Calendar.YEAR);
        int ww = cal.get(Calendar.WEEK_OF_MONTH);

        int mm2 = current.get(Calendar.MONTH);
        int yy2 = current.get(Calendar.YEAR);
        int ww2 = current.get(Calendar.WEEK_OF_MONTH);

        if(yy > yy2) return 1;
        if(yy < yy2) return -1;

        if(mm > mm2) return 1;
        if(mm < mm2) return -1;

        if(ww > ww2) return 1;
        if(ww < ww2) return -1;
        return 0;
    }

    public static String getCurrentTimeStampByFormate(String pattern) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(pattern);
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public static long getCurrentTime(){
        Date now = new Date();
        return now.getTime();
    }

    public static String getDateBefore(int days, String fromDateString, String pattern){

        String result = "";
        SimpleDateFormat sdfDate = new SimpleDateFormat(pattern);
        try {
            Date myDate = sdfDate.parse(fromDateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(myDate);
            calendar.add(Calendar.DAY_OF_YEAR, days);
            Date newDate = calendar.getTime();
            result = sdfDate.format(newDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            Log.e("Error", e.getLocalizedMessage());
        }
        return  result;
    }

    public static String convertFormate(String strDate, String fromPattern,String toPattern){

        String result = "";
        SimpleDateFormat sdtDate = new SimpleDateFormat(toPattern);
        SimpleDateFormat sdfDate = new SimpleDateFormat(fromPattern);
        try {
            Date myDate = sdfDate.parse(strDate);
            result = sdtDate.format(myDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            Log.e("Error", e.getLocalizedMessage()+" by pattern:"+toPattern);
        }
        return  result;
    }


    public static int getDayDifference(String fromTime, String toTime){
        long diff = ChronoUnit.DAYS.between(
                LocalDate.parse(fromTime),
                LocalDate.parse(toTime));
        return (int)diff;
    }



}
