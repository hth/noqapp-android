package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.utils.CommonHelper;

import java.math.BigDecimal;
import java.util.List;

public class StoreProductFinalOrderAdapter extends BaseAdapter {
    private Context context;

    private List<JsonPurchaseOrderProduct> listDataChild;
    private CartOrderUpdate cartOrderUpdate;
    private String currencySymbol;

    public StoreProductFinalOrderAdapter(Context context, List<JsonPurchaseOrderProduct> listDataChild,
                                         CartOrderUpdate cartOrderUpdate, String currencySymbol) {
        this.context = context;
        this.listDataChild = listDataChild;
        this.cartOrderUpdate = cartOrderUpdate;
        this.currencySymbol = currencySymbol;
    }

    public List<JsonPurchaseOrderProduct> getOrders() {
        return listDataChild;
    }

    public int getCount() {
        return this.listDataChild.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ChildViewHolder childViewHolder;
        final JsonPurchaseOrderProduct storeCartItem = listDataChild.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_final_order, viewGroup, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tv_title = convertView.findViewById(R.id.tv_title);
            childViewHolder.tv_price = convertView.findViewById(R.id.tv_price);
            childViewHolder.tv_total_product_price = convertView.findViewById(R.id.tv_total_product_price);
            childViewHolder.tv_value = convertView.findViewById(R.id.tv_value);
            childViewHolder.tv_cat = convertView.findViewById(R.id.tv_cat);
            childViewHolder.tv_product_count = convertView.findViewById(R.id.tv_product_count);
            childViewHolder.btn_decrease = convertView.findViewById(R.id.btn_decrease);
            childViewHolder.btn_increase = convertView.findViewById(R.id.btn_increase);
            convertView.setTag(R.layout.list_item_final_order, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag(R.layout.list_item_final_order);
        }

        childViewHolder.tv_title.setText(storeCartItem.getProductName());
        childViewHolder.tv_value.setText(String.valueOf(storeCartItem.getProductQuantity()));
        childViewHolder.tv_product_count.setText(String.valueOf(storeCartItem.getProductQuantity()));
        childViewHolder.tv_price.setText(currencySymbol + " " + AppUtils.getPriceWithUnits(storeCartItem.getJsonStoreProduct()) + " x " + storeCartItem.getProductQuantity());
        childViewHolder.tv_total_product_price.setText(currencySymbol + " " + CommonHelper.
            displayPrice(new BigDecimal(storeCartItem.getProductPrice()).multiply(new BigDecimal(storeCartItem.getProductQuantity())).toString()));


        switch (storeCartItem.getProductType()) {
            case NV:
                childViewHolder.tv_cat.setBackgroundResource(R.drawable.round_corner_nonveg);
                break;
            default:
                childViewHolder.tv_cat.setBackgroundResource(R.drawable.round_corner_veg);
        }
        childViewHolder.btn_increase.setOnClickListener((View v) -> {
            String val = childViewHolder.tv_value.getText().toString();
            int number = 1 + (TextUtils.isEmpty(val) ? 0 : Integer.parseInt(val));
            childViewHolder.tv_value.setText("" + number);
            listDataChild.get(position).setProductQuantity(number);
            if (number <= 0) {
                listDataChild.remove(position);
                cartOrderUpdate.updateCartOrderInfo(listDataChild, showCartAmount());
            } else {
                cartOrderUpdate.updateCartOrderInfo(listDataChild, showCartAmount());
            }
            notifyDataSetChanged();
        });
        childViewHolder.btn_decrease.setOnClickListener((View v) -> {
            String val = childViewHolder.tv_value.getText().toString();
            int number = (TextUtils.isEmpty(val) ? 0 : (val.equals("0") ? 0 : Integer.parseInt(val) - 1));
            childViewHolder.tv_value.setText(String.valueOf(number));
            listDataChild.get(position).setProductQuantity(number);
            if (number <= 0) {
                listDataChild.remove(position);
                cartOrderUpdate.updateCartOrderInfo(listDataChild, showCartAmount());
            } else {
                cartOrderUpdate.updateCartOrderInfo(listDataChild, showCartAmount());
            }
            notifyDataSetChanged();
        });

        return convertView;
    }


    private String showCartAmount() {
        int price = 0;
        for (JsonPurchaseOrderProduct value : listDataChild) {
            price += value.getProductQuantity() * value.getProductPrice();
        }
        return String.valueOf(price);
    }

    public interface CartOrderUpdate {
        void updateCartOrderInfo(List<JsonPurchaseOrderProduct> list, String cartAmount);
    }

    private final class ChildViewHolder {
        private TextView tv_title;
        private TextView tv_price;
        private TextView tv_product_count;
        private TextView tv_total_product_price;
        private TextView tv_value;
        private TextView tv_cat;
        private Button btn_decrease;
        private Button btn_increase;
    }
}