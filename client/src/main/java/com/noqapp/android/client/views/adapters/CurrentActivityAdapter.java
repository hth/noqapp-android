package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.GeoHashUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.squareup.picasso.Picasso;

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

        private LinearLayout ll_change_bg;



        private TextView tv_total_value;
        private TextView tv_current_value;
        private TextView tv_how_long;
        private TextView tv_estimated_time;
        private TextView tv_after;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_detail = (TextView) itemView.findViewById(R.id.tv_detail);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_store_rating = (TextView) itemView.findViewById(R.id.tv_store_rating);
            this.tv_distance = (TextView) itemView.findViewById(R.id.tv_distance);
            this.tv_status = (TextView) itemView.findViewById(R.id.tv_status);

            this.tv_current_value = (TextView) itemView.findViewById(R.id.tv_current_value);
            this.tv_total_value = (TextView) itemView.findViewById(R.id.tv_total_value);
            this.tv_how_long = (TextView) itemView.findViewById(R.id.tv_how_long);
            this.tv_estimated_time = (TextView) itemView.findViewById(R.id.tv_estimated_time);
            this.tv_after = (TextView) itemView.findViewById(R.id.tv_after);

            this.iv_main = (ImageView) itemView.findViewById(R.id.iv_main);
            this.iv_store_icon = (ImageView) itemView.findViewById(R.id.iv_store_icon);

            this.ll_change_bg = (LinearLayout) itemView.findViewById(R.id.ll_change_bg);
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
        if(!TextUtils.isEmpty(jsonTokenAndQueue.getDisplayImage()))
            Picasso.with(context)
                    .load(jsonTokenAndQueue.getDisplayImage())
                    .into(holder.iv_main);
        else{
            Picasso.with(context).load(R.drawable.store_default).into(holder.iv_main);
            // TODO add default images
        }
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.currentItemClick(dataSet.get(listPosition), v, listPosition);
            }
        });



        setBackGround(dataSet.get(listPosition).afterHowLong(),holder.tv_after, holder.tv_how_long,holder.tv_estimated_time,holder.ll_change_bg);
        holder.tv_total_value.setText(String.valueOf(dataSet.get(listPosition).getServingNumber()));
        holder.tv_current_value.setText(String.valueOf(dataSet.get(listPosition).getToken()));
        holder.tv_how_long.setText(String.valueOf(dataSet.get(listPosition).afterHowLong()));
        holder.tv_how_long.setVisibility(dataSet.get(listPosition).afterHowLong()>0 ?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setBackGround(int pos,TextView tv_after,TextView tv_how_long,TextView tv_estimated_time,LinearLayout ll_change_bg) {
        tv_after.setTextColor(Color.WHITE);
        tv_how_long.setTextColor(Color.WHITE);
        tv_estimated_time.setTextColor(Color.WHITE);
        tv_after.setText("Soon is your turn! You are:");
        //tv_after.setVisibility(View.VISIBLE);
        switch (pos) {
            case 0:
                ll_change_bg.setBackgroundResource(R.drawable.turn_1);
                tv_after.setText("It's your turn!!!");
               // tv_how_long.setText(gotoPerson);
                // tv_after.setVisibility(View.GONE);
                break;
            case 1:
                ll_change_bg.setBackgroundResource(R.drawable.turn_1);
                tv_after.setText("Next is your turn! You are:");
                break;
            case 2:
                ll_change_bg.setBackgroundResource(R.drawable.turn_2);
                break;
            case 3:
                ll_change_bg.setBackgroundResource(R.drawable.turn_3);
                break;
            case 4:
                ll_change_bg.setBackgroundResource(R.drawable.turn_4);
                break;
            case 5:
                ll_change_bg.setBackgroundResource(R.drawable.turn_5);
                break;
            default:
                tv_after.setText("You are:");
                tv_after.setTextColor(ContextCompat.getColor(context, R.color.colorActionbar));
                tv_how_long.setTextColor(ContextCompat.getColor(context, R.color.colorActionbar));
                ll_change_bg.setBackgroundResource(R.drawable.square_bg_drawable);
                tv_estimated_time.setTextColor(ContextCompat.getColor(context, R.color.colorActionbar));
                break;

        }
    }

}
