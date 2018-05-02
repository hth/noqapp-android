package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.ChildData;
import com.noqapp.android.client.presenter.beans.JsonStoreProduct;

import java.util.List;

public class MenuAdapter extends BaseAdapter {
    private static final String TAG = MenuAdapter.class.getSimpleName();
    private Context context;
    private List<ChildData> notificationsList;

    public MenuAdapter(Context context, List<ChildData> notificationsList) {
        this.context = context;
        this.notificationsList = notificationsList;
    }

    public int getCount() {
        return this.notificationsList.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ChildViewHolder childViewHolder;
        final ChildData childData = notificationsList.get(position);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_filter, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tv_child_title = (TextView) convertView
                    .findViewById(R.id.tv_child_title);
            childViewHolder.tv_value = (TextView) convertView
                    .findViewById(R.id.tv_value);
            childViewHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            childViewHolder.tv_discounted_price = (TextView) convertView.findViewById(R.id.tv_discounted_price);
            childViewHolder.btn_increase = (Button) convertView.findViewById(R.id.btn_increase);
            childViewHolder.btn_decrease = (Button) convertView.findViewById(R.id.btn_decrease);
            childViewHolder.tv_cat = (TextView) convertView.findViewById(R.id.tv_cat);
            convertView.setTag(R.layout.list_item_filter, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.list_item_filter);
        }
        JsonStoreProduct jsonStoreProduct = childData.getJsonStoreProduct();
        childViewHolder.tv_child_title.setText(jsonStoreProduct.getProductName());
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
                childViewHolder.tv_cat.setBackgroundResource(R.drawable.round_corner_bank);
        }
        childViewHolder.btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = childViewHolder.tv_value.getText().toString();
                int number = 1 + (TextUtils.isEmpty(val) ? 0 : Integer.parseInt(val));
                childViewHolder.tv_value.setText("" + number);
                notificationsList
                        .get(position).setChildInput(number);
//                if (number <= 0) {
//                    orders.remove(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
//                            .get(childPosition).getJsonStoreProduct().getProductId());
//                    cartUpdate.updateCartInfo(showCartAmount());
//                } else {
//                    orders.put(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
//                            .get(childPosition).getJsonStoreProduct().getProductId(), listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
//                            .get(childPosition));
//                    cartUpdate.updateCartInfo(showCartAmount());
//                }
                notifyDataSetChanged();
            }
        });
        childViewHolder.btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = childViewHolder.tv_value.getText().toString();
                int number = (TextUtils.isEmpty(val) ? 0 : (val.equals("0") ? 0 : Integer.parseInt(val) - 1));
                childViewHolder.tv_value.setText("" + number);
                notificationsList
                        .get(position).setChildInput(number);
//                if (number <= 0) {
//                    orders.remove(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
//                            .get(childPosition).getJsonStoreProduct().getProductId());
//                    cartUpdate.updateCartInfo(showCartAmount());
//                } else {
//                    orders.put(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
//                            .get(childPosition).getJsonStoreProduct().getProductId(), listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
//                            .get(childPosition));
//                    cartUpdate.updateCartInfo(showCartAmount());
//                }
                notifyDataSetChanged();
            }
        });

        return convertView;
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

    private double calculateDiscountPrice(String displayPrice, String discountPercentage) {
        double price = Double.valueOf(displayPrice);
        double discountPercentageValue = Double.valueOf(discountPercentage);
        return price - (price * discountPercentageValue) / 100;
    }

//    private int showCartAmount() {
//        int price = 0;
//        for (ChildData value : getOrders().values()) {
//            price += value.getChildInput() * value.getJsonStoreProduct().getProductPrice();
//        }
//        return price / 100;
//    }
}
