package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.common.model.types.BusinessTypeEnum;

import java.util.List;


public class CurrentActivityAdapter extends RecyclerView.Adapter<CurrentActivityAdapter.MyViewHolder> {
    private final Context context;
    private List<JsonTokenAndQueue> dataSet;

    public interface OnItemClickListener {
        void currentItemClick(JsonTokenAndQueue item, View view, int pos);
    }

    private final OnItemClickListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_detail;
        private TextView tv_address;
        private ImageView iv_store_icon;
        private CardView card_view;
        private TextView tv_total_value;
        private TextView tv_current_value;
        private TextView tv_estimated_time;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_detail = (TextView) itemView.findViewById(R.id.tv_detail);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_current_value = (TextView) itemView.findViewById(R.id.tv_current_value);
            this.tv_total_value = (TextView) itemView.findViewById(R.id.tv_total_value);
            this.tv_estimated_time = (TextView) itemView.findViewById(R.id.tv_estimated_time);
            this.iv_store_icon = (ImageView) itemView.findViewById(R.id.iv_store_icon);
            this.card_view = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    public CurrentActivityAdapter(List<JsonTokenAndQueue> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_item_current_activity, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        JsonTokenAndQueue jsonTokenAndQueue = dataSet.get(listPosition);
        holder.tv_name.setText(jsonTokenAndQueue.getDisplayName());

        String address = "";
        if (!TextUtils.isEmpty(jsonTokenAndQueue.getTown())) {
            address = jsonTokenAndQueue.getTown();
        }
        if (!TextUtils.isEmpty(jsonTokenAndQueue.getArea())) {
            address = jsonTokenAndQueue.getArea() + ", " + address;
        }
        holder.tv_address.setText(address);
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.currentItemClick(dataSet.get(listPosition), v, listPosition);
            }
        });


        setStoreDrawable(context, holder.iv_store_icon, jsonTokenAndQueue.getBusinessType());
        holder.tv_total_value.setText(String.valueOf(dataSet.get(listPosition).getServingNumber()));
        holder.tv_current_value.setText(String.valueOf(dataSet.get(listPosition).getToken()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }



    private  void setStoreDrawable(Context context, ImageView iv, BusinessTypeEnum bussinessType) {
        switch (bussinessType) {
            case DO:
                iv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hospital));
                iv.setColorFilter(context.getResources().getColor(R.color.bussiness_hospital));
                break;
            case BK:
                iv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bank));
                iv.setColorFilter(context.getResources().getColor(R.color.bussiness_bank));
                break;
            default:
                iv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.store));
                iv.setColorFilter(context.getResources().getColor(R.color.bussiness_store));
        }
    }

}
