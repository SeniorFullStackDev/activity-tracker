package com.example.m4lv2.CutomComponents;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.m4lv2.R;
import com.example.m4lv2.utils.ActivityHistoryModel;
import com.example.m4lv2.utils.UtilsCalendar;
import com.example.m4lv2.utils.UtilsPreference;


public class ActivityHistoryViewHolder extends View {

    TextView txtViewStartTime;
    TextView txtViewActiveMins;
    TextView txtViewDistance;
    TextView txtViewCost;

    Context mContext;
    View mv;



    public ActivityHistoryViewHolder(View itemView, Context context){
        super(context);
        txtViewStartTime = (TextView)itemView.findViewById(R.id.txtViewStartTime);
        txtViewActiveMins = (TextView)itemView.findViewById(R.id.txtViewActiveMins);
        txtViewDistance = (TextView)itemView.findViewById(R.id.txtViewDistance);
        txtViewCost = (TextView)itemView.findViewById(R.id.txtViewCost);
        mContext = context;
        mv = itemView;

    }

    public void bindData(ActivityHistoryModel info){

        String today = UtilsCalendar.getCurrentTimeStampByFormate("yyyy-MM-dd");
        txtViewStartTime.setText(info.start_time);
        txtViewActiveMins.setText(String.format("%d", info.getActive_time_in_minutes()));
        txtViewDistance.setText(String.format("%0.1f", info.distance));
        txtViewCost.setText(String.format("$%.2f", info.getCost()));
        mv.setBackgroundColor(Color.parseColor("#e69ad3"));

    }



}
