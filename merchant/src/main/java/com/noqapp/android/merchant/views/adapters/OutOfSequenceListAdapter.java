package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.library.utils.PhoneFormatterUtil;

import java.util.List;

public class OutOfSequenceListAdapter extends BaseAdapter {
    private static final String TAG = OutOfSequenceListAdapter.class.getSimpleName();
    private Context context;
    private List<JsonQueuedPerson> items;

    public OutOfSequenceListAdapter(Context context, List<JsonQueuedPerson> arrayList) {
        this.context = context;
        this.items = arrayList;
    }

    public int getCount() {
        return this.items.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        final RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.listitem_out_of_sequence, null);
            recordHolder.tv_customer_name = (TextView) view.findViewById(R.id.tv_customer_name);
            recordHolder.tv_customer_mobile = (TextView) view.findViewById(R.id.tv_customer_mobile);
            recordHolder.tv_sequence_number = (TextView) view.findViewById(R.id.tv_sequence_number);
            recordHolder.tv_status_msg = (TextView) view.findViewById(R.id.tv_status_msg);
            recordHolder.iv_info = (ImageView) view.findViewById(R.id.iv_info);
            recordHolder.cardview = (CardView) view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }

        JsonQueuedPerson jsonQueuedPerson = items.get(position);
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
            default:
                Log.e(TAG, "Reached unsupported condition state=" + jsonQueuedPerson.getQueueUserState());
                throw new UnsupportedOperationException("Reached unsupported condition");
        }
        return view;
    }

    static class RecordHolder {
        TextView tv_customer_name;
        TextView tv_customer_mobile;
        TextView tv_sequence_number;
        TextView tv_status_msg;
        ImageView iv_info;
        CardView cardview;

        RecordHolder() {
        }
    }
}
