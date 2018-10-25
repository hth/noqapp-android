package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EnumAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final List<String> items;

    public EnumAdapter(Context context, List<String> items) {
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
        TextView lbl = v.findViewById(R.id.tv_title);
        lbl.setTextColor(Color.BLACK);
        lbl.setText(items.get(position));
        return v;
    }
}

