package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.GeoHashUtils;
import com.noqapp.android.client.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class StoreInfoAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OnItemClickListener listener;
    private ArrayList<BizStoreElastic> dataSet;
    private double lat, log;

    public StoreInfoAdapter(ArrayList<BizStoreElastic> data, Context context, OnItemClickListener listener, double lat, double log) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        this.lat = lat;
        this.log = log;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_item_recent_activity, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        BizStoreElastic item = dataSet.get(listPosition);
        switch (item.getBusinessType()) {
            case DO:
            case BK:
            case HS:
                holder.tv_name.setText(item.getBusinessName());
                holder.tv_status.setText("");
                holder.tv_detail.setText("");
                break;
            default:
                holder.tv_name.setText(item.getDisplayName());
                holder.tv_status.setText(AppUtilities.getStoreOpenStatus(item));
                StoreHourElastic storeHourElastic = AppUtilities.getStoreHourElastic(item.getStoreHourElasticList());
                holder.tv_detail.setText(new AppUtilities().formatTodayStoreTiming(context, storeHourElastic));
        }
        if (!TextUtils.isEmpty(item.getDisplayImage())) {
            Picasso.get()
                    .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, item.getDisplayImage()))
                    .placeholder(ImageUtils.getThumbPlaceholder(context))
                    .error(ImageUtils.getThumbErrorPlaceholder(context))
                    .into(holder.iv_main);
        } else {
            Picasso.get().load(ImageUtils.getThumbPlaceholder()).into(holder.iv_main);
        }
        holder.tv_address.setText(AppUtilities.getStoreAddress(item.getTown(), item.getArea()));
        holder.tv_distance.setText(String.valueOf(AppUtilities.calculateDistance(
                (float) lat,
                (float) log,
                (float) GeoHashUtils.decodeLatitude(item.getGeoHash()),
                (float) GeoHashUtils.decodeLongitude(item.getGeoHash()))));

        holder.tv_store_rating.setText(String.valueOf(AppUtilities.round(item.getRating())));
        if (holder.tv_store_rating.getText().toString().equals("0.0"))
            holder.tv_store_rating.setVisibility(View.INVISIBLE);
        else
            holder.tv_store_rating.setVisibility(View.VISIBLE);
        holder.card_view.setOnClickListener((View v) -> {
            listener.onStoreItemClick(dataSet.get(listPosition), v, listPosition);
        });


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onStoreItemClick(BizStoreElastic item, View view, int pos);
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

        private MyViewHolder(View itemView) {
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
