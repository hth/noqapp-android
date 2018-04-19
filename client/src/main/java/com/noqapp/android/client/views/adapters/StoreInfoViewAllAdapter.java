package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.views.customviews.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class StoreInfoViewAllAdapter extends RecyclerView.Adapter<StoreInfoViewAllAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<BizStoreElastic> dataSet;

    public interface OnItemClickListener {
        void onStoreItemClick(BizStoreElastic item, View view, int pos);
    }

    private final OnItemClickListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_address;
        private TextView tv_detail;
        private TextView tv_store_rating;
        private TextView tv_category;
        private TextView tv_store_special;
        private ImageView iv_main;
        private CardView card_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_detail = (TextView) itemView.findViewById(R.id.tv_detail);
            this.tv_store_rating = (TextView) itemView.findViewById(R.id.tv_store_rating);
            this.tv_category = (TextView) itemView.findViewById(R.id.tv_category);
            this.tv_store_special = (TextView) itemView.findViewById(R.id.tv_store_special);
            this.iv_main = (ImageView) itemView.findViewById(R.id.iv_main);
            this.card_view = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    public StoreInfoViewAllAdapter(ArrayList<BizStoreElastic> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_view_all, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        BizStoreElastic bizStoreElastic = dataSet.get(listPosition);
        holder.tv_name.setText(bizStoreElastic.getDisplayName());
        holder.tv_address.setText(bizStoreElastic.getAddress());
        if (!TextUtils.isEmpty(bizStoreElastic.getTown()))
            holder.tv_address.setText(bizStoreElastic.getTown());
        holder.tv_detail.setText(bizStoreElastic.getPhone());
        holder.tv_category.setText(bizStoreElastic.getCategory());
        holder.tv_store_rating.setText(String.valueOf(AppUtilities.round(bizStoreElastic.getRating())));
        Picasso.with(context)
                .load(bizStoreElastic.getDisplayImage())
                .transform(new RoundedTransformation(10, 4))
                .into(holder.iv_main);
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStoreItemClick(dataSet.get(listPosition), v, listPosition);
            }
        });

        switch (bizStoreElastic.getBusinessType()) {
            case DO:
            case HO:
                holder.tv_name.setText(bizStoreElastic.getBusinessName());
                holder.tv_store_special.setText("Emergency 24 hours");
                break;
            default:
                holder.tv_store_special.setText("Dal Tadka , Chicken tikka");
                holder.tv_name.setText(bizStoreElastic.getDisplayName());
        }


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
