package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;
import com.noqapp.android.client.views.activities.ShowAllReviewsActivity;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;


public class SearchAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final OnItemClickListener listener;
    private ArrayList<BizStoreElastic> dataSet;



    public SearchAdapter(ArrayList<BizStoreElastic> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
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
                    .inflate(R.layout.rcv_item_search, parent, false);

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
            holder.tv_address.setText(AppUtilities.getStoreAddress(bizStoreElastic.getTown(),bizStoreElastic.getArea()));
            holder.tv_phoneno.setText(PhoneFormatterUtil.formatNumber(bizStoreElastic.getCountryShortName(), bizStoreElastic.getPhone()));
            holder.tv_store_special.setText(bizStoreElastic.getFamousFor());
            holder.tv_store_rating.setText(String.valueOf(AppUtilities.round(bizStoreElastic.getRating())));
            holder.tv_business_category.setText(bizStoreElastic.getBizCategoryName());
            holder.tv_business_category.setVisibility(TextUtils.isEmpty(bizStoreElastic.getBizCategoryName()) ? View.GONE : View.VISIBLE);
            if(bizStoreElastic.getRatingCount() > 0){
                holder.tv_store_review.setText(String.valueOf(bizStoreElastic.getRatingCount()) + " Reviews");
                holder.tv_store_review.setPaintFlags(holder.tv_store_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }else{
                holder.tv_store_review.setText("No Reviews");
                holder.tv_store_review.setPaintFlags(holder.tv_store_review.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
            }

            holder.tv_store_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (bizStoreElastic.getRatingCount() > 0) {
                        Intent in = new Intent(context, ShowAllReviewsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(NoQueueBaseActivity.KEY_CODE_QR, bizStoreElastic.getCodeQR());
                        bundle.putString("storeName", bizStoreElastic.getDisplayName());
                        bundle.putString("storeAddress", holder.tv_address.getText().toString());
                        in.putExtras(bundle);
                        context.startActivity(in);
                    }
                }
            });
            if (!TextUtils.isEmpty(bizStoreElastic.getDisplayImage()))
                Picasso.with(context)
                        .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
                        .placeholder(ImageUtils.getThumbPlaceholder(context))
                        .error(ImageUtils.getThumbErrorPlaceholder(context))
                        .into(holder.iv_main);
            else {
                Picasso.with(context).load(ImageUtils.getThumbPlaceholder()).into(holder.iv_main);
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
            holder.tv_bussiness_name.setText(bizStoreElastic.getBusinessName());
            switch (bizStoreElastic.getBusinessType()) {
                case DO:
                case BK:
                    holder.tv_store_special.setVisibility(View.GONE);
                    holder.tv_status.setVisibility(View.GONE);
                    holder.tv_category_name.setText("");
                    holder.tv_name.setText(bizStoreElastic.getDisplayName());
                    holder.tv_status.setText("");
                    break;
                default:
                    holder.tv_store_special.setVisibility(View.GONE);
                    holder.tv_status.setVisibility(View.GONE); // hide temporary
                    holder.tv_status.setText(AppUtilities.getStoreOpenStatus(bizStoreElastic));
                    holder.tv_category_name.setText("");
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
        private TextView tv_bussiness_name;
        private TextView tv_business_category;
        private TextView tv_address;
        private TextView tv_phoneno;
        private TextView tv_store_rating;
        private TextView tv_store_review;
        private TextView tv_category_name;
        private TextView tv_store_special;
        private TextView tv_status;
        private ImageView iv_main;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_bussiness_name = itemView.findViewById(R.id.tv_bussiness_name);
            this.tv_business_category = itemView.findViewById(R.id.tv_business_category);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_phoneno = itemView.findViewById(R.id.tv_phoneno);
            this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
            this.tv_store_review = itemView.findViewById(R.id.tv_store_review);
            this.tv_category_name = itemView.findViewById(R.id.tv_category_name);
            this.tv_store_special = itemView.findViewById(R.id.tv_store_special);
            this.tv_status = itemView.findViewById(R.id.tv_status);
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
