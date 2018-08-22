package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.JsonProfile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DependentAdapter extends ArrayAdapter<JsonProfile> {

    private final Context mContext;
    private final List<JsonProfile> items;

    public DependentAdapter(Context context,
                            List<JsonProfile> items) {
        super(context, R.layout.spinner_item, items);
        mContext = context;
        this.items = items;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.spinner_item, null);
        }
        TextView lbl = v.findViewById(R.id.tv_title);
        lbl.setTextColor(Color.BLACK);
        lbl.setText(items.get(position).getName());
        return v;


    }
}

