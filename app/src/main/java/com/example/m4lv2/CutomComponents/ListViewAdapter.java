package com.example.m4lv2.CutomComponents;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.m4lv2.R;
import com.example.m4lv2.utils.ActivityHistoryModel;
import com.example.m4lv2.utils.ModelHistory;
import com.example.m4lv2.utils.TrekHistoryModel;
import com.example.m4lv2.utils.UtilsCalendar;
import com.example.m4lv2.utils.UtilsPreference;
import com.google.android.gms.location.DetectedActivity;

import java.util.HashMap;
import java.util.List;

public class ListViewAdapter extends BaseExpandableListAdapter {

    private List<TrekHistoryModel> lstGroups;
    private Context context;
    private int topIndex = 0;

    public ListViewAdapter(Context context, List<TrekHistoryModel> groups){
        // initialize class variables
        this.context = context;
        lstGroups = groups;
    }

    @Override
    public int getGroupCount() {
        // returns groups count
        return lstGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // returns items count of a group
        TrekHistoryModel  trekHistoryModel = lstGroups.get(groupPosition);
        return trekHistoryModel.getActivityHistoryList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // returns a group
        return lstGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // returns a group item
        TrekHistoryModel trekHistoryModel = (TrekHistoryModel)getGroup(groupPosition);
        return trekHistoryModel.getActivityHistoryList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // return the group id
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // returns the item id of group
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // returns if the ids are specific ( unique for each group or item)
        // or relatives
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // create main items (groups)
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.trek_history_view_holder, null);
        }
        TrekHistoryModel trekHistoryModel = (TrekHistoryModel) getGroup(groupPosition);
        Log.e("TreModel", trekHistoryModel.toString());

        initGroupView(convertView, trekHistoryModel, (topIndex == groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // create the subitems (items of groups)

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.activity_history_view_holder, null);
        }
        ActivityHistoryModel history = (ActivityHistoryModel) getChild(groupPosition, childPosition);
        initChildView(convertView, history);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // returns if the subitem (item of group) can be selected
        return true;
    }

    public void setTopIndex(int topIndex) {
        this.topIndex = topIndex;
    }


    void initGroupView(View itemView, TrekHistoryModel info, boolean topindex){

        String today = UtilsCalendar.getCurrentTimeStampByFormate("yyyy-MM-dd");

        TextView txtViewDate = itemView.findViewById(R.id.txtViewDate);
        TextView txtViewActiveMins = itemView.findViewById(R.id.txtViewActiveMins);
        TextView txtViewTreck = itemView.findViewById(R.id.txtViewTreck);
        TextView txtViewCost = itemView.findViewById(R.id.txtViewCost);

        txtViewDate.setText(UtilsCalendar.convertFormate(info.date,"yyyy-MM-dd","EEEddMMM"));
        txtViewActiveMins.setText(String.format("%d", info.getActive_time_in_minutes()));
        txtViewTreck.setText(String.format("%d/%d", info.get_total_trek_mins(), info.get_total_trek_count()));
        txtViewCost.setText(String.format("$%.2f", info.getDailyCost(new UtilsPreference(context).getSettingTreckCost())));

        if(topindex){
            if(today.equals(info.date)){
                itemView.setBackgroundColor(Color.parseColor("#bdeef9"));
                txtViewDate.setTypeface(txtViewDate.getTypeface(), Typeface.BOLD);
                txtViewActiveMins.setTypeface(txtViewActiveMins.getTypeface(), Typeface.BOLD);
                txtViewTreck.setTypeface(txtViewTreck.getTypeface(), Typeface.BOLD);
                txtViewCost.setTypeface(txtViewCost.getTypeface(), Typeface.BOLD);
            }else{
                itemView.setBackgroundColor(Color.parseColor("#e69ad3"));
            }
        }else{
            itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            txtViewDate.setTypeface(null, Typeface.NORMAL);
            txtViewActiveMins.setTypeface(null, Typeface.NORMAL);
            txtViewTreck.setTypeface(null, Typeface.NORMAL);
            txtViewCost.setTypeface(null, Typeface.NORMAL);
        }
    }

    void initChildView(View itemView, ActivityHistoryModel info){

        String today = UtilsCalendar.getCurrentTimeStampByFormate("yyyy-MM-dd");

        TextView txtViewStartTime = (TextView)itemView.findViewById(R.id.txtViewStartTime);
        TextView txtViewActiveMins = (TextView)itemView.findViewById(R.id.txtViewActiveMins);
        TextView txtViewDistance = (TextView)itemView.findViewById(R.id.txtViewDistance);
        TextView txtViewCost = (TextView)itemView.findViewById(R.id.txtViewCost);

        txtViewStartTime.setText(info.start_time);
        txtViewActiveMins.setText(String.format("%d", info.getActive_time_in_minutes()));
        txtViewDistance.setText(String.format("%.2f", info.distance / 1000));

        txtViewCost.setText(String.format("$%.2f", info.getCost()));

        if(info.trekLvl == 0){
            itemView.setBackgroundColor(Color.parseColor("#9EDF5D"));
            float cost = info.getCost();
            if(info.start_time.equals("00:00")){
                txtViewCost.setText(String.format("$%.2f", cost));
            }else{
                if(cost > 0){
                    txtViewCost.setText(String.format(" - $%.2f", cost));
                }else{
                    txtViewCost.setText(String.format("$%.2f", cost));
                }

            }

        }

        if(info.trekLvl == 1){
            itemView.setBackgroundColor(Color.parseColor("#F1EF62"));
        }

        if(info.trekLvl == 2){
            itemView.setBackgroundColor(Color.parseColor("#F1C862"));
        }


    }

    public void setLstGroups(List<TrekHistoryModel> list){
        this.lstGroups = list;
        notifyDataSetChanged();
    }
}