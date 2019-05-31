package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.GeoHashUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.views.activities.AllReviewsActivity;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class StoreInfoViewAllAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final OnItemClickListener listener;
    private ArrayList<BizStoreElastic> dataSet;
    private double lat, log;
    public StoreInfoViewAllAdapter(ArrayList<BizStoreElastic> data, Context context, OnItemClickListener listener,double lat, double log) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        this.lat = lat;
        this.log = log;
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_view_all, parent, false);

            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        if (viewHolder instanceof MyViewHolder) {
            final MyViewHolder holder = (MyViewHolder) viewHolder;
            final BizStoreElastic bizStoreElastic = dataSet.get(listPosition);
            holder.tv_address.setText(AppUtilities.getStoreAddress(bizStoreElastic.getTown(), bizStoreElastic.getArea()));
            holder.tv_phoneno.setText(PhoneFormatterUtil.formatNumber(bizStoreElastic.getCountryShortName(), bizStoreElastic.getPhone()));
            holder.tv_store_special.setText(bizStoreElastic.getFamousFor());
            holder.tv_store_rating.setText(String.valueOf(AppUtilities.round(bizStoreElastic.getRating())));
            holder.tv_distance.setText(String.valueOf(AppUtilities.calculateDistance(
                    (float) lat,
                    (float) log,
                    (float) GeoHashUtils.decodeLatitude(bizStoreElastic.getGeoHash()),
                    (float) GeoHashUtils.decodeLongitude(bizStoreElastic.getGeoHash()))));
            if(bizStoreElastic.getReviewCount() > 0){
                holder.tv_store_review.setText(String.valueOf(bizStoreElastic.getReviewCount()) + " Reviews");
                holder.tv_store_review.setPaintFlags(holder.tv_store_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }else{
                holder.tv_store_review.setText("No Reviews");
                holder.tv_store_review.setPaintFlags(holder.tv_store_review.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
            }

            holder.tv_store_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (bizStoreElastic.getReviewCount() > 0) {
                        Intent in = new Intent(context, AllReviewsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(IBConstant.KEY_CODE_QR, bizStoreElastic.getCodeQR());
                        bundle.putString(IBConstant.KEY_STORE_NAME, bizStoreElastic.getDisplayName());
                        bundle.putString(IBConstant.KEY_STORE_ADDRESS, holder.tv_address.getText().toString());
                        in.putExtras(bundle);
                        context.startActivity(in);
                    }
                }
            });
            if (!TextUtils.isEmpty(bizStoreElastic.getDisplayImage()))
                Picasso.get()
                        .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
                        .placeholder(ImageUtils.getThumbPlaceholder(context))
                        .error(ImageUtils.getThumbErrorPlaceholder(context))
                        .into(holder.iv_main);
            else {
                Picasso.get().load(ImageUtils.getThumbPlaceholder()).into(holder.iv_main);
            }
            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onStoreItemClick(dataSet.get(listPosition), v, listPosition);
                }
            });
            if (holder.tv_store_rating.getText().toString().equals("0.0")) {
                holder.tv_store_rating.setVisibility(View.INVISIBLE);
            } else {
                holder.tv_store_rating.setVisibility(View.VISIBLE);
            }
            // holder.tv_store_special.setText();
            StoreHourElastic storeHourElastic = AppUtilities.getStoreHourElastic(bizStoreElastic.getStoreHourElasticList());
            switch (bizStoreElastic.getBusinessType()) {
                case DO:
                case BK:
                case HS:
                    holder.tv_store_special.setVisibility(View.GONE);
                    holder.tv_status.setVisibility(View.GONE);
                    holder.tv_category_name.setText("");
                    holder.tv_name.setText(bizStoreElastic.getBusinessName());
                    holder.tv_status.setText("");
                    break;
                default:
                    holder.tv_store_special.setVisibility(View.GONE);
                    holder.tv_status.setVisibility(View.VISIBLE);
                    holder.tv_status.setText(AppUtilities.getStoreOpenStatus(bizStoreElastic));
                    holder.tv_category_name.setText(new AppUtilities().formatTodayStoreTiming(context, storeHourElastic));
                    holder.tv_name.setText(bizStoreElastic.getDisplayName());
                    break;
            }
        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
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
        private TextView tv_address;
        private TextView tv_phoneno;
        private TextView tv_store_rating;
        private TextView tv_store_review;
        private TextView tv_category_name;
        private TextView tv_store_special;
        private TextView tv_status;
        private TextView tv_business_category;
        private TextView tv_distance;
        private ImageView iv_main;
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_phoneno = itemView.findViewById(R.id.tv_phoneno);
            this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
            this.tv_store_review = itemView.findViewById(R.id.tv_store_review);
            this.tv_category_name = itemView.findViewById(R.id.tv_category_name);
            this.tv_store_special = itemView.findViewById(R.id.tv_store_special);
            this.tv_status = itemView.findViewById(R.id.tv_status);
            this.tv_business_category = itemView.findViewById(R.id.tv_business_category);
            this.tv_distance = itemView.findViewById(R.id.tv_distance);
            this.iv_main = itemView.findViewById(R.id.iv_main);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }
}
