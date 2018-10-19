package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.GeoHashUtils;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.common.utils.CommonHelper;

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
import java.util.List;
import java.util.Locale;


public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.MyViewHolder> {
    private final Context context;
    private final OnItemClickListener listener;
    private List<JsonTokenAndQueue> dataSet;
    private double lat, log;

    public RecentActivityAdapter(List<JsonTokenAndQueue> data, Context context, OnItemClickListener listener, double lat, double log) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        this.lat = lat;
        this.log = log;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_item_recent_activity, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        JsonTokenAndQueue jsonTokenAndQueue = dataSet.get(listPosition);
        holder.tv_name.setText(jsonTokenAndQueue.getDisplayName());
        holder.tv_distance.setText(String.valueOf(AppUtilities.calculateDistance(
                (float) lat,
                (float) log,
                (float) GeoHashUtils.decodeLatitude(jsonTokenAndQueue.getGeoHash()),
                (float) GeoHashUtils.decodeLongitude(jsonTokenAndQueue.getGeoHash()))));
        //   holder.tv_status.setText(AppUtilities.getStoreOpenStatus(item));
        holder.tv_address.setText(AppUtilities.getStoreAddress(jsonTokenAndQueue.getTown(), jsonTokenAndQueue.getArea()));
        try {
            holder.tv_detail.setText("Last visit: " + CommonHelper.SDF_YYYY_MM_DD.format(new SimpleDateFormat(Constants.ISO8601_FMT, Locale.getDefault()).parse(jsonTokenAndQueue.getServiceEndTime())));
        } catch (Exception e) {
            e.printStackTrace();
            holder.tv_detail.setText("");
        }
        holder.tv_status.setText(AppUtilities.getStoreOpenStatus(jsonTokenAndQueue));
        holder.tv_store_rating.setText(String.valueOf(AppUtilities.round(jsonTokenAndQueue.getRatingCount())));
        if (holder.tv_store_rating.getText().toString().equals("0.0"))
            holder.tv_store_rating.setVisibility(View.INVISIBLE);
        else
            holder.tv_store_rating.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(jsonTokenAndQueue.getDisplayImage())) {
            Picasso.with(context)
                    .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, jsonTokenAndQueue.getDisplayImage()))
                    .placeholder(ImageUtils.getThumbPlaceholder(context))
                    .error(ImageUtils.getThumbErrorPlaceholder(context))
                    .into(holder.iv_main);
        } else {
            Picasso.with(context).load(ImageUtils.getThumbPlaceholder()).into(holder.iv_main);
        }
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.recentItemClick(dataSet.get(listPosition), v, listPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void updateLatLong(double lat, double log) {
        this.lat = lat;
        this.log = log;
    }

    public interface OnItemClickListener {
        void recentItemClick(JsonTokenAndQueue item, View view, int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_detail;
        private TextView tv_address;
        private TextView tv_store_rating;
        private TextView tv_distance;
        private TextView tv_status;
        private ImageView iv_main;
        private CardView card_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_detail = itemView.findViewById(R.id.tv_detail);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
            this.tv_distance = itemView.findViewById(R.id.tv_distance);
            this.tv_status = itemView.findViewById(R.id.tv_status);
            this.iv_main = itemView.findViewById(R.id.iv_main);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
