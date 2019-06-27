package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.views.fragments.ChartListFragment;

import java.util.List;

public class MerchantChartListAdapter extends BaseAdapter {
    private Context context;
    private List<JsonTopic> items;

    public MerchantChartListAdapter(Context context, List<JsonTopic> arrayList) {
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
            view = layoutInflater.inflate(R.layout.listitem_merchant_chart, viewGroup, false);
            recordHolder.tv_number = view.findViewById(R.id.tv_number);
            recordHolder.tv_queue_name = view.findViewById(R.id.tv_queue_name);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        JsonTopic jsonTopic = items.get(position);
        recordHolder.tv_number.setText("#" + String.valueOf(position + 1));
        recordHolder.tv_queue_name.setText(jsonTopic.getDisplayName());
        if (position == ChartListFragment.selected_pos) {
            recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(context, R.color.pressed_color));
            recordHolder.tv_queue_name.setTextColor(Color.WHITE);
        } else {
            recordHolder.cardview.setCardBackgroundColor(Color.WHITE);
            recordHolder.tv_queue_name.setTextColor(ContextCompat.getColor(context, R.color.color_action_bar));
        }

        return view;
    }

    static class RecordHolder {
        TextView tv_number;
        TextView tv_queue_name;
        CardView cardview;

        RecordHolder() {
        }
    }
}
