package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<JsonPreferredBusiness> {

    private final Context mContext;
    private final List<JsonPreferredBusiness> items;

    public CustomSpinnerAdapter(Context context, List<JsonPreferredBusiness> items) {
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
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        TextView lbl = v.findViewById(R.id.tv_title);
        lbl.setTextColor(Color.BLACK);
        lbl.setText(items.get(position).getDisplayName());
        return v;
    }
}
