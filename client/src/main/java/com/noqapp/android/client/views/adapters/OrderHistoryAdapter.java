package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.common.beans.order.JsonPurchaseOrder;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

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

import java.util.ArrayList;

public class OrderHistoryAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OrderHistoryAdapter.OnItemClickListener listener;
    private ArrayList<JsonPurchaseOrder> dataSet;

    public OrderHistoryAdapter(ArrayList<JsonPurchaseOrder> data, Context context, OrderHistoryAdapter.OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_order_history, parent, false);
        RecyclerView.ViewHolder vh = new OrderHistoryAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        OrderHistoryAdapter.MyViewHolder holder = (OrderHistoryAdapter.MyViewHolder) viewHolder;
        JsonPurchaseOrder bizStoreElastic = dataSet.get(listPosition);
        if (!TextUtils.isEmpty(""))
            Picasso.with(context)
                    .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, ""))
                    .placeholder(ImageUtils.getThumbPlaceholder(context))
                    .error(ImageUtils.getThumbErrorPlaceholder(context))
                    .into(holder.iv_main);
        else {
            Picasso.with(context).load(ImageUtils.getThumbPlaceholder()).into(holder.iv_main);
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
        private ImageView iv_main;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
//            this.tv_name = itemView.findViewById(R.id.tv_name);
//            this.tv_address = itemView.findViewById(R.id.tv_address);
//            this.tv_phoneno = itemView.findViewById(R.id.tv_phoneno);
//            this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
//            this.tv_store_review = itemView.findViewById(R.id.tv_store_review);
//            this.tv_category_name = itemView.findViewById(R.id.tv_category_name);
//            this.tv_store_special = itemView.findViewById(R.id.tv_store_special);
//            this.tv_status = itemView.findViewById(R.id.tv_status);
            this.iv_main = itemView.findViewById(R.id.iv_main);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
