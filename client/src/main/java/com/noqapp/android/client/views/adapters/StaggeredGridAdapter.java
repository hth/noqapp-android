package com.noqapp.android.client.views.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;

import java.util.Collections;
import java.util.List;


public class StaggeredGridAdapter extends RecyclerView.Adapter {
    private List<String> dataList;

    public StaggeredGridAdapter(List<String> dataList) {
        this.dataList = dataList;
        Collections.sort(dataList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int position) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        holder.name.setText(dataList.get(position));
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox name;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }


}
