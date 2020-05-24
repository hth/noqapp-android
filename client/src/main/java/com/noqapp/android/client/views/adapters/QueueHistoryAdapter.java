package com.noqapp.android.client.views.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonQueueHistorical;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.activities.BeforeJoinActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.activities.StoreWithMenuActivity;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.utils.CommonHelper;

import java.util.ArrayList;

public class QueueHistoryAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OnItemClickListener listener;
    private ArrayList<JsonQueueHistorical> dataSet;

    public QueueHistoryAdapter(ArrayList<JsonQueueHistorical> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_queue_history, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        final JsonQueueHistorical jsonQueueHistorical = dataSet.get(listPosition);
        holder.tv_name.setText(jsonQueueHistorical.getDisplayName());
        holder.tv_business_name_address.setText(jsonQueueHistorical.getBusinessName() + " " + AppUtils.getStoreAddress(jsonQueueHistorical.getTown(), jsonQueueHistorical.getArea()));
        holder.tv_queue_join_date.setText(CommonHelper.formatStringDate(CommonHelper.SDF_DD_MMM_YY_HH_MM_A, jsonQueueHistorical.getCreated()));
        holder.tv_queue_status.setText(jsonQueueHistorical.getQueueUserState().getDescription());
        holder.tv_business_category.setText(jsonQueueHistorical.getBizCategoryName());
        holder.btn_rejoin.setOnClickListener((View v) -> {
            Intent in;
            Bundle b = new Bundle();
            switch (jsonQueueHistorical.getBusinessType()) {
                case DO:
                case BK:
                    // open hospital/Bank profile
                    in = new Intent(context, BeforeJoinActivity.class);
                    in.putExtra(IBConstant.KEY_CODE_QR, jsonQueueHistorical.getCodeQR());
                    in.putExtra(IBConstant.KEY_FROM_LIST, true);
                    in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                    in.putExtra(IBConstant.KEY_IS_DO,jsonQueueHistorical.getBusinessType()== BusinessTypeEnum.DO);
                    context.startActivity(in);
                    break;
                case HS:
                case PH: {
                    // open order screen
                    in = new Intent(context, StoreDetailActivity.class);
                    b.putSerializable("BizStoreElastic", AppUtils.getStoreElastic(jsonQueueHistorical));
                    in.putExtras(b);
                    context.startActivity(in);
                }
                break;
                default: {
                    // open order screen
                    in = new Intent(context, StoreWithMenuActivity.class);
                    b.putSerializable("BizStoreElastic", AppUtils.getStoreElastic(jsonQueueHistorical));
                    in.putExtras(b);
                    context.startActivity(in);
                }
            }
        });
        holder.iv_details.setOnClickListener((View v) -> {
            //navigate to detail screen
            listener.onStoreItemClick(jsonQueueHistorical);
        });

        switch (jsonQueueHistorical.getQueueUserState()) {
            case A:
            case N:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.colorMobile));
                break;
            case Q:
                String business;
                switch (jsonQueueHistorical.getBusinessType()) {
                    case DO:
                        business = "Doctor";
                        break;
                    case BK:
                        business = "Bank";
                        break;
                    default:
                        business = "Business";
                }
                holder.tv_queue_status.setText(business + " has not confirmed");
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.text_header_color));
                break;
            case S:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            default:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.text_header_color));
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onStoreItemClick(JsonQueueHistorical item);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_support;
        private TextView tv_address;
        private TextView tv_queue_join_date;
        private TextView tv_queue_status;
        private TextView tv_business_name_address;
        private TextView tv_business_category;
        private Button btn_rejoin;
        private ImageView iv_details;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_queue_join_date = itemView.findViewById(R.id.tv_queue_join_date);
            this.tv_queue_status = itemView.findViewById(R.id.tv_queue_status);
            this.tv_business_name_address = itemView.findViewById(R.id.tv_business_name_address);
            this.tv_business_category = itemView.findViewById(R.id.tv_business_category);
            this.btn_rejoin = itemView.findViewById(R.id.btn_rejoin);
            this.iv_details = itemView.findViewById(R.id.iv_details);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
