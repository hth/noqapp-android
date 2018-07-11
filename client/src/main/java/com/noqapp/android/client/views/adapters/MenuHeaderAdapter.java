package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonStoreCategory;

import java.util.List;


public class MenuHeaderAdapter extends RecyclerView.Adapter<MenuHeaderAdapter.MyViewHolder> {
    private final Context context;
    private final OnItemClickListener listener;
    private List<JsonStoreCategory> dataSet;
    private int selected_pos = 0;

    public MenuHeaderAdapter(List<JsonStoreCategory> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }

    public MenuHeaderAdapter setSelected_pos(int selected_pos) {
        this.selected_pos = selected_pos;
        return this;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_header_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        JsonStoreCategory jsonTokenAndQueue = dataSet.get(listPosition);
        holder.tv_menu_header.setText(jsonTokenAndQueue.getCategoryName());
        if (selected_pos == listPosition) {
            holder.ll_header.setBackgroundColor(ContextCompat.getColor(context, R.color.colorActionbar));
            holder.tv_menu_header.setTextColor(ContextCompat.getColor(context, R.color.colorActionbar));
        } else {
            holder.ll_header.setBackgroundColor(Color.WHITE);
            holder.tv_menu_header.setTextColor(Color.BLACK);
        }
        holder.tv_menu_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.menuHeaderClick(listPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void menuHeaderClick(int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_menu_header;
        private LinearLayout ll_header;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_menu_header = itemView.findViewById(R.id.tv_menu_header);
            this.ll_header = itemView.findViewById(R.id.ll_header);
        }
    }


}
