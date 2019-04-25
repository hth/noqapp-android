package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import android.app.Activity;
import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class ViewAllPeopleInQAdapter extends RecyclerView.Adapter<ViewAllPeopleInQAdapter.MyViewHolder> {
    private final Context context;
    private final OnItemClickListener listener;
    private List<JsonQueuedPerson> dataSet;
    private boolean visibility;

    public ViewAllPeopleInQAdapter(List<JsonQueuedPerson> data, Context context, OnItemClickListener listener,boolean visibility) {
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
        holder.tv_payment_status.setText(Html.fromHtml("<b>Payment Status: </b>" + jsonQueuedPerson.getJsonPurchaseOrder().getPaymentStatus().getDescription()));
        holder.tv_order_state.setText(Html.fromHtml("<b>Order Status: </b>" + jsonQueuedPerson.getJsonPurchaseOrder().getPresentOrderState().getDescription()));
        holder.tv_customer_name.setText(TextUtils.isEmpty(jsonQueuedPerson.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonQueuedPerson.getCustomerName());
        holder.tv_business_customer_id.setText(TextUtils.isEmpty(jsonQueuedPerson.getBusinessCustomerId())
                ? Html.fromHtml("<b>Reg. Id: </b>" + context.getString(R.string.unregister_user))
                : Html.fromHtml("<b>Reg. Id: </b>" + jsonQueuedPerson.getBusinessCustomerId()));
        final String phoneNo = jsonQueuedPerson.getCustomerPhone();
        holder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(), phoneNo));
        if (visibility) {
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

        if (jsonQueuedPerson.isClientVisitedThisBusiness()) {
            //recordHolder.rl_sequence_new_time.setBackgroundColor(Color.TRANSPARENT);
            holder.rl_sequence_new_time.setBackgroundColor(Color.parseColor("#9DC5C3"));
            holder.tv_sequence_number.setTextColor(Color.BLACK);
            holder.tv_join_timing.setTextColor(Color.BLACK);
        } else {
            holder.rl_sequence_new_time.setBackgroundColor(Color.parseColor("#e07e3d"));
            holder.tv_sequence_number.setTextColor(Color.WHITE);
            holder.tv_join_timing.setTextColor(Color.WHITE);
        }
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
        private TextView tv_order_state;
        private TextView tv_sequence_number;
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
            this.tv_order_state = itemView.findViewById(R.id.tv_order_state);
            this.tv_sequence_number = itemView.findViewById(R.id.tv_sequence_number);
            this.rl_sequence_new_time = itemView.findViewById(R.id.rl_sequence_new_time);
            this.tv_join_timing = itemView.findViewById(R.id.tv_join_timing);
            this.iv_new = itemView.findViewById(R.id.iv_new);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
