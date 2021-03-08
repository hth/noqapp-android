package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.views.activities.AllReviewsActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoreInfoViewAllAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final OnItemClickListener listener;
    private List<BizStoreElastic> dataSet;
    private double lat, log;
    private boolean isFavourite = false;

    public StoreInfoViewAllAdapter(List<BizStoreElastic> data, Context context, OnItemClickListener listener, double lat, double log) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        this.lat = lat;
        this.log = log;
    }

    public StoreInfoViewAllAdapter(List<BizStoreElastic> data, Context context, OnItemClickListener listener, double lat, double log, boolean isFavourite) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        this.lat = lat;
        this.log = log;
        this.isFavourite = isFavourite;
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_view_all, parent, false);
            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        if (viewHolder instanceof MyViewHolder) {
            final MyViewHolder holder = (MyViewHolder) viewHolder;
            final BizStoreElastic bizStoreElastic = dataSet.get(listPosition);
            holder.tv_address.setText(AppUtils.getStoreAddress(bizStoreElastic.getTown(), bizStoreElastic.getArea()));
            holder.tv_phoneno.setText(PhoneFormatterUtil.formatNumber(bizStoreElastic.getCountryShortName(), bizStoreElastic.getPhone()));
            holder.tv_store_special.setText(bizStoreElastic.getFamousFor());
            holder.tv_store_rating.setText(String.valueOf(AppUtils.round(bizStoreElastic.getRating())));
            switch (bizStoreElastic.getBusinessType()) {
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
                        (float) GeoHashUtils.decodeLatitude(bizStoreElastic.getGeoHash()),
                        (float) GeoHashUtils.decodeLongitude(bizStoreElastic.getGeoHash()))));
                    holder.tv_distance_unit.setText(LaunchActivity.DISTANCE_UNIT);
            }
            AppUtils.setReviewCountText(bizStoreElastic.getReviewCount(), holder.tv_store_review);

            holder.tv_store_review.setOnClickListener((View v) -> {
                if (bizStoreElastic.getReviewCount() > 0) {
                    Intent in = new Intent(context, AllReviewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(IBConstant.KEY_CODE_QR, bizStoreElastic.getCodeQR());
                    bundle.putString(IBConstant.KEY_STORE_NAME, bizStoreElastic.getDisplayName());
                    bundle.putString(IBConstant.KEY_STORE_ADDRESS, holder.tv_address.getText().toString());
                    in.putExtras(bundle);
                    context.startActivity(in);
                }
            });
            if (!TextUtils.isEmpty(bizStoreElastic.getDisplayImage()))
                Picasso.get()
                    .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
                    .placeholder(ImageUtils.getThumbPlaceholder(context))
                    .error(ImageUtils.getThumbErrorPlaceholder(context))
                    .into(holder.iv_main);
            else {
                Picasso.get().load(ImageUtils.getThumbPlaceholder()).into(holder.iv_main);
            }
            holder.card_view.setOnClickListener((View v) -> listener.onStoreItemClick(dataSet.get(listPosition)));
            if (holder.tv_store_rating.getText().toString().equals("0.0")) {
                holder.tv_store_rating.setVisibility(View.INVISIBLE);
            } else {
                holder.tv_store_rating.setVisibility(View.VISIBLE);
            }
            // holder.tv_store_special.setText();
            StoreHourElastic storeHourElastic = AppUtils.getStoreHourElastic(bizStoreElastic.getStoreHourElasticList());
            switch (bizStoreElastic.getBusinessType()) {
                case DO:
                case CD:
                case CDQ:
                case BK:
                case HS:
                case PW:
                    holder.tv_store_special.setVisibility(View.GONE);
                    holder.tv_status.setVisibility(View.GONE);
                    holder.tv_category_name.setText("");
                    holder.tv_name.setText(isFavourite? bizStoreElastic.getDisplayName(): bizStoreElastic.getBusinessName());
                    holder.tv_status.setText("");
                    break;
                default:
                    holder.tv_store_special.setVisibility(View.GONE);
                    holder.tv_status.setVisibility(View.VISIBLE);
                    holder.tv_status.setText(AppUtils.getStoreOpenStatus(bizStoreElastic));
                    holder.tv_category_name.setText(new AppUtils().formatTodayStoreTiming(context, storeHourElastic.isDayClosed(),
                            storeHourElastic.getStartHour(), storeHourElastic.getEndHour()));
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
        void onStoreItemClick(BizStoreElastic item);
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
            this.tv_phoneno = itemView.findViewById(R.id.tv_phoneno);
            this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
            this.tv_store_review = itemView.findViewById(R.id.tv_store_review);
            this.tv_category_name = itemView.findViewById(R.id.tv_category_name);
            this.tv_store_special = itemView.findViewById(R.id.tv_store_special);
            this.tv_status = itemView.findViewById(R.id.tv_status);
            this.tv_business_category = itemView.findViewById(R.id.tv_business_category);
            this.rl_distance = itemView.findViewById(R.id.rl_distance);
            this.tv_distance = itemView.findViewById(R.id.tv_distance);
            this.tv_distance_unit = itemView.findViewById(R.id.tv_distance_unit);
            this.tv_distance_away = itemView.findViewById(R.id.tv_distance_away);
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
