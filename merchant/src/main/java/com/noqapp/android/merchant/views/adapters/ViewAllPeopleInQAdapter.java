package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            this.tv_business_customer_id = itemView.findViewById(R.id.tv_business_customer_id);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
