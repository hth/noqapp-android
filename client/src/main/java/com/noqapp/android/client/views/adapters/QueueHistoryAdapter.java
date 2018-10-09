package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonQueueHistorical;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class QueueHistoryAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OnItemClickListener listener;
    private ArrayList<JsonQueueHistorical> dataSet;

    public QueueHistoryAdapter(ArrayList<JsonQueueHistorical> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_queue_history, parent, false);
        RecyclerView.ViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        final JsonQueueHistorical jsonQueueHistorical = dataSet.get(listPosition);
        holder.tv_name.setText(jsonQueueHistorical.getDisplayName());
        holder.tv_address.setText(AppUtilities.getStoreAddress(jsonQueueHistorical.getTown(), jsonQueueHistorical.getArea()));
        holder.tv_store_rating.setText(String.valueOf(jsonQueueHistorical.getRatingCount()));
        holder.tv_business_name.setText(jsonQueueHistorical.getBusinessName());
        try {
            holder.tv_queue_join_date.setText(CommonHelper.SDF_YYYY_MM_DD_HH_MM_A.
                    format(new SimpleDateFormat(Constants.ISO8601_FMT, Locale.getDefault()).parse(jsonQueueHistorical.getCreated())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tv_token.setText(String.valueOf(jsonQueueHistorical.getTokenNumber()));
        holder.tv_queue_status.setText(jsonQueueHistorical.getQueueUserState().getDescription());
        holder.tv_business_category.setText(jsonQueueHistorical.getBizCategoryName());
        if (!TextUtils.isEmpty(jsonQueueHistorical.getDisplayImage()))
            Picasso.with(context)
                    .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, jsonQueueHistorical.getDisplayImage()))
                    .placeholder(ImageUtils.getThumbPlaceholder(context))
                    .error(ImageUtils.getThumbErrorPlaceholder(context))
                    .into(holder.iv_main);
        else {
            Picasso.with(context).load(ImageUtils.getThumbPlaceholder()).into(holder.iv_main);
        }
        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStoreItemClick(jsonQueueHistorical);
            }
        });
        holder.iv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStoreItemClick(jsonQueueHistorical);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onStoreItemClick(JsonQueueHistorical item);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_address;
        private TextView tv_queue_join_date;
        private TextView tv_token;
        private TextView tv_queue_status;
        private TextView tv_store_rating;
        private TextView tv_business_name;
        private TextView tv_business_category;
        private ImageView iv_main;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_queue_join_date = itemView.findViewById(R.id.tv_queue_join_date);
            this.tv_token = itemView.findViewById(R.id.tv_token);
            this.tv_queue_status = itemView.findViewById(R.id.tv_queue_status);
            this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
            this.tv_business_name = itemView.findViewById(R.id.tv_business_name);
            this.tv_business_category = itemView.findViewById(R.id.tv_business_category);
            this.iv_main = itemView.findViewById(R.id.iv_main);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
