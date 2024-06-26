package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.pojos.ToothProcedure;

import java.util.List;

public class ToothOptionAdapter extends RecyclerView.Adapter {
    private final OnItemClickListener listener;
    private List<ToothProcedure> drawableList;
    private Context context;
    private boolean isFrontTooth;


    public ToothOptionAdapter(List<ToothProcedure> drawableList, OnItemClickListener listener,
                              Context context,boolean isFrontTooth) {
        this.drawableList = drawableList;
        this.listener = listener;
        this.context = context;
        this.isFrontTooth = isFrontTooth;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(isFrontTooth) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rcv_tooth_option_item, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rcv_tooth_option_item_top, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        holder.iv_option.setBackground(ContextCompat.getDrawable(context,
                AppUtils.getDrawableFromString(drawableList.get(position).getDrawable(),context)));
        holder.tooth_label.setText(drawableList.get(position).getDrawableLabel());
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
        void onOptionSelected(ToothProcedure item);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_option;
        private TextView tooth_label;
        private MyViewHolder(View itemView) {
            super(itemView);
            this.iv_option = itemView.findViewById(R.id.iv_option);
            this.tooth_label = itemView.findViewById(R.id.tooth_label);
        }
    }
}

