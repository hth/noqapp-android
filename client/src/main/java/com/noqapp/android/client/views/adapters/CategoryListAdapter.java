package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.views.activities.ManagerProfileActivity;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {
    private final Context context;
    private final OnItemClickListener listener;
    private List<BizStoreElastic> dataSet;


    public CategoryListAdapter(List<BizStoreElastic> jsonQueues, Context context, OnItemClickListener listener) {
        this.dataSet = jsonQueues;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_item_category, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final BizStoreElastic jsonQueue = dataSet.get(listPosition);
        holder.tv_name.setText(jsonQueue.getDisplayName());
        holder.tv_phoneno.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getPhone()));
        holder.tv_store_rating.setText(String.valueOf(AppUtilities.round(jsonQueue.getRating())));
        holder.tv_address.setText(jsonQueue.getAddress());
        holder.tv_store_review.setText(String.valueOf(jsonQueue.getRatingCount() == 0 ? "No" : jsonQueue.getRatingCount()) + " Reviews");
        holder.tv_phoneno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.makeCall((Activity) context, holder.tv_phoneno.getText().toString());
            }
        });
        holder.tv_specialization.setText(jsonQueue.getCompleteEducation());
        StoreHourElastic storeHourElastic = AppUtilities.getStoreHourElastic(jsonQueue.getStoreHourElasticList());
        holder.tv_join.setEnabled(!storeHourElastic.isDayClosed());
        if (storeHourElastic.isDayClosed()) {
            holder.tv_status.setText(context.getString(R.string.store_closed));
            holder.tv_store_timing.setVisibility(View.GONE);
            holder.tv_join.setBackground(ContextCompat.getDrawable(context, R.drawable.grey_background));
            holder.tv_join.setText("Closed");
        } else {
            holder.tv_store_timing.setVisibility(View.VISIBLE);
            String key = Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour())
                    + "-"
                    + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getEndHour());
            if (key.equalsIgnoreCase("12:01 AM-11:59 PM")) {
                key = context.getString(R.string.whole_day);
                holder.tv_store_timing.setText(key);
            } else {
                holder.tv_store_timing.setText(
                        context.getString(R.string.store_hour) + " " + key);
            }
            holder.tv_join.setBackgroundColor(ContextCompat.getColor(context, R.color.colorActionbar));
            holder.tv_join.setText("Walk-in");
        }


        int timeIn24HourFormat = AppUtilities.getTimeIn24HourFormat();
        if (!storeHourElastic.isDayClosed()) {
            // Before Token Available Time
            if (timeIn24HourFormat < storeHourElastic.getTokenAvailableFrom()) {
                if (jsonQueue.getBusinessType() != null) {
                    switch (jsonQueue.getBusinessType()) {
                        case DO:
                            holder.tv_status.setText("Closed Now. Appointment booking starts at " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                            break;
                        default:
                            holder.tv_status.setText("Closed Now. You can join queue at " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                            break;
                    }
                } else {
                    holder.tv_status.setText("Closed Now. You can join queue at " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                }
            }

            // Between Token Available and Start Hour
            if (timeIn24HourFormat >= storeHourElastic.getTokenAvailableFrom() && timeIn24HourFormat < storeHourElastic.getStartHour()) {
                if (jsonQueue.getBusinessType() != null) {
                    switch (jsonQueue.getBusinessType()) {
                        case DO:
                            holder.tv_status.setText("Now accepting appointments for today");
                            break;
                        default:
                            holder.tv_status.setText("Now you can join queue. Queue service will begin at " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour()));
                            break;
                    }
                } else {
                    holder.tv_status.setText("Now you can join queue. Queue service will begin at " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour()));
                }
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.before_opening_queue));
            }

            // After Start Hour and Before Token Not Available From
            if (timeIn24HourFormat >= storeHourElastic.getStartHour() && timeIn24HourFormat < storeHourElastic.getTokenNotAvailableFrom()) {
                if (jsonQueue.getBusinessType() != null) {
                    switch (jsonQueue.getBusinessType()) {
                        case DO:
                            holder.tv_status.setText("Open now. Book your appointment for today.");
                            break;
                        default:
                            holder.tv_status.setText("Open now. Join the queue.");
                            break;
                    }
                } else {
                    holder.tv_status.setText("Open Now. Join the queue.");
                }
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.open_queue));
            }

            // When between Token Not Available From and End Hour
            if (timeIn24HourFormat >= storeHourElastic.getTokenNotAvailableFrom() && timeIn24HourFormat < storeHourElastic.getEndHour()) {
                holder.tv_status.setText("Closing soon");
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }

            // When after End Hour
            if (timeIn24HourFormat >= storeHourElastic.getEndHour()) {
                holder.tv_status.setText("Closed now");
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }


        } else {
            //TODO(hth) Show when will this be open next. For now hide it.
            holder.tv_status.setText("Closed Today");
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        if (!TextUtils.isEmpty(jsonQueue.getDisplayImage())) {

            Picasso.with(context).load(AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, jsonQueue.getDisplayImage())).
                    placeholder(context.getResources().getDrawable(R.drawable.profile_red)).
                    error(context.getResources().getDrawable(R.drawable.profile_red)).into(holder.iv_main);
        } else {
            Picasso.with(context).load(R.drawable.profile_red).into(holder.iv_main);
        }


        holder.tv_store_special.setText(jsonQueue.getFamousFor());
        holder.tv_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCategoryItemClick(jsonQueue, v, listPosition);
            }
        });

        holder.iv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ManagerProfileActivity.class);
                intent.putExtra("webProfileId", jsonQueue.getWebProfileId());
                intent.putExtra("managerName", jsonQueue.getDisplayName());
                intent.putExtra("managerImage", jsonQueue.getDisplayImage());
                context.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onCategoryItemClick(BizStoreElastic item, View view, int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_address;
        private TextView tv_phoneno;
        private TextView tv_store_rating;
        private TextView tv_specialization;
        private TextView tv_store_special;
        private TextView tv_store_review;
        private TextView tv_store_timing;
        private TextView tv_status;
        private TextView tv_join;
        private ImageView iv_main;
        private CardView card_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_phoneno = itemView.findViewById(R.id.tv_phoneno);
            this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
            this.tv_specialization = itemView.findViewById(R.id.tv_specialization);
            this.tv_store_special = itemView.findViewById(R.id.tv_store_special);
            this.tv_store_review = itemView.findViewById(R.id.tv_store_review);
            this.tv_store_timing = itemView.findViewById(R.id.tv_store_timing);
            this.tv_status = itemView.findViewById(R.id.tv_status);
            this.iv_main = itemView.findViewById(R.id.iv_main);
            this.tv_join = itemView.findViewById(R.id.tv_join);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }

}



