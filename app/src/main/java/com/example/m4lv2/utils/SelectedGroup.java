package com.example.m4lv2.utils;

public class SelectedGroup {
    int groupPosition;
    TrekHistoryModel trekHistoryModel;

    public SelectedGroup(int groupPosition, TrekHistoryModel trekHistoryModel) {
        this.groupPosition = groupPosition;
        this.trekHistoryModel = trekHistoryModel;
    }

    public int getGroupPosition() {
        return groupPosition;
    }

    public TrekHistoryModel getTrekHistoryModel() {
        return trekHistoryModel;
    }

    public int getChildSize(){
        return  trekHistoryModel.getActivityHistoryList().size();
    }
}
