package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.order.JsonPurchaseOrderProduct;
import com.noqapp.android.merchant.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class OrderItemAdapter extends BaseAdapter {
    private Context context;
    private List<JsonPurchaseOrderProduct> jsonPurchaseOrderProductList;

    public OrderItemAdapter(Context context, List<JsonPurchaseOrderProduct> notificationsList) {
        this.context = context;
        this.jsonPurchaseOrderProductList = notificationsList;
    }

    public int getCount() {
        return this.jsonPurchaseOrderProductList.size();
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
            view = layoutInflater.inflate(R.layout.list_item_order, viewGroup, false);
            recordHolder.tv_title = view.findViewById(R.id.tv_title);
            recordHolder.tv_amount = view.findViewById(R.id.tv_amount);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        recordHolder.tv_title.setText(jsonPurchaseOrderProductList.get(position).getProductName() + " x " + jsonPurchaseOrderProductList.get(position).getProductQuantity());
        recordHolder.tv_amount.setText(String.valueOf(jsonPurchaseOrderProductList.get(position).getProductPrice()*1.0 / 100));
        return view;
    }

    static class RecordHolder {
        TextView tv_title;
        TextView tv_amount;

        RecordHolder() {
        }
    }
}

