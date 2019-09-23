package com.example.m4lv2.CutomComponents;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.m4lv2.R;
import com.example.m4lv2.utils.ActivityHistoryModel;
import com.example.m4lv2.utils.TrekHistoryModel;
import com.example.m4lv2.utils.UtilsCalendar;
import com.example.m4lv2.utils.UtilsPreference;

public class TrekHistoryViewHolder extends View {

    TextView txtViewDate;
    TextView txtViewActiveMins;
    TextView txtViewTreck;
    TextView txtViewCost;

    Context mContext;
    View mv;

    public TrekHistoryViewHolder(View itemView, Context context){
        super(context);
        Log.e("txtViewDate", itemView.toString());
        txtViewDate = itemView.findViewById(R.id.txtViewDate);
        txtViewActiveMins = itemView.findViewById(R.id.txtViewActiveMins);
        txtViewTreck = itemView.findViewById(R.id.txtViewTreck);
        txtViewCost = itemView.findViewById(R.id.txtViewCost);
        mContext = context;
        mv = itemView;

    }

    public void bindData(TrekHistoryModel info, boolean topindex){

        if(mv == null) return;

        String today = UtilsCalendar.getCurrentTimeStampByFormate("yyyy-MM-dd");
        Log.e("txtViewDate ---  1", txtViewDate.toString());
        Log.e("txtViewDate ---  2", info.date);
        txtViewDate.setText(UtilsCalendar.convertFormate(info.date,"yyyy-MM-dd","EEEddMMM"));
        txtViewActiveMins.setText(String.format("%d", info.getActive_time_in_minutes()));
        txtViewTreck.setText(String.format("%d/%d", info.get_total_trek_mins(), info.get_total_trek_count()));
        txtViewCost.setText(String.format("$%.2f", info.getDailyCost(new UtilsPreference(mContext).getSettingTreckCost())));
        if(topindex){
            mv.setBackgroundColor(Color.parseColor("#e69ad3"));
        }else{

            if(today.equals(info.date)){
                mv.setBackgroundColor(Color.parseColor("#bdeef9"));
                txtViewDate.setTypeface(txtViewDate.getTypeface(), Typeface.BOLD);
                txtViewActiveMins.setTypeface(txtViewActiveMins.getTypeface(), Typeface.BOLD);
                txtViewTreck.setTypeface(txtViewTreck.getTypeface(), Typeface.BOLD);
                txtViewCost.setTypeface(txtViewCost.getTypeface(), Typeface.BOLD);
            }else{
                mv.setBackgroundColor(Color.parseColor("#FFFFFF"));
                txtViewDate.setTypeface(null, Typeface.NORMAL);
                txtViewActiveMins.setTypeface(null, Typeface.NORMAL);
                txtViewTreck.setTypeface(null, Typeface.NORMAL);
                txtViewCost.setTypeface(null, Typeface.NORMAL);
            }
        }
    }


}
