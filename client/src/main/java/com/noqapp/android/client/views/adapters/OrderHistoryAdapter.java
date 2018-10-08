package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.common.beans.order.JsonPurchaseOrder;

import com.noqapp.android.common.utils.CommonHelper;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OnItemClickListener listener;
    private ArrayList<JsonPurchaseOrderHistorical> dataSet;

    public OrderHistoryAdapter(ArrayList<JsonPurchaseOrderHistorical> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_order_history, parent, false);
        RecyclerView.ViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        final JsonPurchaseOrderHistorical jsonPurchaseOrderHistorical = dataSet.get(listPosition);
        holder.tv_name.setText(jsonPurchaseOrderHistorical.getDisplayName());
        holder.tv_address.setText(jsonPurchaseOrderHistorical.getStoreAddress());
        try {
            holder.tv_order_date.setText(Html.fromHtml("<b>Order date- </b>" + CommonHelper.SDF_YYYY_MM_DD.
                    format(new SimpleDateFormat(Constants.ISO8601_FMT, Locale.getDefault()).parse(jsonPurchaseOrderHistorical.getCreated()))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tv_order_number.setText(Html.fromHtml("<b>Order number- </b>" +String.valueOf(jsonPurchaseOrderHistorical.getTokenNumber())));
        holder.tv_order_amount.setText(Html.fromHtml("<b>Order amount- </b>" +Integer.parseInt(jsonPurchaseOrderHistorical.getOrderPrice())/100));
        holder.tv_store_rating.setText(Html.fromHtml("<b>Rating- </b>" +String.valueOf(jsonPurchaseOrderHistorical.getRatingCount())));
        holder.tv_queue_status.setText(Html.fromHtml("<b>Status- </b>"  + jsonPurchaseOrderHistorical.getPresentOrderState().getDescription()));
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStoreItemClick(jsonPurchaseOrderHistorical, v, listPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onStoreItemClick(JsonPurchaseOrderHistorical item, View view, int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_address;
        private TextView tv_order_date;
        private TextView tv_store_rating;
        private TextView tv_order_number;
        private TextView tv_order_amount;
        private TextView tv_store_special;
        private TextView tv_queue_status;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_order_date = itemView.findViewById(R.id.tv_order_date);
            this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
            this.tv_order_number = itemView.findViewById(R.id.tv_order_number);
            this.tv_order_amount = itemView.findViewById(R.id.tv_order_amount);
//            this.tv_store_special = itemView.findViewById(R.id.tv_store_special);
            this.tv_queue_status = itemView.findViewById(R.id.tv_queue_status);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
