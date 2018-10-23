package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProductHistorical;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.Feedback;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.views.activities.ContactUsActivity;
import com.noqapp.android.client.views.activities.ReviewActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
            holder.tv_order_date.setText(CommonHelper.SDF_DD_MMM_YY_HH_MM_A.
                    format(new SimpleDateFormat(Constants.ISO8601_FMT, Locale.getDefault()).parse(jsonPurchaseOrderHistorical.getCreated())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tv_order_item.setText(getOrderItems(jsonPurchaseOrderHistorical.getJsonPurchaseOrderProductHistoricalList()));
        try {
            holder.tv_order_amount.setText(context.getString(R.string.rupee) +" "+ String.valueOf(Integer.parseInt(jsonPurchaseOrderHistorical.getOrderPrice()) / 100));
        } catch (Exception e) {
            holder.tv_order_amount.setText("0");
            e.printStackTrace();
        }
        holder.tv_queue_status.setText(jsonPurchaseOrderHistorical.getPresentOrderState().getDescription());
        switch (jsonPurchaseOrderHistorical.getPresentOrderState()) {
            case CO:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.colorMobile));
                break;
            case OD:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            default:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.text_header_color));
        }
        holder.iv_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStoreItemClick(jsonPurchaseOrderHistorical, v, listPosition);
            }
        });
        holder.btn_reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BizStoreElastic bizStoreElastic = new BizStoreElastic();
                bizStoreElastic.setRating(jsonPurchaseOrderHistorical.getRatingCount());
                bizStoreElastic.setDisplayImage("");
                bizStoreElastic.setBusinessName(jsonPurchaseOrderHistorical.getDisplayName());
                bizStoreElastic.setCodeQR(jsonPurchaseOrderHistorical.getCodeQR());
                bizStoreElastic.setBusinessType(jsonPurchaseOrderHistorical.getBusinessType());
                Intent intent = new Intent(context, StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", bizStoreElastic);
                intent.putExtras(bundle);
                context.startActivity(intent);
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

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_address;
        private TextView tv_order_date;
        private TextView tv_order_amount;
        private TextView tv_order_item;
        private TextView tv_queue_status;
        private ImageView iv_details;
        private Button btn_reorder;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_order_date = itemView.findViewById(R.id.tv_order_date);
            this.tv_order_amount = itemView.findViewById(R.id.tv_order_amount);
            this.tv_order_item = itemView.findViewById(R.id.tv_order_item);
            this.tv_queue_status = itemView.findViewById(R.id.tv_queue_status);
            this.btn_reorder = itemView.findViewById(R.id.btn_reorder);
            this.iv_details = itemView.findViewById(R.id.iv_details);
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
