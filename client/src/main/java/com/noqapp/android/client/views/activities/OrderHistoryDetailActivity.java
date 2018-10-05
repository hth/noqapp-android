package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.order.JsonPurchaseOrder;
import com.noqapp.android.common.beans.order.JsonPurchaseOrderProduct;
import com.noqapp.android.common.utils.CommonHelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderHistoryDetailActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_detail);
        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.screen_order_detail));


        TextView tv_total_order_amt = findViewById(R.id.tv_total_order_amt);
        TextView tv_tax_amt = findViewById(R.id.tv_tax_amt);
        TextView tv_due_amt = findViewById(R.id.tv_due_amt);
        LinearLayout ll_order_details = findViewById(R.id.ll_order_details);
        TextView tv_token = findViewById(R.id.tv_token);
        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_address = findViewById(R.id.tv_address);
        TextView tv_order_mode = findViewById(R.id.tv_order_mode);
        TextView tv_delivery_address = findViewById(R.id.tv_delivery_address);
        TextView tv_order_date = findViewById(R.id.tv_order_date);
        TextView tv_order_status = findViewById(R.id.tv_order_status);

        JsonPurchaseOrder jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("data");

        tv_tax_amt.setText(getString(R.string.rupee) + "" + "0.0");
        tv_due_amt.setText(getString(R.string.rupee) + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        tv_total_order_amt.setText(getString(R.string.rupee) + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        for (int i = 0; i < jsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = jsonPurchaseOrder.getPurchaseOrderProducts().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = (TextView) inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_qty = (TextView) inflatedLayout.findViewById(R.id.tv_qty);
            TextView tv_price = (TextView) inflatedLayout.findViewById(R.id.tv_price);
            TextView tv_total_price = (TextView) inflatedLayout.findViewById(R.id.tv_total_price);
            tv_title.setText(jsonPurchaseOrderProduct.getProductName());
            tv_qty.setText("Quantity: " + jsonPurchaseOrderProduct.getProductQuantity());
            tv_price.setText(getString(R.string.rupee) + "" + jsonPurchaseOrderProduct.getProductPrice() / 100);
            tv_total_price.setText(getString(R.string.rupee) + "" + jsonPurchaseOrderProduct.getProductPrice() * jsonPurchaseOrderProduct.getProductQuantity() / 100);
            ll_order_details.addView(inflatedLayout);
        }
        tv_token.setText(String.valueOf("Your order no: "+jsonPurchaseOrder.getToken()));
        tv_store_name.setText("Store Name: not available");
        tv_address.setText("Store Address: not available");
        tv_order_mode.setText("Mode of payment: "+jsonPurchaseOrder.getPaymentType().getDescription());
        tv_delivery_address.setText("Deliver Adddress: "+jsonPurchaseOrder.getDeliveryAddress());
        tv_order_status.setText("Status: "+jsonPurchaseOrder.getPurchaseOrderState().getDescription());
        try {
            tv_order_date.setText("Order timing: " + CommonHelper.SDF_YYYY_MM_DD.format(new SimpleDateFormat(Constants.ISO8601_FMT, Locale.getDefault()).parse(jsonPurchaseOrder.getCreated())));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
