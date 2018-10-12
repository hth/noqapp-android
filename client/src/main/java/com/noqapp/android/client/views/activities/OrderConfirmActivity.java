package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.PurchaseApiModel;
import com.noqapp.android.client.presenter.PurchaseOrderPresenter;
import com.noqapp.android.client.presenter.beans.body.OrderDetail;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.order.JsonPurchaseOrder;
import com.noqapp.android.common.beans.order.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OrderConfirmActivity extends BaseActivity implements PurchaseOrderPresenter {

    private PurchaseApiModel purchaseApiModel;
    private TextView tv_total_order_amt;
    private TextView tv_tax_amt;
    private TextView tv_due_amt;
    private LinearLayout ll_order_details;
    private TextView tv_serving_no;
    private TextView tv_token;
    private TextView tv_estimated_time;
    private JsonPurchaseOrder jsonPurchaseOrder, oldjsonPurchaseOrder;
    private Button btn_cancel_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        tv_total_order_amt = findViewById(R.id.tv_total_order_amt);
        tv_tax_amt = findViewById(R.id.tv_tax_amt);
        tv_due_amt = findViewById(R.id.tv_due_amt);
        ll_order_details = findViewById(R.id.ll_order_details);
        tv_serving_no = findViewById(R.id.tv_serving_no);
        tv_token = findViewById(R.id.tv_token);
        tv_estimated_time = findViewById(R.id.tv_estimated_time);
        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_address = findViewById(R.id.tv_address);
        btn_cancel_order = findViewById(R.id.btn_cancel_order);
        initActionsViews(true);
        purchaseApiModel = new PurchaseApiModel(this);
        tv_toolbar_title.setText(getString(R.string.screen_order_confirm));
        tv_store_name.setText(getIntent().getExtras().getString("storeName"));
        tv_address.setText(getIntent().getExtras().getString("storeAddress"));

        if (getIntent().getBooleanExtra(NoQueueBaseFragment.KEY_FROM_LIST, false)) {
            tv_toolbar_title.setText(getString(R.string.order_details));
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                progressDialog.setMessage("Order details fetching in progress..");
                String codeQR = getIntent().getExtras().getString(NoQueueBaseFragment.KEY_CODE_QR);
                int token = getIntent().getExtras().getInt("token");
                purchaseApiModel.orderDetail(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new OrderDetail().setCodeQR(codeQR).setToken(token));
            } else {
                ShowAlertInformation.showNetworkDialog(OrderConfirmActivity.this);
            }
        } else {
            jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("data");
            oldjsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("oldData");
            updateUI();
        }
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_home.performClick();
            }
        });
        btn_cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != jsonPurchaseOrder) {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        progressDialog.show();
                        progressDialog.setMessage("Order cancel in progress..");
                        purchaseApiModel.cancelOrder(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                    } else {
                        ShowAlertInformation.showNetworkDialog(OrderConfirmActivity.this);
                    }
                }
            }
        });
    }

    private void updateUI() {
        tv_tax_amt.setText(getString(R.string.rupee) + "" + "0.0");
        tv_due_amt.setText(getString(R.string.rupee) + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        tv_total_order_amt.setText(getString(R.string.rupee) + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        for (int i = 0; i < oldjsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = oldjsonPurchaseOrder.getPurchaseOrderProducts().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_qty = inflatedLayout.findViewById(R.id.tv_qty);
            TextView tv_price = inflatedLayout.findViewById(R.id.tv_price);
            TextView tv_total_price = inflatedLayout.findViewById(R.id.tv_total_price);
            tv_title.setText(jsonPurchaseOrderProduct.getProductName());
           // tv_qty.setText("Quantity: " + jsonPurchaseOrderProduct.getProductQuantity());
            tv_price.setText(getString(R.string.rupee) + "" + (jsonPurchaseOrderProduct.getProductPrice() / 100)+" x "+String.valueOf(jsonPurchaseOrderProduct.getProductQuantity()));
            tv_total_price.setText(getString(R.string.rupee) + "" + jsonPurchaseOrderProduct.getProductPrice() * jsonPurchaseOrderProduct.getProductQuantity() / 100);
            ll_order_details.addView(inflatedLayout);
        }
        tv_serving_no.setText(String.valueOf(jsonPurchaseOrder.getServingNumber()));
        tv_token.setText(String.valueOf(jsonPurchaseOrder.getToken()));
        tv_estimated_time.setText(getString(R.string.will_be_served, "30 Min *"));
        if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.CO) {
            btn_cancel_order.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        iv_home.performClick();
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if (null != jsonPurchaseOrder) {
            this.jsonPurchaseOrder = jsonPurchaseOrder;
            oldjsonPurchaseOrder = jsonPurchaseOrder;
            updateUI();
        } else {
            //Show error
        }
        dismissProgress();
    }

    @Override
    public void purchaseOrderCancelResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if (null != jsonPurchaseOrder) {
            if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.CO) {
                Toast.makeText(this, "Order cancelled successfully.", Toast.LENGTH_LONG).show();
                iv_home.performClick();
            } else {
                Toast.makeText(this, "failed to cancel order .", Toast.LENGTH_LONG).show();
            }
        } else {
            //Show error
        }
        dismissProgress();
    }


    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }
}
