package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.PurchaseApiModel;
import com.noqapp.android.client.presenter.PurchaseOrderPresenter;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.OrderDetail;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OrderConfirmActivity extends BaseActivity implements PurchaseOrderPresenter, ActivityCommunicator {

    private PurchaseApiModel purchaseApiModel;
    private TextView tv_total_order_amt;
    private TextView tv_tax_amt;
    private TextView tv_due_amt;
    private LinearLayout ll_order_details;
    private TextView tv_serving_no;
    private TextView tv_token;
    private TextView tv_status;
    private TextView tv_estimated_time;
    private JsonPurchaseOrder jsonPurchaseOrder, oldjsonPurchaseOrder;
    private Button btn_cancel_order;
    private String codeQR;
    private int currentServing = -1;

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
        tv_status = findViewById(R.id.tv_status);
        tv_estimated_time = findViewById(R.id.tv_estimated_time);
        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_address = findViewById(R.id.tv_address);
        btn_cancel_order = findViewById(R.id.btn_cancel_order);
        LaunchActivity.getLaunchActivity().activityCommunicator = this;
        initActionsViews(true);
        purchaseApiModel = new PurchaseApiModel(this);
        tv_toolbar_title.setText(getString(R.string.screen_order_confirm));
        tv_store_name.setText(getIntent().getExtras().getString("storeName"));
        tv_address.setText(getIntent().getExtras().getString("storeAddress"));
        codeQR = getIntent().getExtras().getString(NoQueueBaseFragment.KEY_CODE_QR);
        currentServing = getIntent().getExtras().getInt("currentServing");
        if (getIntent().getBooleanExtra(NoQueueBaseFragment.KEY_FROM_LIST, false)) {
            tv_toolbar_title.setText(getString(R.string.order_details));
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                progressDialog.setMessage("Order details fetching in progress..");
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
                LaunchActivity.getLaunchActivity().activityCommunicator = null;
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
        String currencySymbol = getIntent().getExtras().getString(AppUtilities.CURRENCY_SYMBOL);
        tv_tax_amt.setText(currencySymbol + "" + "0.0");
        if (jsonPurchaseOrder.getBusinessType() == BusinessTypeEnum.PH) {   // to avoid crash it is added for  Pharmacy order place from merchant side directly
            jsonPurchaseOrder.setOrderPrice("0");
        }
        tv_due_amt.setText(currencySymbol + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        tv_total_order_amt.setText(currencySymbol + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        for (int i = 0; i < oldjsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = oldjsonPurchaseOrder.getPurchaseOrderProducts().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_total_price = inflatedLayout.findViewById(R.id.tv_total_price);
            tv_title.setText(jsonPurchaseOrderProduct.getProductName() + " " + currencySymbol + "" + (jsonPurchaseOrderProduct.getProductPrice() / 100) + " x " + String.valueOf(jsonPurchaseOrderProduct.getProductQuantity()));
            tv_total_price.setText(currencySymbol + "" + jsonPurchaseOrderProduct.getProductPrice() * jsonPurchaseOrderProduct.getProductQuantity() / 100);
            if (jsonPurchaseOrder.getBusinessType() == BusinessTypeEnum.PH) {
                //added for  Pharmacy order place from merchant side directly
                findViewById(R.id.ll_amount).setVisibility(View.GONE);
                tv_total_price.setVisibility(View.GONE);
            }
            ll_order_details.addView(inflatedLayout);
        }
        int currentTemp = currentServing == -1 ? jsonPurchaseOrder.getServingNumber() : currentServing;
        tv_serving_no.setText(jsonPurchaseOrder.getToken() - currentTemp <= 0? String.valueOf(jsonPurchaseOrder.getToken()):String.valueOf(currentTemp));
        switch (jsonPurchaseOrder.getPresentOrderState()) {
            case OP:
                tv_status.setText("Order being prepared");
                break;
            case RD:
            case RP:
            case OD:
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getDescription());
                break;
            default:
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getDescription());
        }
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
        LaunchActivity.getLaunchActivity().activityCommunicator = null;
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
    public void purchaseOrderActivateResponse(JsonPurchaseOrderHistorical jsonPurchaseOrderHistorical) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LaunchActivity.getLaunchActivity().activityCommunicator = null;
    }

    @Override
    public boolean updateUI(String qrCode, JsonTokenAndQueue jq, String go_to) {
        if (codeQR.equals(qrCode) && String.valueOf(jsonPurchaseOrder.getToken()).equals(String.valueOf(jq.getServingNumber()))) {
            //updating the current order status
            if (jq.getToken() - jq.getServingNumber() <= 0) {
                switch (jq.getPurchaseOrderState()) {
                    case OP:
                        tv_status.setText("Order being prepared");
                        break;
                    case RD:
                    case RP:
                    case OD:
                        tv_status.setText(jq.getPurchaseOrderState().getDescription());
                        break;
                    default:
                        tv_status.setText(jq.getPurchaseOrderState().getDescription());
                }
            }
            int currentTemp = currentServing == -1 ? jq.getServingNumber() : currentServing;
            tv_serving_no.setText(jq.getToken() - currentTemp <= 0? String.valueOf(jsonPurchaseOrder.getToken()):String.valueOf(currentTemp));
        }
        return false;
    }

    @Override
    public void requestProcessed(String qrCode, String token) {
        if (codeQR.equals(qrCode) && String.valueOf(jsonPurchaseOrder.getToken()).equals(token)) {
            //remove the screen from stack
            finish();
        }
    }

}
