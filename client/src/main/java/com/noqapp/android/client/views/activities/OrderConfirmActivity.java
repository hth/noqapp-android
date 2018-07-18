package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrder;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProduct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderConfirmActivity extends BaseActivity {

    @BindView(R.id.tv_total_order_amt)
    protected TextView tv_total_order_amt;
    @BindView(R.id.tv_tax_amt)
    protected TextView tv_tax_amt;
    @BindView(R.id.tv_due_amt)
    protected TextView tv_due_amt;
    @BindView(R.id.ll_order_details)
    protected LinearLayout ll_order_details;
    @BindView(R.id.tv_serving_no)
    protected TextView tv_serving_no;
    @BindView(R.id.tv_token)
    protected TextView tv_token;
    @BindView(R.id.tv_estimated_time)
    protected TextView tv_estimated_time;
    @BindView(R.id.tv_store_name)
    protected TextView tv_store_name;
    @BindView(R.id.tv_address)
    protected TextView tv_address;
    private JsonPurchaseOrder jsonPurchaseOrder, oldjsonPurchaseOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        ButterKnife.bind(this);
        initActionsViews(false);

        tv_toolbar_title.setText(getString(R.string.screen_order_confirm));
        jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("data");
        oldjsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("oldData");

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
            tv_title.setText(jsonPurchaseOrderProduct.getJsonStoreProduct().getProductName());
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
