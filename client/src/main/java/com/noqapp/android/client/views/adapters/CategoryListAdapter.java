package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.Formatter;

import java.util.List;

public class CategoryListAdapter extends BaseAdapter {
    private Context context;
    private List<JsonQueue> items;

    public CategoryListAdapter(Context context, List<JsonQueue> arrayList) {
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
            view = layoutInflater.inflate(R.layout.listitem_category, null);
           // recordHolder.tv_number = (TextView) view.findViewById(R.id.tv_number);
            recordHolder.tv_queue_name = (TextView) view.findViewById(R.id.tv_queue_name);
            recordHolder.tv_store_timing = (TextView) view.findViewById(R.id.tv_store_timing);
           // recordHolder.tv_inqueue = (TextView) view.findViewById(R.id.tv_inqueue);
           // recordHolder.tv_label = (TextView) view.findViewById(R.id.tv_label);
            recordHolder.cardview = (CardView) view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        JsonQueue jsonQueue = items.get(position);

        recordHolder.tv_queue_name.setText(jsonQueue.getDisplayName());
        recordHolder.tv_store_timing.setText(context.getString(R.string.store_hour) + " " +
                Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour()) + " - " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getEndHour()));
        return view;
    }

    static class RecordHolder {
       // TextView tv_number;
        TextView tv_queue_name;
        TextView tv_store_timing;
       // TextView tv_inqueue;
       // TextView tv_label;
        CardView cardview;

        RecordHolder() {
        }
    }
}
