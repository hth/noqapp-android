package com.noqapp.android.merchant.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.OrderDetailActivity;

import java.util.List;

public class ViewAllPeopleInQOrderAdapter extends RecyclerView.Adapter {
    private final Context context;
    private List<JsonPurchaseOrder> dataSet;
    private boolean visibility;

    public ViewAllPeopleInQOrderAdapter(List<JsonPurchaseOrder> data, Context context, boolean visibility) {
        this.dataSet = data;
        this.context = context;
        this.visibility = visibility;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_order_history_q_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        MyViewHolder recordHolder = (MyViewHolder) viewHolder;
        final JsonPurchaseOrder jsonPurchaseOrder = dataSet.get(listPosition);
        final String phoneNo = jsonPurchaseOrder.getCustomerPhone();
        recordHolder.tv_join_timing.setText(Formatter.getTime(jsonPurchaseOrder.getCreated()));
        recordHolder.tv_sequence_number.setText(String.valueOf(jsonPurchaseOrder.getToken()));
        recordHolder.tv_customer_name.setText(TextUtils.isEmpty(jsonPurchaseOrder.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonPurchaseOrder.getCustomerName());
        recordHolder.rl_sequence_new_time.setBackgroundColor(Color.parseColor("#e07e3d"));
        recordHolder.tv_sequence_number.setTextColor(Color.WHITE);
        recordHolder.tv_join_timing.setTextColor(Color.WHITE);
        if (null != LaunchActivity.getLaunchActivity()) {
            recordHolder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                    PhoneFormatterUtil.formatNumber(AppInitialize.getUserProfile().getCountryShortName(), phoneNo));
        }

        if (visibility) {
            if (null != LaunchActivity.getLaunchActivity()) {
                recordHolder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                        PhoneFormatterUtil.formatNumber(AppInitialize.getUserProfile().getCountryShortName(), phoneNo));
            }
            recordHolder.tv_customer_mobile.setOnClickListener(v -> {
                if (!recordHolder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user)))
                    AppUtils.makeCall(LaunchActivity.getLaunchActivity(), PhoneFormatterUtil.formatNumber(AppInitialize.getUserProfile().getCountryShortName(), phoneNo));
            });
        } else {
            recordHolder.tv_customer_mobile.setText(AppUtils.hidePhoneNumberWithX(phoneNo));
        }
        recordHolder.tv_order_data.setOnClickListener(v -> {
            Intent in = new Intent(context, OrderDetailActivity.class);
            in.putExtra("jsonPurchaseOrder", jsonPurchaseOrder);
            in.putExtra(IBConstant.KEY_IS_PAYMENT_NOT_ALLOWED, true);
            in.putExtra(IBConstant.KEY_IS_HISTORY, true);
            ((Activity) context).startActivity(in);
        });
        recordHolder.tv_payment_status.setText(Html.fromHtml("<b>Payment Status: </b>" + jsonPurchaseOrder.getPaymentStatus().getDescription()));
        recordHolder.tv_order_status.setText(Html.fromHtml("<b>Order Status: </b>" + jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription()));
        recordHolder.tv_customer_mobile.setOnClickListener(v -> {
            if (!recordHolder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user)))
                AppUtils.makeCall(LaunchActivity.getLaunchActivity(), phoneNo);
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_customer_name;
        TextView tv_customer_mobile;
        TextView tv_sequence_number;
        TextView tv_order_data;
        TextView tv_payment_status;
        TextView tv_order_status;
        TextView tv_join_timing;
        RelativeLayout rl_sequence_new_time;
        ImageView iv_new;
        CardView cardview;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.tv_sequence_number = itemView.findViewById(R.id.tv_sequence_number);
            this.tv_order_data = itemView.findViewById(R.id.tv_order_data);
            this.tv_order_status = itemView.findViewById(R.id.tv_order_status);
            this.tv_payment_status = itemView.findViewById(R.id.tv_payment_status);
            this.rl_sequence_new_time = itemView.findViewById(R.id.rl_sequence_new_time);
            this.tv_join_timing = itemView.findViewById(R.id.tv_join_timing);
            this.iv_new = itemView.findViewById(R.id.iv_new);
            this.cardview = itemView.findViewById(R.id.cardview);
        }
    }
}
