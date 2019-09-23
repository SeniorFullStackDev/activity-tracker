package com.example.m4lv2.CutomComponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.m4lv2.R;

public class SpinnerAdapter extends BaseAdapter {
    Context context;
    String[] options;
    LayoutInflater inflter;

    public SpinnerAdapter(Context applicationContext, String[] options) {
        this.context = applicationContext;
        this.options = options;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return options.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_item, null);
        TextView textView = (TextView) view.findViewById(R.id.textView);;
        textView.setText(options[i]);
        return view;
    }

    public void setItems(String[] options){
        this.options = options;
        notifyDataSetChanged();
    }
}