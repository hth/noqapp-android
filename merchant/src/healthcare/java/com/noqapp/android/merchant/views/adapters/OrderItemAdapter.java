package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;

import java.math.BigDecimal;
import java.util.List;

public class OrderItemAdapter extends BaseAdapter {
    private Context context;
    private List<JsonPurchaseOrderProduct> jsonPurchaseOrderProductList;
    private String currencySymbol;


    public OrderItemAdapter(Context context, List<JsonPurchaseOrderProduct> notificationsList, String currencySymbol) {
        this.context = context;
        this.jsonPurchaseOrderProductList = notificationsList;
        this.currencySymbol = currencySymbol;
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
            recordHolder.tv_amount = view.findViewById(R.id.tv_total_price);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        recordHolder.tv_title.setText(jsonPurchaseOrderProductList.get(position).getProductName() + " x " + jsonPurchaseOrderProductList.get(position).getProductQuantity());
        recordHolder.tv_amount.setText(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jsonPurchaseOrderProductList.get(position).getProductPrice()).multiply(new BigDecimal(jsonPurchaseOrderProductList.get(position).getProductQuantity())).toString()));
        return view;
    }

    static class RecordHolder {
        TextView tv_title;
        TextView tv_amount;

        RecordHolder() {
        }
    }
}

