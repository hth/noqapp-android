package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.merchant.R;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        holder.tv_menu_header.setText(dataSet.get(listPosition).getCategoryName());
        if (selected_pos == listPosition) {
            holder.ll_header.setBackgroundColor(ContextCompat.getColor(context, R.color.color_action_bar));
            holder.tv_menu_header.setTextColor(ContextCompat.getColor(context, R.color.color_action_bar));
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
