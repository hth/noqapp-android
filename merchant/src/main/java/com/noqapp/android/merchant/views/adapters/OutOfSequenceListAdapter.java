package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

import java.util.List;

public class OutOfSequenceListAdapter extends BaseAdapter {
    private Context context;
    private List<JsonQueuedPerson> items;

    public OutOfSequenceListAdapter(Context context, List<JsonQueuedPerson> arrayList) {
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
            view = layoutInflater.inflate(R.layout.listitem_out_of_sequence, null);
            recordHolder.tv_customer_name = (TextView) view.findViewById(R.id.tv_customer_name);
            recordHolder.tv_sequence_number = (TextView) view.findViewById(R.id.tv_sequence_number);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        JsonQueuedPerson jsonQueuedPerson = items.get(position);
        recordHolder.tv_sequence_number.setText(String.valueOf(jsonQueuedPerson.getToken()));
        recordHolder.tv_customer_name.setText(TextUtils.isEmpty(jsonQueuedPerson.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonQueuedPerson.getCustomerName());


        return view;
    }

    static class RecordHolder {
        TextView tv_customer_name;
        TextView tv_sequence_number;

        RecordHolder() {
        }
    }
}
