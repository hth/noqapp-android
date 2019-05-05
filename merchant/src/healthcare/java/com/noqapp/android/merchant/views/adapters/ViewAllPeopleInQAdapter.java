package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.OrderDetailActivity;

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

import java.util.List;

public class ViewAllPeopleInQAdapter extends RecyclerView.Adapter<ViewAllPeopleInQAdapter.MyViewHolder> {
    private final Context context;
    private final OnItemClickListener listener;
    private List<JsonQueuedPerson> dataSet;
    private boolean visibility;

    public ViewAllPeopleInQAdapter(List<JsonQueuedPerson> data, Context context, OnItemClickListener listener, boolean visibility) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        this.visibility = visibility;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final JsonQueuedPerson jsonQueuedPerson = dataSet.get(listPosition);
        holder.tv_sequence_number.setText(String.valueOf(jsonQueuedPerson.getToken()));
        holder.tv_join_timing.setText(Formatter.getTime(jsonQueuedPerson.getCreated()));
        switch (jsonQueuedPerson.getQueueUserState()) {
            case A:
                //TODO(show) abort state or other states
                break;
            case S:
            default:

        }
        final JsonPurchaseOrder jsonPurchaseOrder = jsonQueuedPerson.getJsonPurchaseOrder();
        if (null != jsonPurchaseOrder) {
            holder.tv_payment_status.setText(Html.fromHtml("<b>Payment Status: </b>" + jsonPurchaseOrder.getPaymentStatus().getDescription()));
            holder.tv_order_state.setText(Html.fromHtml("<b>Order Status: </b>" + jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription()));
        } else {
            holder.tv_payment_status.setText(Html.fromHtml("<b>Payment Status: </b>" + context.getString(R.string.unregister_user)));
            holder.tv_order_state.setText(Html.fromHtml("<b>Order Status: </b>" + context.getString(R.string.unregister_user)));
        }
        holder.tv_queue_status.setText(Html.fromHtml("<b>Queue Status: </b>" + jsonQueuedPerson.getQueueUserState().getDescription()));
        holder.tv_customer_name.setText(TextUtils.isEmpty(jsonQueuedPerson.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonQueuedPerson.getCustomerName());
        holder.tv_business_customer_id.setText(TextUtils.isEmpty(jsonQueuedPerson.getBusinessCustomerId())
                ? Html.fromHtml("<b>Reg. Id: </b>" + context.getString(R.string.unregister_user))
                : Html.fromHtml("<b>Reg. Id: </b>" + jsonQueuedPerson.getBusinessCustomerId()));
        final String phoneNo = jsonQueuedPerson.getCustomerPhone();
        if (visibility) {
            if (null != LaunchActivity.getLaunchActivity()) {
                holder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                        PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(), phoneNo));
            }
            holder.tv_customer_mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user)))
                        new AppUtils().makeCall((Activity) context, PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(), phoneNo));
                }
            });
        } else {
            holder.tv_customer_mobile.setText(new AppUtils().hidePhoneNumberWithX(phoneNo));
        }

        holder.rl_sequence_new_time.setBackgroundColor(Color.parseColor("#e07e3d"));
        holder.tv_sequence_number.setTextColor(Color.WHITE);
        holder.tv_join_timing.setTextColor(Color.WHITE);
        holder.tv_order_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, OrderDetailActivity.class);
                in.putExtra("jsonQueuedPerson", jsonQueuedPerson);
                in.putExtra("isFromHistory", true);
                ((Activity) context).startActivity(in);
            }
        });
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.currentItemClick(listPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void currentItemClick(int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_customer_name;
        private TextView tv_business_customer_id;
        private TextView tv_customer_mobile;
        private TextView tv_payment_status;
        private TextView tv_order_data;
        private TextView tv_order_state;
        private TextView tv_sequence_number;
        private TextView tv_queue_status;
        private RelativeLayout rl_sequence_new_time;
        private TextView tv_join_timing;
        private ImageView iv_new;
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            this.tv_business_customer_id = itemView.findViewById(R.id.tv_business_customer_id);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.tv_payment_status = itemView.findViewById(R.id.tv_payment_status);
            this.tv_order_data = itemView.findViewById(R.id.tv_order_data);
            this.tv_order_state = itemView.findViewById(R.id.tv_order_state);
            this.tv_sequence_number = itemView.findViewById(R.id.tv_sequence_number);
            this.tv_queue_status = itemView.findViewById(R.id.tv_queue_status);
            this.rl_sequence_new_time = itemView.findViewById(R.id.rl_sequence_new_time);
            this.tv_join_timing = itemView.findViewById(R.id.tv_join_timing);
            this.iv_new = itemView.findViewById(R.id.iv_new);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
