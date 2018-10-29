package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.activities.StoreMenuActivity;
import com.noqapp.android.common.beans.ChildData;
import com.noqapp.android.common.beans.store.JsonStoreProduct;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private List<ChildData> menuItemsList;
    private StoreMenuActivity storeMenuActivity;
    private CartOrderUpdate cartOrderUpdate;

    public MenuAdapter(Context context, List<ChildData> menuItemsList, StoreMenuActivity storeMenuActivity, CartOrderUpdate cartOrderUpdate) {
        this.context = context;
        this.menuItemsList = menuItemsList;
        this.storeMenuActivity = storeMenuActivity;
        this.cartOrderUpdate = cartOrderUpdate;
    }

    public int getCount() {
        return this.menuItemsList.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ChildViewHolder childViewHolder;
        final ChildData childData = menuItemsList.get(position);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_menu_child, viewGroup, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tv_child_title = convertView.findViewById(R.id.tv_child_title);
            childViewHolder.tv_child_title_details = convertView.findViewById(R.id.tv_child_title_details);
            childViewHolder.tv_value = convertView.findViewById(R.id.tv_value);
            childViewHolder.tv_price = convertView.findViewById(R.id.tv_price);
            childViewHolder.tv_discounted_price = convertView.findViewById(R.id.tv_discounted_price);
            childViewHolder.btn_increase = convertView.findViewById(R.id.btn_increase);
            childViewHolder.btn_decrease = convertView.findViewById(R.id.btn_decrease);
            childViewHolder.tv_cat = convertView.findViewById(R.id.tv_cat);
            convertView.setTag(R.layout.list_item_menu_child, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.list_item_menu_child);
        }
        final JsonStoreProduct jsonStoreProduct = childData.getJsonStoreProduct();
        childViewHolder.tv_child_title.setText(jsonStoreProduct.getProductName());
        childViewHolder.tv_child_title_details.setText(jsonStoreProduct.getProductInfo());
        childViewHolder.tv_value.setText(String.valueOf(childData.getChildInput()));
        //TODO chandra use County Code of the store to decide on Currency type
        childViewHolder.tv_price.setText(context.getString(R.string.rupee) + " " + jsonStoreProduct.getDisplayPrice());
        childViewHolder.tv_discounted_price.setText(
                context.getString(R.string.rupee)
                        + " "
                        + calculateDiscountPrice(jsonStoreProduct.getDisplayPrice(), jsonStoreProduct.getDisplayDiscount()));
        if (jsonStoreProduct.getProductDiscount() > 0) {
            childViewHolder.tv_price.setPaintFlags(childViewHolder.tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            childViewHolder.tv_discounted_price.setVisibility(View.VISIBLE);
        } else {
            childViewHolder.tv_price.setPaintFlags(childViewHolder.tv_price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            childViewHolder.tv_discounted_price.setVisibility(View.INVISIBLE);
        }
        switch (jsonStoreProduct.getProductType()) {
            case NV:
                childViewHolder.tv_cat.setBackgroundResource(R.drawable.button_drawable_red);
                break;
            default:
                childViewHolder.tv_cat.setBackgroundResource(R.drawable.round_corner_veg);
        }
        childViewHolder.btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = childViewHolder.tv_value.getText().toString();
                int number = 1 + (TextUtils.isEmpty(val) ? 0 : Integer.parseInt(val));
                childViewHolder.tv_value.setText("" + number);
                menuItemsList
                        .get(position).setChildInput(number);
                if (number <= 0) {
                    storeMenuActivity.getOrders().remove(jsonStoreProduct.getProductId());
                    cartOrderUpdate.updateCartOrderInfo(showCartAmount());
                } else {
                    storeMenuActivity.getOrders().put(jsonStoreProduct.getProductId(), menuItemsList
                            .get(position));
                    cartOrderUpdate.updateCartOrderInfo(showCartAmount());
                }
                notifyDataSetChanged();
            }
        });
        childViewHolder.btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = childViewHolder.tv_value.getText().toString();
                int number = (TextUtils.isEmpty(val) ? 0 : (val.equals("0") ? 0 : Integer.parseInt(val) - 1));
                childViewHolder.tv_value.setText("" + number);
                menuItemsList
                        .get(position).setChildInput(number);
                if (number <= 0) {
                    storeMenuActivity.getOrders().remove(jsonStoreProduct.getProductId());
                    cartOrderUpdate.updateCartOrderInfo(showCartAmount());
                } else {
                    storeMenuActivity.getOrders().put(jsonStoreProduct.getProductId(), menuItemsList
                            .get(position));
                    cartOrderUpdate.updateCartOrderInfo(showCartAmount());
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private double calculateDiscountPrice(String displayPrice, String discountAmount) {
        double price = Double.valueOf(displayPrice);
        double discountAmountValue = Double.valueOf(discountAmount);
        return (price - discountAmountValue);
    }

    private int showCartAmount() {
        int price = 0;
        for (ChildData value : storeMenuActivity.getOrders().values()) {
            price += value.getChildInput() * value.getJsonStoreProduct().getProductPrice();
        }
        return price / 100;
    }

    public interface CartOrderUpdate {
        void updateCartOrderInfo(int amountString);
    }

    public final class ChildViewHolder {
        TextView tv_child_title;
        TextView tv_child_title_details;
        TextView tv_price;
        TextView tv_value;
        TextView tv_discounted_price;
        TextView tv_cat;
        Button btn_decrease;
        Button btn_increase;
    }
}
