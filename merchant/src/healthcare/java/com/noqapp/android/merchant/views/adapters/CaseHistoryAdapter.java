package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

import java.util.List;

public class CaseHistoryAdapter extends BaseAdapter {
    private final Context context;
    private List<JsonQueuedPerson> dataSet;

    public CaseHistoryAdapter(List<JsonQueuedPerson> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }


    public View getView(int position, View view, ViewGroup viewGroup) {
        MyViewHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new MyViewHolder();
            view = layoutInflater.inflate(R.layout.list_item_case_history, viewGroup, false);
            recordHolder.tv_patient_name = view.findViewById(R.id.tv_patient_name);
            recordHolder.tv_patient_details = view.findViewById(R.id.tv_patient_details);
            view.setTag(recordHolder);
        } else {
            recordHolder = (MyViewHolder) view.getTag();
        }
        recordHolder.tv_patient_name.setText(dataSet.get(position).getCustomerName());
        recordHolder.tv_patient_details.setText(dataSet.get(position).getCustomerPhone());

        return view;
    }

    public int getCount() {
        return this.dataSet.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }


    public static class MyViewHolder {
        private TextView tv_patient_name;
        private TextView tv_patient_details;
        private CardView card_view;
    }
}
