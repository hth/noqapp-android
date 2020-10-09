package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.store.JsonStoreCategory;

import java.util.List;

public class MenuHeaderAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OnItemClickListener listener;
    private List<JsonStoreCategory> dataSet;
    private int selected_pos = 0;

    public MenuHeaderAdapter(List<JsonStoreCategory> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }

    public MenuHeaderAdapter setSelectedPosition(int selected_pos) {
        this.selected_pos = selected_pos;
        return this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_header_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        JsonStoreCategory jsonTokenAndQueue = dataSet.get(listPosition);
        holder.tv_menu_header.setText(jsonTokenAndQueue.getCategoryName());
        if (selected_pos == listPosition) {
            holder.ll_header.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_color_red));
            holder.tv_menu_header.setTextColor(ContextCompat.getColor(context, R.color.theme_color_red));
        } else {
            holder.ll_header.setBackgroundColor(Color.WHITE);
            holder.tv_menu_header.setTextColor(Color.BLACK);
        }
        holder.tv_menu_header.setOnClickListener((View v) -> listener.menuHeaderClick(listPosition));
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
