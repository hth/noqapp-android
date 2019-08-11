package com.noqapp.android.client.views.adapters;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.views.activities.StoreMenuActivity;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoreProductMenuAdapter extends RecyclerView.Adapter {
    private List<StoreCartItem> menuItemsList;
    private StoreMenuActivity storeMenuActivity;
    private CartOrderUpdate cartOrderUpdate;
    private String currencySymbol;

    public StoreProductMenuAdapter(List<StoreCartItem> menuItemsList, StoreMenuActivity storeMenuActivity,
                                   CartOrderUpdate cartOrderUpdate, String currencySymbol) {
        this.menuItemsList = menuItemsList;
        this.storeMenuActivity = storeMenuActivity;
        this.cartOrderUpdate = cartOrderUpdate;
        this.currencySymbol = currencySymbol;
    }

    @Override
    public int getItemCount() {
        return menuItemsList.size();
    }

    public interface CartOrderUpdate {
        void updateCartOrderInfo(int amountString);
    }

    private int showCartAmount() {
        int price = 0;
        for (StoreCartItem value : storeMenuActivity.getOrders().values()) {
            price += value.getChildInput() * value.getFinalDiscountedPrice();
        }
        return price;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_menu_child, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int position) {
        ProductViewHolder productViewHolder = (ProductViewHolder) Vholder;
        final StoreCartItem storeCartItem = menuItemsList.get(position);
        final JsonStoreProduct jsonStoreProduct = storeCartItem.getJsonStoreProduct();
        productViewHolder.tv_title.setText(jsonStoreProduct.getProductName());
        productViewHolder.tv_product_details.setText(jsonStoreProduct.getProductInfo());
        productViewHolder.tv_value.setText(String.valueOf(storeCartItem.getChildInput()));
        productViewHolder.tv_price.setText(currencySymbol + " " + AppUtilities.getPriceWithUnits(jsonStoreProduct));
        productViewHolder.tv_discounted_price.setText(currencySymbol + " " + storeCartItem.getFinalDiscountedPrice());
        String url = TextUtils.isEmpty(jsonStoreProduct.getProductImage()) ? "https://www.uig-hotel-skye.com/images/Food/Food-7.jpg" :
                jsonStoreProduct.getProductImage();
        Picasso.get().load(url).into(productViewHolder.iv_product_image);


        if (jsonStoreProduct.getProductDiscount() > 0) {
            productViewHolder.tv_price.setPaintFlags(productViewHolder.tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            productViewHolder.tv_discounted_price.setVisibility(View.VISIBLE);
        } else {
            productViewHolder.tv_price.setPaintFlags(productViewHolder.tv_price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            productViewHolder.tv_discounted_price.setVisibility(View.INVISIBLE);
        }
        switch (jsonStoreProduct.getProductType()) {
            case NV:
                productViewHolder.tv_cat.setBackgroundResource(R.drawable.round_corner_nonveg);
                break;
            default:
                productViewHolder.tv_cat.setBackgroundResource(R.drawable.round_corner_veg);
        }
        productViewHolder.btn_increase.setOnClickListener((View v) -> {
            String val = productViewHolder.tv_value.getText().toString();
            int number = 1 + (TextUtils.isEmpty(val) ? 0 : Integer.parseInt(val));
            productViewHolder.tv_value.setText("" + number);
            menuItemsList.get(position).setChildInput(number);
            if (number <= 0) {
                storeMenuActivity.getOrders().remove(jsonStoreProduct.getProductId());
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
            } else {
                storeMenuActivity.getOrders().put(jsonStoreProduct.getProductId(), menuItemsList.get(position));
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
            }
            notifyDataSetChanged();
        });
        productViewHolder.btn_decrease.setOnClickListener((View v) -> {
            String val = productViewHolder.tv_value.getText().toString();
            int number = (TextUtils.isEmpty(val) ? 0 : (val.equals("0") ? 0 : Integer.parseInt(val) - 1));
            productViewHolder.tv_value.setText("" + number);
            menuItemsList.get(position).setChildInput(number);
            if (number <= 0) {
                storeMenuActivity.getOrders().remove(jsonStoreProduct.getProductId());
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
            } else {
                storeMenuActivity.getOrders().put(jsonStoreProduct.getProductId(), menuItemsList.get(position));
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
            }
            notifyDataSetChanged();
        });
    }

    public final class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_product_details;
        private TextView tv_price;
        private TextView tv_value;
        private TextView tv_discounted_price;
        private TextView tv_cat;
        private ImageView iv_product_image;
        private Button btn_decrease;
        private Button btn_increase;

        public ProductViewHolder(View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_product_details = itemView.findViewById(R.id.tv_product_details);
            this.tv_price = itemView.findViewById(R.id.tv_price);
            this.tv_value = itemView.findViewById(R.id.tv_value);
            this.tv_discounted_price = itemView.findViewById(R.id.tv_discounted_price);
            this.tv_cat = itemView.findViewById(R.id.tv_cat);
            this.iv_product_image = itemView.findViewById(R.id.iv_product_image);
            this.btn_decrease = itemView.findViewById(R.id.btn_decrease);
            this.btn_increase = itemView.findViewById(R.id.btn_increase);
        }
    }
}
