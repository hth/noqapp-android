package com.noqapp.android.client.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.noqapp.android.client.utils.PhoneFormatterUtil;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {
    private final Context context;
    private List<BizStoreElastic> dataSet;

    public interface OnItemClickListener {
        void onCategoryItemClick(BizStoreElastic item, View view, int pos);
    }

    private final OnItemClickListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_address;
        private TextView tv_phoneno;
        private TextView tv_store_rating;
        private TextView tv_specialization;
        private TextView tv_store_special;
        private TextView tv_store_review;
        private TextView tv_status;
        private ImageView iv_main;
        private CardView card_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_phoneno = (TextView) itemView.findViewById(R.id.tv_phoneno);
            this.tv_store_rating = (TextView) itemView.findViewById(R.id.tv_store_rating);
            this.tv_specialization = (TextView) itemView.findViewById(R.id.tv_specialization);
            this.tv_store_special = (TextView) itemView.findViewById(R.id.tv_store_special);
            this.tv_store_review = (TextView) itemView.findViewById(R.id.tv_store_review);
            this.tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            this.iv_main = (ImageView) itemView.findViewById(R.id.iv_main);
            this.card_view = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

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
        holder.tv_name.setText(dataSet.get(listPosition).getDisplayName());
        holder.tv_phoneno.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getPhone()));
        holder.tv_store_rating.setText(String.valueOf(AppUtilities.round(dataSet.get(listPosition).getRating())));
        holder.tv_address.setText(jsonQueue.getAddress());
        holder.tv_store_review.setText(String.valueOf(jsonQueue.getRatingCount() == 0 ? "No" : jsonQueue.getRatingCount()) + " Reviews");
        holder.tv_phoneno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.makeCall((Activity) context, holder.tv_phoneno.getText().toString());
            }
        });
        // holder.tv_store_special.setText();
        StoreHourElastic storeHourElastic = jsonQueue.getStoreHourElasticList().get(AppUtilities.getTodayDay());
        if (storeHourElastic.isDayClosed()) {
            holder.tv_status.setText(context.getString(R.string.store_closed));
        } else {
            holder.tv_status.setText(
                    context.getString(R.string.store_hour)
                            + " "
                            + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour())
                            + " - "
                            + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getEndHour()));
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
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.before_opening_queue));
            }

            // When after End Hour
            if (timeIn24HourFormat >= storeHourElastic.getEndHour()) {
                holder.tv_status.setText("Closed now");
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.color_btn_select));
            }

            holder.tv_status.setTypeface(null, Typeface.BOLD);
        } else {
            //TODO(hth) Show when will this be open next. For now hide it.
            //holder.tv_category.setText("Show some smart message");
            //holder.tv_category.setTextColor(Color.DKGRAY);
            //holder.tv_category.setTypeface(null, Typeface.BOLD);
            holder.tv_status.setVisibility(View.GONE);
        }
       // commented temporary
       /* Picasso.with(context)
                .load(dataSet.get(listPosition).getDisplayImage())
                .transform(new RoundedTransformation(10, 4))
                .into(holder.iv_main);*/
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCategoryItemClick(dataSet.get(listPosition), v, listPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}



