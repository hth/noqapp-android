package com.noqapp.android.merchant.views.adapters;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.StoreMenuActivity;

import java.math.BigDecimal;
import java.util.List;

public class StoreMenuOrderAdapter extends RecyclerView.Adapter {

    public void setMenuItemsList(List<StoreCartItem> menuItemsList) {
        this.menuItemsList = menuItemsList;
        notifyDataSetChanged();
    }

    private List<StoreCartItem> menuItemsList;
    private StoreMenuActivity storeMenuActivity;
    private CartOrderUpdate cartOrderUpdate;
    private String currencySymbol;

    public StoreMenuOrderAdapter(List<StoreCartItem> menuItemsList, StoreMenuActivity storeMenuActivity, CartOrderUpdate cartOrderUpdate, String currencySymbol) {
        this.menuItemsList = menuItemsList;
        this.storeMenuActivity = storeMenuActivity;
        this.cartOrderUpdate = cartOrderUpdate;
        this.currencySymbol = currencySymbol;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_child, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder recordHolder = (MyViewHolder) viewHolder;

        final StoreCartItem storeCartItem = menuItemsList.get(position);
        final JsonStoreProduct jsonStoreProduct = storeCartItem.getJsonStoreProduct();

        recordHolder.tv_title.setText(jsonStoreProduct.getProductName());
        recordHolder.tv_value.setText(String.valueOf(storeCartItem.getChildInput()));
        recordHolder.tv_price.setText(currencySymbol + AppUtils.getPriceWithUnits(jsonStoreProduct));
        recordHolder.tv_discounted_price.setText(currencySymbol + storeCartItem.getFinalDiscountedPrice());
        if (jsonStoreProduct.getProductDiscount() > 0) {
            recordHolder.tv_price.setPaintFlags(recordHolder.tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            recordHolder.tv_discounted_price.setVisibility(View.VISIBLE);
        } else {
            recordHolder.tv_price.setPaintFlags(recordHolder.tv_price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            recordHolder.tv_discounted_price.setVisibility(View.INVISIBLE);
        }
        recordHolder.btn_increase.setOnClickListener(v -> {
            String val = recordHolder.tv_value.getText().toString();
            int number = 1 + (TextUtils.isEmpty(val) ? 0 : Integer.parseInt(val));
            recordHolder.tv_value.setText(number);
            menuItemsList.get(position).setChildInput(number);
            if (number <= 0) {
                storeMenuActivity.getOrders().remove(jsonStoreProduct.getProductId());
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
                setMenuItemsList(storeMenuActivity.getCartList());
            } else {
                storeMenuActivity.getOrders().put(jsonStoreProduct.getProductId(), menuItemsList.get(position));
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
            }
            notifyDataSetChanged();
        });
        recordHolder.btn_decrease.setOnClickListener(v -> {
            String val = recordHolder.tv_value.getText().toString();
            int number = (TextUtils.isEmpty(val) ? 0 : (val.equals("0") ? 0 : Integer.parseInt(val) - 1));
            recordHolder.tv_value.setText(String.valueOf(number));
            menuItemsList.get(position).setChildInput(number);
            if (number <= 0) {
                storeMenuActivity.getOrders().remove(jsonStoreProduct.getProductId());
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
                setMenuItemsList(storeMenuActivity.getCartList());
            } else {
                storeMenuActivity.getOrders().put(jsonStoreProduct.getProductId(), menuItemsList.get(position));
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
            }
            notifyDataSetChanged();
        });
    }

    public BigDecimal showCartAmount() {
        BigDecimal price = new BigDecimal(0);
        for (StoreCartItem value : storeMenuActivity.getOrders().values()) {
            price = price.add(new BigDecimal(value.getChildInput()).multiply(value.getFinalDiscountedPrice()));
        }
        return price;
    }

    public interface CartOrderUpdate {
        void updateCartOrderInfo(BigDecimal amountString);
    }

    @Override
    public int getItemCount() {
        return menuItemsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_price;
        TextView tv_value;
        TextView tv_discounted_price;
        Button btn_decrease;
        Button btn_increase;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_value = itemView.findViewById(R.id.tv_value);
            this.tv_price = itemView.findViewById(R.id.tv_price);
            this.tv_discounted_price = itemView.findViewById(R.id.tv_discounted_price);
            this.btn_increase = itemView.findViewById(R.id.btn_increase);
            this.btn_decrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}
