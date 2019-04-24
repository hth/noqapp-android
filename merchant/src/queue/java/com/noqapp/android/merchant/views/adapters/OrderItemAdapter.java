package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.OrderDetailActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import java.math.BigDecimal;
import java.util.List;

public class OrderItemAdapter extends BaseAdapter {
    private Context context;
    private List<JsonPurchaseOrderProduct> jsonPurchaseOrderProductList;
    private String currencySymbol;
    private OrderDetailActivity orderDetailActivity;
    private boolean isClickEnable = true;

    public void setClickEnable(boolean clickEnable) {
        isClickEnable = clickEnable;
    }

    public OrderItemAdapter(Context context, List<JsonPurchaseOrderProduct> notificationsList, String currencySymbol, OrderDetailActivity orderDetailActivity) {
        this.context = context;
        this.jsonPurchaseOrderProductList = notificationsList;
        this.currencySymbol = currencySymbol;
        this.orderDetailActivity = orderDetailActivity;
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
            recordHolder.tv_amount = view.findViewById(R.id.tv_amount);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        recordHolder.tv_title.setText(jsonPurchaseOrderProductList.get(position).getProductName() + " x " + jsonPurchaseOrderProductList.get(position).getProductQuantity());
        recordHolder.tv_amount.setText(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jsonPurchaseOrderProductList.get(position).getProductPrice()).multiply(new BigDecimal(jsonPurchaseOrderProductList.get(position).getProductQuantity())).toString()));
        if (jsonPurchaseOrderProductList.get(position).getProductPrice() == 0) {
            recordHolder.tv_amount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit, 0);
        } else {
            recordHolder.tv_amount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        recordHolder.tv_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jsonPurchaseOrderProductList.get(position).getProductPrice() == 0 && isClickEnable) {
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
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                        }
                    });
                    btn_update.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            edt_prod_price.setError(null);
                            if (edt_prod_price.getText().toString().equals("")) {
                                edt_prod_price.setError(context.getString(R.string.empty_price));
                            } else {
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(edt_prod_price.getWindowToken(), 0);
                                jsonPurchaseOrderProductList.get(position).setProductPrice(Integer.parseInt(edt_prod_price.getText().toString())*100);
                                //recordHolder.tv_amount.setText(currencySymbol + " "+String.valueOf(menuSelectData.get(pos).getPrice()));
                                //tv_cost.setText(currencySymbol + " "+ String.valueOf(calculateTotalPrice()));
                                notifyDataSetChanged();
                                orderDetailActivity.updateProductPriceList(jsonPurchaseOrderProductList.get(position),position);
                                mAlertDialog.dismiss();
                            }
                        }
                    });
                    mAlertDialog.show();
                }
            }
        });
        return view;
    }

    static class RecordHolder {
        TextView tv_title;
        TextView tv_amount;

        RecordHolder() {
        }
    }
}

