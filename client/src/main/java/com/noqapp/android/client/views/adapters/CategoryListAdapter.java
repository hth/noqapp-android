package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.activities.ManagerProfileActivity;
import com.noqapp.android.client.views.activities.ShowAllReviewsActivity;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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
                .inflate(R.layout.rcv_item_category1, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final BizStoreElastic bizStoreElastic = dataSet.get(listPosition);
        holder.tv_name.setText(bizStoreElastic.getDisplayName());
        holder.tv_store_rating.setText(String.valueOf(AppUtilities.round(bizStoreElastic.getRating())));
        holder.tv_address.setText(AppUtilities.getStoreAddress(bizStoreElastic.getTown(),bizStoreElastic.getArea()));
        holder.tv_store_review.setPaintFlags(holder.tv_store_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (bizStoreElastic.getReviewCount() == 0) {
            holder.tv_store_review.setText("No Review");
            holder.tv_store_review.setPaintFlags(holder.tv_store_review.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        } else if (bizStoreElastic.getReviewCount() == 1) {
            holder.tv_store_review.setText("1 Review");
        } else {
            holder.tv_store_review.setText(String.valueOf(bizStoreElastic.getReviewCount()) + " Reviews");
        }

        holder.tv_store_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bizStoreElastic.getReviewCount() > 0) {
                    Intent in = new Intent(context, ShowAllReviewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(IBConstant.KEY_CODE_QR, bizStoreElastic.getCodeQR());
                    bundle.putString(IBConstant.KEY_STORE_NAME, bizStoreElastic.getDisplayName());
                    bundle.putString(IBConstant.KEY_STORE_ADDRESS, AppUtilities.getStoreAddress(bizStoreElastic.getTown(), bizStoreElastic.getArea()));
                    in.putExtras(bundle);
                    context.startActivity(in);
                }
            }
        });
        holder.tv_specialization.setText(bizStoreElastic.getCompleteEducation());
        StoreHourElastic storeHourElastic = AppUtilities.getStoreHourElastic(bizStoreElastic.getStoreHourElasticList());
        holder.tv_join.setEnabled(!storeHourElastic.isDayClosed());
        if (storeHourElastic.isDayClosed()) {
            holder.tv_status.setText(context.getString(R.string.store_closed));
            holder.tv_store_timing.setVisibility(View.VISIBLE);
            holder.tv_store_timing.setText("");
            holder.tv_time_label.setText("");
            holder.tv_join.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_bg_inactive));
            holder.tv_join.setTextColor(context.getResources().getColor(R.color.button_color));
            holder.tv_join.setText("Closed");
        } else {
            holder.tv_store_timing.setVisibility(View.VISIBLE);
            holder.tv_time_label.setText("Store timing");
            holder.tv_store_timing.setText(new AppUtilities().formatTodayStoreTiming(context, storeHourElastic));
            holder.tv_join.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_bg_enable));
            holder.tv_join.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_join.setText("Walk-in");
            if (bizStoreElastic.getBusinessType() == BusinessTypeEnum.HS) {
                holder.tv_join.setText("Visit Store");
            }
        }


        int timeIn24HourFormat = AppUtilities.getTimeIn24HourFormat();
        if (!storeHourElastic.isDayClosed()) {
            // Before Token Available Time
            if (timeIn24HourFormat < storeHourElastic.getTokenAvailableFrom()) {
                if (bizStoreElastic.getBusinessType() != null) {
                    switch (bizStoreElastic.getBusinessType()) {
                        case DO:
                            holder.tv_status.setText("Closed Now. Appointment booking starts at "
                                    + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                            break;
                        default:
                            holder.tv_status.setText("Closed Now. You can join queue at "
                                    + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                            break;
                    }
                } else {
                    holder.tv_status.setText("Closed Now. You can join queue at "
                            + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                }
            }

            // Between Token Available and Start Hour
            if (timeIn24HourFormat >= storeHourElastic.getTokenAvailableFrom() && timeIn24HourFormat < storeHourElastic.getStartHour()) {
                if (bizStoreElastic.getBusinessType() != null) {
                    switch (bizStoreElastic.getBusinessType()) {
                        case DO:
                            holder.tv_status.setText("Now accepting appointments for today");
                            break;
                        default:
                            holder.tv_status.setText("Now you can join queue. Queue service will begin at "
                                    + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour()));
                            break;
                    }
                } else {
                    holder.tv_status.setText("Now you can join queue. Queue service will begin at "
                            + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour()));
                }
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.before_opening_queue));
            }

            // After Start Hour and Before Token Not Available From
            if (timeIn24HourFormat >= storeHourElastic.getStartHour() && timeIn24HourFormat < storeHourElastic.getTokenNotAvailableFrom()) {
                if (bizStoreElastic.getBusinessType() != null) {
                    switch (bizStoreElastic.getBusinessType()) {
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
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.button_color));
            }

            // When after End Hour
            if (timeIn24HourFormat >= storeHourElastic.getEndHour()) {
                holder.tv_status.setText("Closed now");
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.button_color));
            }
        } else {
            //TODO(hth) Show when will this be open next. For now hide it.
            holder.tv_status.setText("Closed Today");
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.button_color));
        }
        if (!TextUtils.isEmpty(bizStoreElastic.getDisplayImage())) {
            Picasso.get().load(
                    AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, bizStoreElastic.getDisplayImage()))
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_theme))
                    .error(context.getResources().getDrawable(R.drawable.profile_theme))
                    .into(holder.iv_main);
        } else {
            Picasso.get().load(R.drawable.profile_theme).into(holder.iv_main);
        }

        holder.tv_consult_fees.setVisibility(bizStoreElastic.getProductPrice() == 0 ? View.GONE:View.VISIBLE);
       // String feeString = "<font color=#000000><b>"+ AppUtilities.getCurrencySymbol(bizStoreElastic.getCountryShortName()) + String.valueOf(bizStoreElastic.getProductPrice() / 100) + "</b></font>  Consultation fee";
        String feeString = "<b>"+ AppUtilities.getCurrencySymbol(bizStoreElastic.getCountryShortName()) + String.valueOf(bizStoreElastic.getProductPrice() / 100) + "</b>";
        holder.tv_consult_fees.setText(Html.fromHtml(feeString));
        holder.tv_store_special.setText(bizStoreElastic.getFamousFor());
        holder.tv_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bizStoreElastic.getBusinessType() != BusinessTypeEnum.HS) {
                    listener.onCategoryItemClick(bizStoreElastic, v, listPosition);
                } else {
                    //Do nothing
                    Toast.makeText(context, "Please visit store to avail the service.", Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.iv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bizStoreElastic.getBusinessType() == BusinessTypeEnum.DO) {
                    Intent intent = new Intent(context, ManagerProfileActivity.class);
                    intent.putExtra("webProfileId", bizStoreElastic.getWebProfileId());
                    intent.putExtra("managerName", bizStoreElastic.getDisplayName());
                    intent.putExtra("managerImage", bizStoreElastic.getDisplayImage());
                    intent.putExtra("bizCategoryId", bizStoreElastic.getBizCategoryId());
                    context.startActivity(intent);
                }
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
        private TextView tv_store_rating;
        private TextView tv_specialization;
        private TextView tv_store_special;
        private TextView tv_store_review;
        private TextView tv_store_timing;
        private TextView tv_time_label;
        private TextView tv_status;
        private TextView tv_join;
        private TextView tv_consult_fees;
        private ImageView iv_main;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
            this.tv_specialization = itemView.findViewById(R.id.tv_specialization);
            this.tv_store_special = itemView.findViewById(R.id.tv_store_special);
            this.tv_store_review = itemView.findViewById(R.id.tv_store_review);
            this.tv_store_timing = itemView.findViewById(R.id.tv_store_timing);
            this.tv_time_label = itemView.findViewById(R.id.tv_time_label);
            this.tv_status = itemView.findViewById(R.id.tv_status);
            this.iv_main = itemView.findViewById(R.id.iv_main);
            this.tv_join = itemView.findViewById(R.id.tv_join);
            this.tv_consult_fees = itemView.findViewById(R.id.tv_consult_fees);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}