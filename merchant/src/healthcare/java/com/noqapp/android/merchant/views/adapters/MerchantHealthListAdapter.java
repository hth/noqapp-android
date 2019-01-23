package com.noqapp.android.merchant.views.adapters;


import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MerchantHealthListAdapter extends BaseAdapter {
    private Context context;
    private List<JsonTopic> items;
    private List<JsonPreferredBusiness> jsonPreferredBusinesses;

    public MerchantHealthListAdapter(Context context, List<JsonTopic> arrayList, List<JsonPreferredBusiness> jsonPreferredBusinesses) {
        this.context = context;
        this.items = arrayList;
        this.jsonPreferredBusinesses = jsonPreferredBusinesses;
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
            view = layoutInflater.inflate(R.layout.list_item_health_merchant, viewGroup, false);
            recordHolder.tv_number = view.findViewById(R.id.tv_number);
            recordHolder.tv_queue_name = view.findViewById(R.id.tv_queue_name);
            recordHolder.acsp_store = view.findViewById(R.id.acsp_store);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        JsonTopic jsonTopic = items.get(position);
        recordHolder.tv_number.setText("#" + String.valueOf(position + 1));
        recordHolder.tv_queue_name.setText(jsonTopic.getDisplayName());
        recordHolder.cardview.setCardBackgroundColor(Color.TRANSPARENT);
        recordHolder.tv_queue_name.setTextColor(ContextCompat.getColor(context, R.color.color_action_bar));

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(context, jsonPreferredBusinesses);
        recordHolder.acsp_store.setAdapter(spinAdapter);
        return view;
    }

    static class RecordHolder {
        TextView tv_number;
        TextView tv_queue_name;
        AppCompatSpinner acsp_store;
        CardView cardview;

        RecordHolder() {
        }
    }
}
