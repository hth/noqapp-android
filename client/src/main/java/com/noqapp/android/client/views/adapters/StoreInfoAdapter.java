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
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.utils.GeoHashUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class StoreInfoAdapter extends RecyclerView.Adapter<StoreInfoAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<BizStoreElastic> dataSet;
    private double lat,log;

    public interface OnItemClickListener {
        void onStoreItemClick(BizStoreElastic item, View view, int pos);
    }

    private final OnItemClickListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_detail;
        private TextView tv_address;
        private TextView tv_store_rating;
        private TextView tv_distance;
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

    public StoreInfoAdapter(ArrayList<BizStoreElastic> data, Context context, OnItemClickListener listener, double lat, double log) {
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
        BizStoreElastic item = dataSet.get(listPosition);
        switch (item.getBusinessType()) {
            case DO:
            case BK:
                holder.tv_name.setText(item.getBusinessName());
                holder.tv_status.setText("");
                holder.tv_detail.setText("");
                break;
            default:
                holder.tv_name.setText(item.getDisplayName());
                holder.tv_status.setText(AppUtilities.getStoreOpenStatus(item));
                StoreHourElastic storeHourElastic = item.getStoreHourElasticList().get(AppUtilities.getTodayDay());
                String time =  Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour()) +
                        " - " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getEndHour());
                holder.tv_detail.setText(time);
        }
        String address="";
        if (!TextUtils.isEmpty(item.getTown())) {
            address = item.getTown();
        }
        if (!TextUtils.isEmpty(item.getArea())) {
            address = item.getArea() +", "+address;
        }
        if(!TextUtils.isEmpty(item.getDisplayImage()))
            Picasso.with(context)
                    .load(item.getDisplayImage())
                    .into(holder.iv_main);
        else{
            Picasso.with(context).load(R.drawable.store_default).into(holder.iv_main);
            // TODO add default images
        }
        holder.tv_address.setText(address);
        AppUtilities.setStoreDrawable(context, holder.iv_store_icon, item.getBusinessType(), holder.tv_store_rating);
        holder.tv_distance.setText(AppUtilities.calculateDistanceInKm(
                (float) lat,
                (float) log,
                (float) GeoHashUtils.decodeLatitude(item.getGeoHash()),
                (float) GeoHashUtils.decodeLongitude(item.getGeoHash())));

        holder.tv_store_rating.setText(String.valueOf(AppUtilities.round(item.getRating())));

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStoreItemClick(dataSet.get(listPosition), v, listPosition);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
