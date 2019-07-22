package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;

import java.util.HashMap;
import java.util.List;

/**
 * Created by chandra on 3/28/18.
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<JsonStoreCategory> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<StoreCartItem>> listDataChild;
    private CartUpdate cartUpdate;
    private String currencySymbol;
    private HashMap<String, StoreCartItem> orders = new HashMap<>();

    public CustomExpandableListAdapter(Context context, List<JsonStoreCategory> listDataHeader,
                                       HashMap<String, List<StoreCartItem>> listDataChild, CartUpdate cartUpdate, String currencySymbol) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        this.cartUpdate = cartUpdate;
        this.currencySymbol = currencySymbol;
        orders.clear();
    }

    public HashMap<String, StoreCartItem> getOrders() {
        return orders;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition).getCategoryId())
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(
            final int groupPosition,
            final int childPosition,
            boolean isLastChild,
            View convertView,
            ViewGroup parent
    ) {
        final ChildViewHolder childViewHolder;
        final StoreCartItem storeCartItem = (StoreCartItem) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_menu_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tv_child_title = convertView.findViewById(R.id.tv_child_title);
            childViewHolder.tv_value = convertView.findViewById(R.id.tv_value);
            childViewHolder.tv_price = convertView.findViewById(R.id.tv_price);
            childViewHolder.tv_discounted_price = convertView.findViewById(R.id.tv_discounted_price);
            childViewHolder.btn_increase = convertView.findViewById(R.id.btn_increase);
            childViewHolder.btn_decrease = convertView.findViewById(R.id.btn_decrease);
            childViewHolder.tv_cat = convertView.findViewById(R.id.tv_cat);
            convertView.setTag(R.layout.list_item_menu_child, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag(R.layout.list_item_menu_child);
        }
        JsonStoreProduct jsonStoreProduct = storeCartItem.getJsonStoreProduct();
        childViewHolder.tv_child_title.setText(jsonStoreProduct.getProductName());
        childViewHolder.tv_value.setText(String.valueOf(storeCartItem.getChildInput()));
        childViewHolder.tv_price.setText(currencySymbol + " " + jsonStoreProduct.getDisplayPrice());
        childViewHolder.tv_discounted_price.setText(
                currencySymbol
                        + " "
                        + +storeCartItem.getFinalDiscountedPrice());
        if (jsonStoreProduct.getProductDiscount() > 0) {
            childViewHolder.tv_price.setPaintFlags(childViewHolder.tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            childViewHolder.tv_discounted_price.setVisibility(View.VISIBLE);
        } else {
            childViewHolder.tv_price.setPaintFlags(childViewHolder.tv_price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            childViewHolder.tv_discounted_price.setVisibility(View.INVISIBLE);
        }
        switch (jsonStoreProduct.getProductType()) {
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
            listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                    .get(childPosition).setChildInput(number);
            if (number <= 0) {
                orders.remove(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                        .get(childPosition).getJsonStoreProduct().getProductId());
                cartUpdate.updateCartInfo(showCartAmount());
            } else {
                orders.put(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                        .get(childPosition).getJsonStoreProduct().getProductId(), listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                        .get(childPosition));
                cartUpdate.updateCartInfo(showCartAmount());
            }
            notifyDataSetChanged();
        });
        childViewHolder.btn_decrease.setOnClickListener((View v) -> {
            String val = childViewHolder.tv_value.getText().toString();
            int number = (TextUtils.isEmpty(val) ? 0 : (val.equals("0") ? 0 : Integer.parseInt(val) - 1));
            childViewHolder.tv_value.setText("" + number);
            listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                    .get(childPosition).setChildInput(number);
            if (number <= 0) {
                orders.remove(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                        .get(childPosition).getJsonStoreProduct().getProductId());
                cartUpdate.updateCartInfo(showCartAmount());
            } else {
                orders.put(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                        .get(childPosition).getJsonStoreProduct().getProductId(), listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                        .get(childPosition));
                cartUpdate.updateCartInfo(showCartAmount());
            }
            notifyDataSetChanged();
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition).getCategoryId()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(
            int groupPosition,
            boolean isExpanded,
            View convertView,
            ViewGroup parent
    ) {
        String headerTitle = ((JsonStoreCategory) getGroup(groupPosition)).getCategoryName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_menu_group, parent, false);
        }

        TextView tv_list_header = convertView.findViewById(R.id.tv_list_header);
        tv_list_header.setTypeface(null, Typeface.BOLD);
        tv_list_header.setText(headerTitle);
        ImageView ivGroupIndicator =  convertView.findViewById(R.id.ivGroupIndicator);
        ivGroupIndicator.setSelected(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private int showCartAmount() {
        int price = 0;
        for (StoreCartItem value : getOrders().values()) {
            price += value.getChildInput() * value.getFinalDiscountedPrice();
        }
        return price;
    }

    public interface CartUpdate {
        void updateCartInfo(int amountString);
    }

    public final class ChildViewHolder {
        TextView tv_child_title;
        TextView tv_price;
        TextView tv_value;
        TextView tv_discounted_price;
        TextView tv_cat;
        Button btn_decrease;
        Button btn_increase;
    }
}