package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;

import java.util.List;

public class ToothOptionAdapter extends RecyclerView.Adapter {
    private final OnItemClickListener listener;
    private List<Integer> drawableList;
    private Context context;


    public ToothOptionAdapter(List<Integer> drawableList, OnItemClickListener listener, Context context) {
        this.drawableList = drawableList;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_tooth_option_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        holder.iv_option.setBackground(ContextCompat.getDrawable(context, drawableList.get(position)));
        holder.iv_option.setOnClickListener(v -> {
            if (null != listener) {
                listener.onOptionSelected(drawableList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return drawableList.size();
    }

    public interface OnItemClickListener {
        void onOptionSelected(Integer item);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_option;
        private MyViewHolder(View itemView) {
            super(itemView);
            this.iv_option = itemView.findViewById(R.id.iv_option);
        }
    }
}

