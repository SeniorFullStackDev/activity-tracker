package com.example.m4lv2.CutomComponents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.m4lv2.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Dataset> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textLongitude;
        public TextView textLatitude;
        public TextView textDistance;

        public MyViewHolder(View v) {
            super(v);
            textLongitude = v.findViewById(R.id.textLonggitude);
            textLatitude = v.findViewById(R.id.textLattitude);
            textDistance = v.findViewById(R.id.textDistance);
        }


    }

    public static  class Dataset {
        public String longgitude;
        public String lattitude;
        public String distance;

        public Dataset(String longgitude, String lattitude, String distance) {
            this.longgitude = longgitude;
            this.lattitude = lattitude;
            this.distance = distance;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Dataset> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);

        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textLongitude.setText(mDataset.get(position).longgitude);
        holder.textLatitude.setText(mDataset.get(position).lattitude);
        holder.textDistance.setText(mDataset.get(position).distance);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }




}