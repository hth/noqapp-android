package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.ProductUtils;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.OrderDetailActivity;

import java.math.BigDecimal;
import java.util.List;

public class OrderItemAdapter extends BaseAdapter {
    private Context context;
    private List<JsonPurchaseOrderProduct> jsonPurchaseOrderProductList;
    private String currencySymbol;
    private OrderDetailActivity orderDetailActivity;
    private boolean isClickEnable = true;
    private boolean isInQ = false;

    public void setClickEnable(boolean clickEnable) {
        isClickEnable = clickEnable;
    }

    public OrderItemAdapter(
        Context context,
        List<JsonPurchaseOrderProduct> jsonPurchaseOrderProducts,
        String currencySymbol,
        OrderDetailActivity orderDetailActivity
    ) {
        this.context = context;
        this.jsonPurchaseOrderProductList = jsonPurchaseOrderProducts;
        this.currencySymbol = currencySymbol;
        this.orderDetailActivity = orderDetailActivity;
    }

    public OrderItemAdapter(
        Context context,
        List<JsonPurchaseOrderProduct> jsonPurchaseOrderProducts,
        String currencySymbol,
        boolean isInQ
    ) {
        this.context = context;
        this.jsonPurchaseOrderProductList = jsonPurchaseOrderProducts;
        this.currencySymbol = currencySymbol;
        this.isInQ = isInQ;
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
            recordHolder.tv_total_price = view.findViewById(R.id.tv_total_price);
            recordHolder.tv_price = view.findViewById(R.id.tv_price);
            recordHolder.tv_discounted_price = view.findViewById(R.id.tv_discounted_price);
            recordHolder.tv_product_quantity = view.findViewById(R.id.tv_product_quantity);
            recordHolder.ll_mid = view.findViewById(R.id.ll_mid);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        JsonPurchaseOrderProduct jpop = jsonPurchaseOrderProductList.get(position);
        if (isInQ) {
            recordHolder.tv_title.setText(jpop.getProductName());
            recordHolder.tv_total_price.setText(String.valueOf(jpop.getProductQuantity()));
            recordHolder.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            recordHolder.tv_total_price.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            recordHolder.ll_mid.setVisibility(View.GONE);
        } else {
            recordHolder.ll_mid.setVisibility(View.VISIBLE);
            recordHolder.tv_title.setText(jpop.getProductName());
            recordHolder.tv_total_price.setText(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jpop.getProductPrice()).multiply(new BigDecimal(jpop.getProductQuantity())).toString()));
            recordHolder.tv_price.setText(currencySymbol + jpop.getDisplayPrice());
            recordHolder.tv_product_quantity.setText(" x " + jpop.getProductQuantity());
            recordHolder.tv_discounted_price.setText(currencySymbol + ProductUtils.calculateDiscountPrice(jpop.getDisplayPrice(), jpop.getDisplayDiscount()));
            if (jpop.getProductDiscount() > 0) {
                recordHolder.tv_price.setPaintFlags(recordHolder.tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                recordHolder.tv_discounted_price.setVisibility(View.VISIBLE);
                recordHolder.tv_total_price.setText(currencySymbol + ProductUtils.calculateDiscountPrice(jpop.getDisplayPrice(), jpop.getDisplayDiscount()));
            } else {
                recordHolder.tv_price.setPaintFlags(recordHolder.tv_price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                recordHolder.tv_discounted_price.setVisibility(View.GONE);
                recordHolder.tv_total_price.setText(currencySymbol + jpop.getDisplayPrice());
            }
            if (0 == jpop.getProductPrice()) {
                recordHolder.tv_total_price.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit, 0);
            } else {
                recordHolder.tv_total_price.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            recordHolder.tv_total_price.setOnClickListener(v -> {
                if (jpop.getProductPrice() == 0 && isClickEnable) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    builder.setTitle(null);
                    View prideDialog = inflater.inflate(R.layout.dialog_modify_price, null, false);
                    final EditText edt_prod_price = prideDialog.findViewById(R.id.edt_prod_price);
                    builder.setView(prideDialog);
                    final AlertDialog mAlertDialog = builder.create();
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    Button btn_update = prideDialog.findViewById(R.id.btn_update);
                    Button btn_cancel = prideDialog.findViewById(R.id.btn_cancel);
                    btn_cancel.setOnClickListener(v1 -> mAlertDialog.dismiss());
                    btn_update.setOnClickListener(v12 -> {
                        edt_prod_price.setError(null);
                        if (edt_prod_price.getText().toString().equals("")) {
                            edt_prod_price.setError(context.getString(R.string.empty_price));
                        } else {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edt_prod_price.getWindowToken(), 0);
                            jpop.setProductPrice(Integer.parseInt(edt_prod_price.getText().toString()) * 100);
                            notifyDataSetChanged();
                            orderDetailActivity.updateProductPriceList(jpop, position);
                            mAlertDialog.dismiss();
                        }
                    });
                    mAlertDialog.show();
                }
            });
        }
        return view;
    }

    static class RecordHolder {
        TextView tv_title;
        TextView tv_total_price;
        TextView tv_price;
        TextView tv_discounted_price;
        TextView tv_product_quantity;
        LinearLayout ll_mid;
        RecordHolder() {
        }
    }
}

