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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import static com.noqapp.android.client.BuildConfig.MISSING_PRODUCT_IMAGE;

public class StoreProductMenuAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<JsonStoreCategory> listDataHeader;
    private HashMap<String, List<StoreCartItem>> listDataChild;
    private CartOrderUpdate cartOrderUpdate;
    private String currencySymbol;
    private HashMap<String, StoreCartItem> orders = new HashMap<>();
    private boolean isStoreOpen;
    private BusinessTypeEnum businessType;
    private String bizStoreId;

    public StoreProductMenuAdapter(
        Context context,
        List<JsonStoreCategory> listDataHeader,
        HashMap<String, List<StoreCartItem>> listDataChild,
        CartOrderUpdate cartOrderUpdate,
        String currencySymbol,
        boolean isStoreOpen,
        BusinessTypeEnum businessType,
        String bizStoreId) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        this.cartOrderUpdate = cartOrderUpdate;
        this.currencySymbol = currencySymbol;
        this.isStoreOpen = isStoreOpen;
        this.businessType = businessType;
        this.bizStoreId = bizStoreId;
        orders.clear();
    }

    public HashMap<String, StoreCartItem> getOrders() {
        return orders;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild
            .get(this.listDataHeader.get(groupPosition).getCategoryId())
            .get(childPosition);
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
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_menu_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tv_title = convertView.findViewById(R.id.tv_title);
            childViewHolder.tv_product_details = convertView.findViewById(R.id.tv_product_details);
            childViewHolder.tv_price = convertView.findViewById(R.id.tv_price);
            childViewHolder.tv_value = convertView.findViewById(R.id.tv_value);
            childViewHolder.tv_discounted_price = convertView.findViewById(R.id.tv_discounted_price);
            childViewHolder.tv_product_unit = convertView.findViewById(R.id.tv_product_unit);
            childViewHolder.tv_cat = convertView.findViewById(R.id.tv_cat);
            childViewHolder.tv_sold_out = convertView.findViewById(R.id.tv_sold_out);
            childViewHolder.ll_btns = convertView.findViewById(R.id.ll_btns);
            childViewHolder.tv_temp = convertView.findViewById(R.id.tv_temp);
            childViewHolder.iv_product_image = convertView.findViewById(R.id.iv_product_image);
            childViewHolder.btn_decrease = convertView.findViewById(R.id.btn_decrease);
            childViewHolder.btn_increase = convertView.findViewById(R.id.btn_increase);
            childViewHolder.view_disable = convertView.findViewById(R.id.view_disable);
            convertView.setTag(R.layout.list_item_menu_child, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag(R.layout.list_item_menu_child);
        }
        JsonStoreProduct jsonStoreProduct = storeCartItem.getJsonStoreProduct();
        childViewHolder.tv_title.setText(jsonStoreProduct.getProductName());
        childViewHolder.tv_product_details.setText(jsonStoreProduct.getProductInfo());
        childViewHolder.tv_value.setText(String.valueOf(storeCartItem.getChildInput()));
        childViewHolder.tv_price.setText(currencySymbol + jsonStoreProduct.getDisplayPrice());
        childViewHolder.tv_product_unit.setText(AppUtils.getProductWithUnits(jsonStoreProduct));
        childViewHolder.tv_discounted_price.setText(currencySymbol + storeCartItem.getFinalDiscountedPrice());
        if (!AppUtils.isRelease()) {
            childViewHolder.tv_temp.setText("Inventory " + jsonStoreProduct.getInventoryCurrent() + " out of " + jsonStoreProduct.getInventoryLimit());
            childViewHolder.tv_temp.setVisibility(View.VISIBLE);
        }
        Picasso.get()
            .load(StringUtils.isNotBlank(jsonStoreProduct.getProductImage())
                ? BuildConfig.AWSS3 + BuildConfig.PRODUCT_BUCKET + bizStoreId + File.separator + jsonStoreProduct.getProductImage()
                : MISSING_PRODUCT_IMAGE)
            .placeholder(ImageUtils.getThumbPlaceholder(context))
            .error(ImageUtils.getThumbErrorPlaceholder(context))
            .into(childViewHolder.iv_product_image);
        childViewHolder.iv_product_image.setOnClickListener(v -> showProductImageDialog(jsonStoreProduct, storeCartItem, childViewHolder.btn_increase, childViewHolder.btn_decrease, childViewHolder.tv_value));
        if (jsonStoreProduct.getProductDiscount() > 0) {
            childViewHolder.tv_price.setPaintFlags(childViewHolder.tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            childViewHolder.tv_discounted_price.setVisibility(View.VISIBLE);
        } else {
            childViewHolder.tv_price.setPaintFlags(childViewHolder.tv_price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            childViewHolder.tv_discounted_price.setVisibility(View.GONE);
        }
        if (isStoreOpen) {
            switch (businessType) {
                case RS:
                case FT:
                    if (jsonStoreProduct.getInventoryCurrent() > 0 || BuildConfig.INVENTORY_STATE.equalsIgnoreCase("OFF")) {
                        childViewHolder.view_disable.setVisibility(View.GONE);
                        childViewHolder.ll_btns.setVisibility(View.VISIBLE);
                        childViewHolder.tv_sold_out.setVisibility(View.GONE);
                    } else {
                        childViewHolder.view_disable.setVisibility(View.VISIBLE);
                        childViewHolder.ll_btns.setVisibility(View.GONE);
                        childViewHolder.tv_sold_out.setVisibility(View.VISIBLE);
                    }
                    break;
                default: {
                    childViewHolder.view_disable.setVisibility(View.GONE);
                    childViewHolder.ll_btns.setVisibility(View.VISIBLE);
                    childViewHolder.tv_sold_out.setVisibility(View.GONE);
                }
            }
        } else {
            childViewHolder.view_disable.setVisibility(View.VISIBLE);
            childViewHolder.ll_btns.setVisibility(View.GONE);
            childViewHolder.tv_sold_out.setVisibility(View.GONE);
        }

        switch (jsonStoreProduct.getProductType()) {
            case NV:
                childViewHolder.tv_cat.setBackgroundResource(R.drawable.round_corner_nonveg);
                break;
            case VE:
                childViewHolder.tv_cat.setBackgroundResource(R.drawable.round_corner_veg);
                break;
            default:
                childViewHolder.tv_cat.setBackgroundResource(R.drawable.round_corner_none);
        }

        childViewHolder.btn_increase.setOnClickListener((View v) -> {
            String val = childViewHolder.tv_value.getText().toString();
            int number = 1 + (TextUtils.isEmpty(val) ? 0 : Integer.parseInt(val));
            childViewHolder.tv_value.setText(String.valueOf(number));
            listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                .get(childPosition).setChildInput(number);
            if (number <= 0) {
                orders.remove(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                    .get(childPosition).getJsonStoreProduct().getProductId());
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
            } else {
                orders.put(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                    .get(childPosition).getJsonStoreProduct().getProductId(), listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                    .get(childPosition));
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
            }
            notifyDataSetChanged();
        });
        childViewHolder.btn_decrease.setOnClickListener((View v) -> {
            String val = childViewHolder.tv_value.getText().toString();
            int number = (TextUtils.isEmpty(val) ? 0 : (val.equals("0") ? 0 : Integer.parseInt(val) - 1));
            childViewHolder.tv_value.setText(String.valueOf(number));
            listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                .get(childPosition).setChildInput(number);
            if (number <= 0) {
                orders.remove(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                    .get(childPosition).getJsonStoreProduct().getProductId());
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
            } else {
                orders.put(listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                    .get(childPosition).getJsonStoreProduct().getProductId(), listDataChild.get(listDataHeader.get(groupPosition).getCategoryId())
                    .get(childPosition));
                cartOrderUpdate.updateCartOrderInfo(showCartAmount());
            }
            notifyDataSetChanged();
        });

        return convertView;
    }

    private void showProductImageDialog(JsonStoreProduct jsonStoreProduct, StoreCartItem storeCartItem,
                                        Button list_btn_increase, Button list_btn_decrease, TextView list_tv_value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.FullScreenDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        View view = inflater.inflate(R.layout.dialog_show_product, null, false);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_product_details = view.findViewById(R.id.tv_product_details);
        TextView tv_price = view.findViewById(R.id.tv_price);
        TextView tv_value = view.findViewById(R.id.tv_value);
        TextView tv_discounted_price = view.findViewById(R.id.tv_discounted_price);
        TextView tv_product_unit = view.findViewById(R.id.tv_product_unit);
        TextView tv_cat = view.findViewById(R.id.tv_cat);
        TextView tv_sold_out = view.findViewById(R.id.tv_sold_out);
        LinearLayout ll_btns = view.findViewById(R.id.ll_btns);
        TextView tv_temp = view.findViewById(R.id.tv_temp);
        ImageView iv_product_image = view.findViewById(R.id.iv_product_image);
        Button btn_decrease = view.findViewById(R.id.btn_decrease);
        Button btn_increase = view.findViewById(R.id.btn_increase);
        Button btn_place_order = view.findViewById(R.id.btn_place_order);
        btn_place_order.setText("Done");
        btn_place_order.setVisibility(View.VISIBLE);
        tv_title.setText(jsonStoreProduct.getProductName());
        tv_product_details.setText(jsonStoreProduct.getProductInfo());
        tv_value.setText(String.valueOf(storeCartItem.getChildInput()));
        tv_price.setText(currencySymbol + jsonStoreProduct.getDisplayPrice());
        tv_product_unit.setText(AppUtils.getProductWithUnits(jsonStoreProduct));
        tv_discounted_price.setText(currencySymbol + storeCartItem.getFinalDiscountedPrice());
        if (!AppUtils.isRelease()) {
            tv_temp.setText("Inventory " + jsonStoreProduct.getInventoryCurrent() + " out of " + jsonStoreProduct.getInventoryLimit());
            tv_temp.setVisibility(View.VISIBLE);
        }
        Picasso.get()
            .load(StringUtils.isNotBlank(jsonStoreProduct.getProductImage())
                ? BuildConfig.AWSS3 + BuildConfig.PRODUCT_BUCKET + bizStoreId + File.separator + jsonStoreProduct.getProductImage()
                : MISSING_PRODUCT_IMAGE)
            .placeholder(ImageUtils.getThumbPlaceholder(context))
            .error(ImageUtils.getThumbErrorPlaceholder(context))
            .into(iv_product_image);

        if (jsonStoreProduct.getProductDiscount() > 0) {
            tv_price.setPaintFlags(tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv_discounted_price.setVisibility(View.VISIBLE);
        } else {
            tv_price.setPaintFlags(tv_price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tv_discounted_price.setVisibility(View.GONE);
        }
        if (isStoreOpen) {
            switch (businessType) {
                case RS:
                case FT:
                    if (jsonStoreProduct.getInventoryCurrent() > 0 || BuildConfig.INVENTORY_STATE.equalsIgnoreCase("OFF")) {
                        ll_btns.setVisibility(View.VISIBLE);
                        tv_sold_out.setVisibility(View.GONE);
                    } else {
                        ll_btns.setVisibility(View.GONE);
                        tv_sold_out.setVisibility(View.VISIBLE);
                    }
                    break;
                default: {
                    ll_btns.setVisibility(View.VISIBLE);
                    tv_sold_out.setVisibility(View.GONE);
                }
            }
        } else {
            ll_btns.setVisibility(View.GONE);
            tv_sold_out.setVisibility(View.GONE);
        }

        switch (jsonStoreProduct.getProductType()) {
            case NV:
                tv_cat.setBackgroundResource(R.drawable.round_corner_nonveg);
                break;
            case VE:
                tv_cat.setBackgroundResource(R.drawable.round_corner_veg);
                break;
            default:
                tv_cat.setBackgroundResource(R.drawable.round_corner_none);
        }

        btn_increase.setOnClickListener((View v) -> {
            String val = tv_value.getText().toString();
            int number = 1 + (TextUtils.isEmpty(val) ? 0 : Integer.parseInt(val));
            tv_value.setText(String.valueOf(number));
            int list_number = (TextUtils.isEmpty(val) ? 0 : Integer.parseInt(val));
            list_tv_value.setText(String.valueOf(list_number));
            list_btn_increase.performClick();
            BigDecimal amountString = showCartAmount();
            if (amountString.compareTo(new BigDecimal(0)) > 0) {
                // btn_place_order.setVisibility(View.VISIBLE);
                btn_place_order.setText("Your cart amount is: " + currencySymbol + amountString.toString());
            } else {
                // btn_place_order.setVisibility(View.GONE);
                btn_place_order.setText("Done");
            }
        });
        btn_decrease.setOnClickListener((View v) -> {
            String val = tv_value.getText().toString();
            int number = (TextUtils.isEmpty(val) ? 0 : (val.equals("0") ? 0 : Integer.parseInt(val) - 1));
            tv_value.setText(String.valueOf(number));
            int list_number = (TextUtils.isEmpty(val) ? 0 : (val.equals("0") ? 0 : Integer.parseInt(val)));
            list_tv_value.setText(String.valueOf(list_number));
            list_btn_decrease.performClick();
            BigDecimal amountString = showCartAmount();
            if (amountString.compareTo(new BigDecimal(0)) > 0) {
                // btn_place_order.setVisibility(View.VISIBLE);
                btn_place_order.setText("Your cart amount is: " + currencySymbol + amountString.toString());
            } else {
                // btn_place_order.setVisibility(View.GONE);
                btn_place_order.setText("Done");
            }
            notifyDataSetChanged();
        });
        builder.setView(view);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        btn_place_order.setOnClickListener(v -> mAlertDialog.dismiss());
        mAlertDialog.show();
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = ((JsonStoreCategory) getGroup(groupPosition)).getCategoryName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_menu_group, parent, false);
        }
        // ExpandableListView mExpandableListView = (ExpandableListView) parent;
        // mExpandableListView.expandGroup(groupPosition);
        TextView tv_list_header = convertView.findViewById(R.id.tv_list_header);
        tv_list_header.setTypeface(null, Typeface.BOLD);
        tv_list_header.setText(headerTitle);
        //ImageView ivGroupIndicator = convertView.findViewById(R.id.ivGroupIndicator);
        // ivGroupIndicator.setSelected(isExpanded);
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

    private BigDecimal showCartAmount() {
        BigDecimal price = new BigDecimal(0);
        for (StoreCartItem value : getOrders().values()) {
            price = price.add(new BigDecimal(value.getChildInput()).multiply(value.getFinalDiscountedPrice()));
        }
        return price;
    }

    public interface CartOrderUpdate {
        void updateCartOrderInfo(BigDecimal amountString);
    }

    public final class ChildViewHolder {
        private TextView tv_title;
        private TextView tv_product_details;
        private TextView tv_price;
        private TextView tv_value;
        private TextView tv_discounted_price;
        private TextView tv_product_unit;
        private TextView tv_cat;
        private ImageView iv_product_image;
        private TextView tv_sold_out;
        private TextView tv_temp;
        private LinearLayout ll_btns;
        private View view_disable;
        private Button btn_decrease;
        private Button btn_increase;
    }
}