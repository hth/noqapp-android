package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.GeoHashUtils;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_recent_activity, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        BizStoreElastic item = dataSet.get(listPosition);
        switch (item.getBusinessType()) {
            case DO:
            case CD:
            case CDQ:
            case BK:
            case HS:
                holder.tv_name.setText(item.getBusinessName());
                holder.tv_timing.setText("");
                break;
            default:
                holder.tv_name.setText(item.getDisplayName());
                StoreHourElastic storeHourElastic = AppUtils.getStoreHourElastic(item.getStoreHourElasticList());
                holder.tv_timing.setText(new AppUtils().formatTodayStoreTiming(context, storeHourElastic));
        }
        if (!TextUtils.isEmpty(item.getDisplayImage())) {
            Picasso.get()
                    .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, item.getDisplayImage()))
                    .placeholder(ImageUtils.getThumbPlaceholder(context))
                    .error(ImageUtils.getThumbErrorPlaceholder(context))
                    .into(holder.iv_main);
        } else {
            Picasso.get().load(ImageUtils.getThumbPlaceholder()).into(holder.iv_main);
        }
        holder.tv_address.setText(AppUtils.getStoreAddress(item.getTown(), item.getArea()));
        switch (item.getBusinessType()) {
            case CD:
            case CDQ:
                holder.tv_distance.setVisibility(View.INVISIBLE);
                holder.tv_distance_unit.setVisibility(View.INVISIBLE);
                holder.tv_distance_away.setVisibility(View.INVISIBLE);
                holder.rl_distance.setVisibility(View.INVISIBLE);
                break;
            default:
                holder.tv_distance.setText(String.valueOf(AppUtils.calculateDistance(
                        (float) lat,
                        (float) log,
                        (float) GeoHashUtils.decodeLatitude(item.getGeoHash()),
                        (float) GeoHashUtils.decodeLongitude(item.getGeoHash()))));
                holder.tv_distance_unit.setText(LaunchActivity.DISTANCE_UNIT);
        }

        holder.tv_store_rating.setText(String.valueOf(AppUtils.round(item.getRating())));
        if (holder.tv_store_rating.getText().toString().equals("0.0")) {
            holder.tv_store_rating.setVisibility(View.INVISIBLE);
        } else {
            holder.tv_store_rating.setVisibility(View.VISIBLE);
        }
        holder.card_view.setOnClickListener((View v) -> listener.onStoreItemClick(dataSet.get(listPosition)));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onStoreItemClick(BizStoreElastic item);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_address;
        private TextView tv_timing;
        private TextView tv_store_rating;
        private RelativeLayout rl_distance;
        private TextView tv_distance;
        private TextView tv_distance_unit;
        private TextView tv_distance_away;
        private ImageView iv_main;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_timing = itemView.findViewById(R.id.tv_detail);
            this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
            this.rl_distance = itemView.findViewById(R.id.rl_distance);
            this.tv_distance = itemView.findViewById(R.id.tv_distance);
            this.tv_distance_unit = itemView.findViewById(R.id.tv_distance_unit);
            this.tv_distance_away = itemView.findViewById(R.id.tv_distance_away);
            this.iv_main = itemView.findViewById(R.id.iv_main);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
