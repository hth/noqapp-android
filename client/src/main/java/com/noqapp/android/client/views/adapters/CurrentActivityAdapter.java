package com.noqapp.android.client.views.adapters;

import android.content.Context;
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
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.GeoHashUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        private TextView tv_distance;
        private TextView tv_store_rating;
        private TextView tv_status;
        private ImageView iv_main;
        private ImageView iv_store_icon;
        private CardView card_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_detail = (TextView) itemView.findViewById(R.id.tv_detail);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_store_rating = (TextView) itemView.findViewById(R.id.tv_store_rating);
            this.tv_distance = (TextView) itemView.findViewById(R.id.tv_distance);
            this.tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            this.iv_main = (ImageView) itemView.findViewById(R.id.iv_main);
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
        holder.tv_distance.setText(AppUtilities.calculateDistanceInKm(
                (float) LaunchActivity.getLaunchActivity().latitute,
                (float) LaunchActivity.getLaunchActivity().longitute,
                (float) GeoHashUtils.decodeLatitude(dataSet.get(listPosition).getGeoHash()),
                (float) GeoHashUtils.decodeLongitude(dataSet.get(listPosition).getGeoHash())));
        String address = "";
        if (!TextUtils.isEmpty(jsonTokenAndQueue.getTown())) {
            address = jsonTokenAndQueue.getTown();
        }
        if (!TextUtils.isEmpty(jsonTokenAndQueue.getArea())) {
            address = jsonTokenAndQueue.getArea() + "," + address;
        }
        holder.tv_address.setText(address);
        holder.tv_status.setText(AppUtilities.getStoreOpenStatus(jsonTokenAndQueue));

        AppUtilities.setStoreDrawable(context, holder.iv_store_icon, jsonTokenAndQueue.getBusinessType(), holder.tv_store_rating);
        switch (jsonTokenAndQueue.getBusinessType()) {
            case DO:
            case HO:
                Picasso.with(context)
                        .load("https://noqapp.com/imgs/240x120/f.jpeg")
                        .into(holder.iv_main);
                break;
            default:
                Picasso.with(context)
                        .load("https://noqapp.com/imgs/240x120/b.jpeg")
                        .into(holder.iv_main);
        }
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.currentItemClick(dataSet.get(listPosition), v, listPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
