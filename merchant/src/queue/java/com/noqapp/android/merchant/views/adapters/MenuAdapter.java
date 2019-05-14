package com.noqapp.android.merchant.views.adapters;


import com.noqapp.android.common.beans.ChildData;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private List<ChildData> menuItemsList;
    private MenuItemUpdate menuItemUpdate;

    public MenuAdapter(Context context, List<ChildData> menuItemsList, MenuItemUpdate menuItemUpdate) {
        this.context = context;
        this.menuItemsList = menuItemsList;
        this.menuItemUpdate = menuItemUpdate;
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
            childViewHolder.tv_price = convertView.findViewById(R.id.tv_price);
            childViewHolder.tv_discounted_price = convertView.findViewById(R.id.tv_discounted_price);
            childViewHolder.tv_cat = convertView.findViewById(R.id.tv_cat);
            childViewHolder.iv_delete = convertView.findViewById(R.id.iv_delete);
            childViewHolder.iv_edit = convertView.findViewById(R.id.iv_edit);
            childViewHolder.rl_menu_child = convertView.findViewById(R.id.rl_menu_child);
            convertView.setTag(R.layout.list_item_menu_child, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.list_item_menu_child);
        }
        final JsonStoreProduct jsonStoreProduct = childData.getJsonStoreProduct();
        childViewHolder.tv_child_title.setText(jsonStoreProduct.getProductName());
        childViewHolder.tv_child_title_details.setText(jsonStoreProduct.getProductInfo());
        //  childViewHolder.tv_value.setText(String.valueOf(childData.getChildInput()));
        String currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        childViewHolder.tv_price.setText(currencySymbol + " " + AppUtils.getPriceWithUnits(jsonStoreProduct));
        childViewHolder.tv_discounted_price.setText(
                currencySymbol
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
        childViewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCustomDialog showDialog = new ShowCustomDialog(context);
                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                    @Override
                    public void btnPositiveClick() {
                        Toast.makeText(context, "Deleted from Menu Item List", Toast.LENGTH_LONG).show();
                        menuItemUpdate.menuItemUpdate(jsonStoreProduct, ActionTypeEnum.REMOVE);
                    }
                    @Override
                    public void btnNegativeClick() {
                        //Do nothing
                    }
                });
                showDialog.displayDialog("Delete Menu Item", "Do you want to delete it from Menu Item List?");
            }
        });
        childViewHolder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuItemUpdate.addOrEditProduct(jsonStoreProduct, ActionTypeEnum.EDIT);
            }
        });

        if (jsonStoreProduct.isActive()) {
            childViewHolder.rl_menu_child.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            childViewHolder.rl_menu_child.setBackgroundColor(ContextCompat.getColor(context, R.color.disable_list));
        }
        return convertView;
    }

    private double calculateDiscountPrice(String displayPrice, String discountAmount) {
        double price = Double.valueOf(displayPrice);
        double discountAmountValue = Double.valueOf(discountAmount);
        return (price - discountAmountValue);
    }

    public final class ChildViewHolder {
        TextView tv_child_title;
        TextView tv_child_title_details;
        TextView tv_price;
        TextView tv_value;
        TextView tv_discounted_price;
        TextView tv_cat;
        ImageView iv_delete;
        ImageView iv_edit;
        RelativeLayout rl_menu_child;
    }

    public interface MenuItemUpdate {

        void menuItemUpdate(JsonStoreProduct jsonStoreProduct, ActionTypeEnum actionTypeEnum);

        void addOrEditProduct(JsonStoreProduct jsonStoreProduct, ActionTypeEnum actionTypeEnum);

    }

}
