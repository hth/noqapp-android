package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.common.beans.JsonReview;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ShowAllReviewsAdapter extends RecyclerView.Adapter<ShowAllReviewsAdapter.MyViewHolder> {
    private final Context context;

    private List<JsonReview> dataSet;

    public ShowAllReviewsAdapter(List<JsonReview> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_current_activity, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final JsonReview jsonReview = dataSet.get(listPosition);
        holder.tv_name.setText(jsonReview.getName());
        holder.tv_detail.setText(jsonReview.getReview());
        holder.tv_rating.setText(String.valueOf(jsonReview.getRatingCount()));
        Picasso.with(context).load(ImageUtils.getProfilePlaceholder()).into(holder.iv_main);
        try {
            if (!TextUtils.isEmpty(jsonReview.getProfileImage())) {
                Picasso.with(context)
                        .load(AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, jsonReview.getProfileImage()))
                        .placeholder(ImageUtils.getProfilePlaceholder(context))
                        .error(ImageUtils.getProfileErrorPlaceholder(context))
                        .into(holder.iv_main);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_detail;
        private TextView tv_rating;
        private ImageView iv_main;
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_detail = itemView.findViewById(R.id.tv_detail);
            this.tv_rating = itemView.findViewById(R.id.tv_rating);
            this.iv_main = itemView.findViewById(R.id.iv_main);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }

}
