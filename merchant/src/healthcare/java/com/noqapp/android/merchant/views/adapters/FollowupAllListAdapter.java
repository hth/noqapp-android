package com.noqapp.android.merchant.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import java.util.List;

public class FollowupAllListAdapter extends RecyclerView.Adapter<FollowupAllListAdapter.MyViewHolder> {
    private final Context context;
    private final ViewAllPeopleInQAdapter.OnItemClickListener listener;
    private List<JsonQueuedPerson> dataSet;
    private boolean visibility;

    public FollowupAllListAdapter(List<JsonQueuedPerson> data, Context context, ViewAllPeopleInQAdapter.OnItemClickListener listener, boolean visibility) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        this.visibility =visibility;
    }

    @Override
    public FollowupAllListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_followup_list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final FollowupAllListAdapter.MyViewHolder holder, final int listPosition) {
        final JsonQueuedPerson jsonQueuedPerson = dataSet.get(listPosition);
        holder.tv_customer_name.setText(TextUtils.isEmpty(jsonQueuedPerson.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonQueuedPerson.getCustomerName());
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
        holder.card_view.setOnClickListener(v -> {
            if (null != listener) {
                listener.currentItemClick(listPosition);
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
        private TextView tv_customer_mobile;
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
