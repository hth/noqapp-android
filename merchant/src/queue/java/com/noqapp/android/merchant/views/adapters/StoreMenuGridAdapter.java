package com.noqapp.android.merchant.views.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.StoreMenuActivity;

import java.util.List;

public class StoreMenuGridAdapter extends RecyclerView.Adapter {

    public void setMenuItemsList(List<StoreCartItem> menuItemsList) {
        this.menuItemsList = menuItemsList;
        notifyDataSetChanged();
    }

    private List<StoreCartItem> menuItemsList;
    private StoreMenuActivity storeMenuActivity;
    private CartOrderUpdate cartOrderUpdate;


    public StoreMenuGridAdapter(List<StoreCartItem> menuItemsList, StoreMenuActivity storeMenuActivity, CartOrderUpdate cartOrderUpdate) {
        this.menuItemsList = menuItemsList;
        this.storeMenuActivity = storeMenuActivity;
        this.cartOrderUpdate = cartOrderUpdate;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_grid, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder recordHolder = (MyViewHolder) viewHolder;
        final StoreCartItem storeCartItem = menuItemsList.get(position);
        final JsonStoreProduct jsonStoreProduct = storeCartItem.getJsonStoreProduct();
        recordHolder.tv_child_title.setText(jsonStoreProduct.getProductName());
        recordHolder.cardview.setOnClickListener(v -> {
            StoreCartItem sci = menuItemsList.get(position);
            sci.setChildInput(1);
            storeMenuActivity.getOrders().put(jsonStoreProduct.getProductId(), sci);
            cartOrderUpdate.cartStatusChanged();
        });
    }


    public interface CartOrderUpdate {
        void cartStatusChanged();
    }


    @Override
    public int getItemCount() {
        return menuItemsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_child_title;
        private CardView cardview;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_child_title = itemView.findViewById(R.id.tv_child_title);
            this.cardview = itemView.findViewById(R.id.cardview);
        }
    }
}
