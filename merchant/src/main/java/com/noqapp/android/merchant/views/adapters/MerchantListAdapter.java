package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.views.fragments.MerchantListFragment;
import com.noqapp.android.merchant.R;

import java.util.List;


public class MerchantListAdapter extends BaseAdapter {
    private Context context;
    private List<JsonTopic> items;

    public MerchantListAdapter(Context context, List<JsonTopic> arrayList) {
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
        RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.listitem_currentqueue, null);
            recordHolder.tv_number = (TextView) view.findViewById(R.id.tv_number);
            recordHolder.tv_queue_name = (TextView) view
                    .findViewById(R.id.tv_queue_name);
            recordHolder.tv_serving_no = (TextView) view
                    .findViewById(R.id.tv_serving_no);
            recordHolder.tv_inqueue = (TextView) view
                    .findViewById(R.id.tv_inqueue);

            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        JsonTopic jsonTopic = items.get(position);
        recordHolder.tv_number.setText("#" + String.valueOf(position + 1));
        recordHolder.tv_queue_name.setText(jsonTopic.getDisplayName());
        recordHolder.tv_serving_no.setText("Serving now: " + String.valueOf(jsonTopic.getServingNumber()));
        recordHolder.tv_inqueue.setText(String.valueOf(jsonTopic.getRemaining()));

        if (position == MerchantListFragment.selected_pos) {
            view.setBackgroundColor(ContextCompat.getColor(
                    context, R.color.pressed_color));
            recordHolder.tv_queue_name.setTextColor(Color.WHITE);
            recordHolder.tv_serving_no.setTextColor(Color.WHITE);
            recordHolder.tv_inqueue.setTextColor(Color.WHITE);

        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
            recordHolder.tv_queue_name.setTextColor(ContextCompat.getColor(context, R.color.color_action_bar));
            recordHolder.tv_serving_no.setTextColor(ContextCompat.getColor(context, R.color.color_list_subtitle));
            recordHolder.tv_inqueue.setTextColor(ContextCompat.getColor(context, R.color.color_list_subtitle));
        }
        return view;
    }

    static class RecordHolder {
        TextView tv_number;
        TextView tv_queue_name;
        TextView tv_serving_no;
        TextView tv_inqueue;

        RecordHolder() {
        }
    }

}
