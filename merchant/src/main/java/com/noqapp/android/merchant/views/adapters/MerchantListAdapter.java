package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.views.fragments.MerchantListFragment;

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
            view = layoutInflater.inflate(R.layout.listitem_merchant, null);
            recordHolder.tv_number = view.findViewById(R.id.tv_number);
            recordHolder.tv_queue_name = view.findViewById(R.id.tv_queue_name);
            recordHolder.tv_serving_no = view.findViewById(R.id.tv_serving_no);
            recordHolder.tv_inqueue = view.findViewById(R.id.tv_inqueue);
            recordHolder.tv_label = view.findViewById(R.id.tv_label);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        JsonTopic jsonTopic = items.get(position);
        recordHolder.tv_number.setText("#" + String.valueOf(position + 1));
        recordHolder.tv_queue_name.setText(jsonTopic.getDisplayName());
        if (jsonTopic.getQueueStatus() == QueueStatusEnum.D) {
            recordHolder.tv_serving_no.setText("Done");
        } else if (jsonTopic.getToken() == 0) {
            recordHolder.tv_serving_no.setText("Not Started");
        } else {
            recordHolder.tv_serving_no.setText("Serving Now: " + String.valueOf(jsonTopic.getServingNumber()));
        }
        recordHolder.tv_inqueue.setText(String.valueOf(jsonTopic.getRemaining()));

        if (position == MerchantListFragment.selected_pos) {
            recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(context, R.color.pressed_color));
            recordHolder.tv_queue_name.setTextColor(Color.WHITE);
            recordHolder.tv_serving_no.setTextColor(Color.WHITE);
            recordHolder.tv_inqueue.setTextColor(Color.WHITE);
            recordHolder.tv_label.setTextColor(Color.WHITE);
        } else {
            recordHolder.cardview.setCardBackgroundColor(Color.TRANSPARENT);
            recordHolder.tv_queue_name.setTextColor(ContextCompat.getColor(context, R.color.color_action_bar));
            recordHolder.tv_serving_no.setTextColor(ContextCompat.getColor(context, R.color.color_list_subtitle));
            recordHolder.tv_inqueue.setTextColor(ContextCompat.getColor(context, R.color.color_list_subtitle));
            recordHolder.tv_label.setTextColor(ContextCompat.getColor(context, R.color.color_list_subtitle));
        }

        if (jsonTopic.getRemaining() <= 0) {
            recordHolder.tv_inqueue.setBackgroundResource(R.drawable.circle_bg_unselect_drawable);
            if (position == MerchantListFragment.selected_pos) {
                recordHolder.tv_inqueue.setTextColor(Color.WHITE);
            } else {
                recordHolder.tv_inqueue.setTextColor(ContextCompat.getColor(context, R.color.color_list_subtitle));
            }
        } else {
            recordHolder.tv_inqueue.setBackgroundResource(R.drawable.circle_bg_select_drawable);
            recordHolder.tv_inqueue.setTextColor(Color.WHITE);
        }
        return view;
    }

    static class RecordHolder {
        TextView tv_number;
        TextView tv_queue_name;
        TextView tv_serving_no;
        TextView tv_inqueue;
        TextView tv_label;
        CardView cardview;

        RecordHolder() {
        }
    }
}
