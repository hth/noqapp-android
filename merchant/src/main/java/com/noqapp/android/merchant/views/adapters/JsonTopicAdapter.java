package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;

import java.util.List;

public class JsonTopicAdapter extends ArrayAdapter<JsonTopic> {
    private final Context mContext;
    private final List<JsonTopic> items;

    public JsonTopicAdapter(Context context, List<JsonTopic> items) {
        super(context, R.layout.spinner_item, items);
        mContext = context;
        this.items = items;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (null == v) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        TextView lbl = (TextView) v.findViewById(R.id.tv_title);
        lbl.setTextColor(Color.BLACK);
        lbl.setText(items.get(position).getDisplayName());
        return v;
    }
}

