package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.order.JsonPurchaseOrder;
import com.noqapp.android.common.beans.order.JsonPurchaseOrderProduct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderConfirmActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        TextView tv_total_order_amt = findViewById(R.id.tv_total_order_amt);
        TextView tv_tax_amt = findViewById(R.id.tv_tax_amt);
        TextView tv_due_amt = findViewById(R.id.tv_due_amt);
        LinearLayout ll_order_details = findViewById(R.id.ll_order_details);
        TextView tv_serving_no = findViewById(R.id.tv_serving_no);
        TextView tv_token = findViewById(R.id.tv_token);
        TextView tv_estimated_time = findViewById(R.id.tv_estimated_time);
        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_address = findViewById(R.id.tv_address);
        initActionsViews(true);

        tv_toolbar_title.setText(getString(R.string.screen_order_confirm));
        JsonPurchaseOrder jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("data");
        JsonPurchaseOrder oldjsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("oldData");

        tv_tax_amt.setText(getString(R.string.rupee) + "" + "0.0");
        tv_due_amt.setText(getString(R.string.rupee) + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        tv_total_order_amt.setText(getString(R.string.rupee) + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        for (int i = 0; i < oldjsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = oldjsonPurchaseOrder.getPurchaseOrderProducts().get(i);
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
        tv_serving_no.setText(String.valueOf(jsonPurchaseOrder.getServingNumber()));
        tv_token.setText(String.valueOf(jsonPurchaseOrder.getToken()));
        tv_estimated_time.setText("30 Min *");

        tv_store_name.setText(getIntent().getExtras().getString("storeName"));
        tv_address.setText(getIntent().getExtras().getString("storeAddress"));
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_home.performClick();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        iv_home.performClick();
    }
}
