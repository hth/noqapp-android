package com.noqapp.android.merchant.views.activities;


import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OrderDetailActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    protected ImageView actionbarBack;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private boolean isProductWithoutPrice = false;
    private TextView tv_cost;
    private Spinner sp_payment_mode;
    private String[] payment_modes = {"Cash", "Cheque", "Credit Card", "Debit Card", "Internet Banking", "Paytm"};
    private PaymentModeEnum[] payment_modes_enum = {PaymentModeEnum.CA, PaymentModeEnum.CQ, PaymentModeEnum.CC, PaymentModeEnum.DC, PaymentModeEnum.NTB, PaymentModeEnum.PTM};
    private Button btn_update_price;
    private View rl_payment;
    private TextView tv_payment_mode, tv_payment_status, tv_address, tv_multiple_payment;
    public static UpdateWholeList updateWholeList;
    private RelativeLayout rl_multiple;

    public interface UpdateWholeList {
        void updateWholeList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_order_detail);

        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        initProgress();
        jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getSerializableExtra("jsonPurchaseOrder");
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                // updateWholeList = null;
                finish();
            }
        });

        tv_toolbar_title.setText(getString(R.string.order_details));

        tv_payment_mode = findViewById(R.id.tv_payment_mode);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_address = findViewById(R.id.tv_address);
        btn_update_price = findViewById(R.id.btn_update_price);
        tv_cost = findViewById(R.id.tv_cost);
        tv_multiple_payment = findViewById(R.id.tv_multiple_payment);
        rl_multiple = findViewById(R.id.rl_multiple);
        sp_payment_mode = findViewById(R.id.sp_payment_mode);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, payment_modes);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_payment_mode.setAdapter(aa);
        sp_payment_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    ((TextView) view).setTextColor(Color.BLACK); //Change selected text color
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rl_payment = findViewById(R.id.rl_payment);

        Button btn_pay_now = findViewById(R.id.btn_pay_now);
        initProgress();
        updateUI();


        btn_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProductWithoutPrice) {
                    Toast.makeText(OrderDetailActivity.this, "Some product having 0 price. Please set price to them", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.show();
                    progressDialog.setMessage("Starting payment..");
                    jsonPurchaseOrder.setPaymentMode(payment_modes_enum[sp_payment_mode.getSelectedItemPosition()]);
                    jsonPurchaseOrder.setPartialPayment(jsonPurchaseOrder.getOrderPrice());

                }

            }
        });

    }


    private void updateUI() {
        tv_address.setText(Html.fromHtml(jsonPurchaseOrder.getDeliveryAddress()));
        String currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        if (PaymentStatusEnum.PP == jsonPurchaseOrder.getPaymentStatus() ||
                PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
            if (isProductWithoutPrice) {
                rl_payment.setVisibility(View.GONE);
                btn_update_price.setVisibility(View.VISIBLE);
            } else {
                rl_payment.setVisibility(View.VISIBLE);
                btn_update_price.setVisibility(View.GONE);
            }
            if (PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
                rl_multiple.setVisibility(View.VISIBLE);
                tv_multiple_payment.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getPartialPayment()) / 100));
            } else {
                rl_multiple.setVisibility(View.GONE);
                tv_multiple_payment.setText("");
            }
        } else {
            rl_payment.setVisibility(View.GONE);
        }
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus() ||
                PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
            tv_payment_mode.setText(jsonPurchaseOrder.getPaymentMode().getDescription());
            tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
            if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
                //tv_paid_amount_value.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100));
            }
        } else {
            tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
        }

        try {
            tv_cost.setText(currencySymbol + " " + String.valueOf(Integer.parseInt(jsonPurchaseOrder.getOrderPrice()) / 100));
        } catch (Exception e) {
            tv_cost.setText(currencySymbol + " " + String.valueOf(0 / 100));
        }
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Queue Settings...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
        if (null != updateWholeList) {
            //updateWholeList = null;
        }
    }

//    @Override
//    public void responseErrorPresenter(ErrorEncounteredJson eej) {
//        dismissProgress();
//        new ErrorResponseHandler().processError(OrderDetailActivity.this, eej);
//    }
//
//    @Override
//    public void responseErrorPresenter(int errorCode) {
//        dismissProgress();
//    }
//
//    @Override
//    public void authenticationFailure() {
//        dismissProgress();
//        AppUtils.authenticationProcessing();
//    }
//
//    @Override
//    public void paymentProcessResponse(JsonPurchaseOrder jsonPurchaseOrder) {
//        dismissProgress();
//        if (null != jsonPurchaseOrder) {
//            if (jsonPurchaseOrder.getPaymentStatus() == PaymentStatusEnum.PA ||
//                    jsonPurchaseOrder.getPaymentStatus() == PaymentStatusEnum.MP) {
//                this.jsonPurchaseOrder = jsonPurchaseOrder;
//                updateUI();
//                Toast.makeText(OrderDetailActivity.this, "Payment updated successfully", Toast.LENGTH_LONG).show();
//                if (null != updateWholeList) {
//                    updateWholeList.updateWholeList();
//                }
//            }
//        }
//    }
//
//    @Override
//    public void purchaseOrderResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
//        dismissProgress();
//        if (null != jsonPurchaseOrderList) {
//            Log.v("order data:", jsonPurchaseOrderList.toString());
//            finish();
//            if (null != updateWholeList) {
//                updateWholeList.updateWholeList();
//            }
//        }
//    }
}
