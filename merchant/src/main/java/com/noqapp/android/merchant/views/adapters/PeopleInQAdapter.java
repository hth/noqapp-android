package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.common.utils.PhoneFormatterUtil;

import java.util.List;

public class PeopleInQAdapter extends RecyclerView.Adapter<PeopleInQAdapter.MyViewHolder> {

    private static final String TAG = PeopleInQAdapter.class.getSimpleName();
    private final Context context;
    private List<JsonQueuedPerson> dataSet;

    public interface PeopleInQAdapterClick{

        void PeopleInQClick(int position);
    }

    private PeopleInQAdapterClick peopleInQAdapterClick;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_customer_name;
        TextView tv_customer_mobile;
        TextView tv_sequence_number;
        TextView tv_status_msg;
        ImageView iv_info;
        CardView cardview;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = (TextView) itemView.findViewById(R.id.tv_customer_name);
            this.tv_customer_mobile = (TextView) itemView.findViewById(R.id.tv_customer_mobile);
            this.tv_sequence_number = (TextView) itemView.findViewById(R.id.tv_sequence_number);
            this.tv_status_msg = (TextView) itemView.findViewById(R.id.tv_status_msg);
            this.iv_info = (ImageView) itemView.findViewById(R.id.iv_info);
            this.cardview = (CardView) itemView.findViewById(R.id.cardview);
        }
    }

    public PeopleInQAdapter(List<JsonQueuedPerson> data, Context context,PeopleInQAdapterClick peopleInQAdapterClick) {
        this.dataSet = data;
        this.context = context;
        this.peopleInQAdapterClick = peopleInQAdapterClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_people_queue_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder recordHolder, final int position) {
        JsonQueuedPerson jsonQueuedPerson = dataSet.get(position);
        final String phoneNo = jsonQueuedPerson.getCustomerPhone();

        recordHolder.tv_sequence_number.setText(String.valueOf(jsonQueuedPerson.getToken()));
        recordHolder.tv_customer_name.setText(TextUtils.isEmpty(jsonQueuedPerson.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonQueuedPerson.getCustomerName());
        recordHolder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                //TODO : @ Chandra Please change the country code dynamically, country code you can get it from TOPIC
                PhoneFormatterUtil.formatNumber("IN", phoneNo));
        recordHolder.tv_customer_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recordHolder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user)))
                    new AppUtils().makeCall(LaunchActivity.getLaunchActivity(), phoneNo);
            }
        });
        recordHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peopleInQAdapterClick.PeopleInQClick(position);
            }
        });
        switch (jsonQueuedPerson.getQueueUserState()) {
            case Q:
                if (TextUtils.isEmpty(jsonQueuedPerson.getServerDeviceId())) {
                    recordHolder.iv_info.setBackgroundResource(R.drawable.acquire_available);
                    recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_available));
                } else if (jsonQueuedPerson.getServerDeviceId().equals(UserUtils.getDeviceId())) {
                    recordHolder.iv_info.setBackgroundResource(R.drawable.acquired_by_you);
                    recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_acquired_by_you));
                } else {
                    recordHolder.iv_info.setBackgroundResource(R.drawable.acquired_already);
                    recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_already_acquired));
                }
                recordHolder.cardview.setCardBackgroundColor(Color.WHITE);
                break;
            case A:
                recordHolder.iv_info.setBackgroundResource(R.drawable.acquire_cancel_by_user);
                recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(
                        context, R.color.disable_list));
                recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_left_queue));
                break;
            case N:
                recordHolder.iv_info.setBackgroundResource(R.drawable.acquire_cancel_by_user);
                recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(
                        context, R.color.disable_list));
                recordHolder.tv_status_msg.setText(context.getString(R.string.msg_merchant_skip));
                break;
            case S:
                recordHolder.iv_info.setBackgroundResource(R.drawable.acquire_cancel_by_user);
                recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(
                        context, R.color.disable_list));
                recordHolder.tv_status_msg.setText(context.getString(R.string.msg_merchant_served));
                break;
            default:
                Log.e(TAG, "Reached unsupported condition state=" + jsonQueuedPerson.getQueueUserState());
                throw new UnsupportedOperationException("Reached unsupported condition");
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
