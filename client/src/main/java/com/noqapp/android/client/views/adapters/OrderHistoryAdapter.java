package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProductHistorical;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.Feedback;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.views.activities.ContactUsActivity;
import com.noqapp.android.client.views.activities.ReviewActivity;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
        holder.tv_address.setText(AppUtilities.getStoreAddress(jsonPurchaseOrderHistorical.getTown(), jsonPurchaseOrderHistorical.getArea()));
        try {
            holder.tv_order_date.setText(CommonHelper.SDF_YYYY_MM_DD_HH_MM_A.
                    format(new SimpleDateFormat(Constants.ISO8601_FMT, Locale.getDefault()).parse(jsonPurchaseOrderHistorical.getCreated())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tv_order_item.setText(getOrderItems(jsonPurchaseOrderHistorical.getJsonPurchaseOrderProductHistoricalList()));
        holder.tv_order_amount.setText(String.valueOf(Integer.parseInt(jsonPurchaseOrderHistorical.getOrderPrice()) / 100));
        holder.tv_store_rating.setText("Rating: " + String.valueOf(jsonPurchaseOrderHistorical.getRatingCount()));
        holder.tv_queue_status.setText(jsonPurchaseOrderHistorical.getPresentOrderState().getDescription());
        if (0 == jsonPurchaseOrderHistorical.getRatingCount()) {
            holder.tv_add_review.setVisibility(View.VISIBLE);
            if (jsonPurchaseOrderHistorical.getPresentOrderState() != PurchaseOrderStateEnum.OD) {
                holder.tv_add_review.setVisibility(View.GONE);
            }
            holder.tv_store_rating.setVisibility(View.GONE);
        } else {
            holder.tv_add_review.setVisibility(View.GONE);
            holder.tv_store_rating.setVisibility(View.VISIBLE);
        }
        holder.tv_add_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue();
                jsonTokenAndQueue.setQueueUserId(jsonPurchaseOrderHistorical.getQueueUserId());
                jsonTokenAndQueue.setDisplayName(jsonPurchaseOrderHistorical.getDisplayName());
                jsonTokenAndQueue.setStoreAddress(jsonPurchaseOrderHistorical.getStoreAddress());
                jsonTokenAndQueue.setBusinessType(jsonPurchaseOrderHistorical.getBusinessType());
                jsonTokenAndQueue.setCodeQR(jsonPurchaseOrderHistorical.getCodeQR());
                jsonTokenAndQueue.setToken(jsonPurchaseOrderHistorical.getTokenNumber());
                Intent in = new Intent(context, ReviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("object", jsonTokenAndQueue);
                in.putExtras(bundle);
                context.startActivity(in);
            }
        });
        holder.tv_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feedback feedback = new Feedback();
                feedback.setMessageOrigin(MessageOriginEnum.O);
                feedback.setCodeQR(jsonPurchaseOrderHistorical.getCodeQR());
                feedback.setToken(jsonPurchaseOrderHistorical.getTokenNumber());
                Intent in = new Intent(context, ContactUsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("object", feedback);
                in.putExtras(bundle);
                context.startActivity(in);
            }
        });
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
        private TextView tv_support;
        private TextView tv_address;
        private TextView tv_order_date;
        private TextView tv_store_rating;
        private TextView tv_add_review;
        private TextView tv_order_amount;
        private TextView tv_order_item;
        private TextView tv_queue_status;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_support = itemView.findViewById(R.id.tv_support);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_order_date = itemView.findViewById(R.id.tv_order_date);
            this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
            this.tv_add_review = itemView.findViewById(R.id.tv_add_review);
            this.tv_order_amount = itemView.findViewById(R.id.tv_order_amount);
            this.tv_order_item = itemView.findViewById(R.id.tv_order_item);
            this.tv_queue_status = itemView.findViewById(R.id.tv_queue_status);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }

    private String getOrderItems(List<JsonPurchaseOrderProductHistorical> data) {
        String result = "";
        for (int i = 0; i < data.size(); i++) {
            result += data.get(i).getProductName() + " x " + String.valueOf(data.get(i).getProductQuantity()) + ", ";
        }
        return result.endsWith(", ") ? result.substring(0, result.length() - 2) : result;
    }
}
