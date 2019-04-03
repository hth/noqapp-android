package com.noqapp.android.merchant.views.activities;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.views.interfaces.QueuePaymentPresenter;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OrderDetailActivity extends AppCompatActivity implements QueuePaymentPresenter {
    private ProgressDialog progressDialog;
    protected ImageView actionbarBack;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private TextView tv_cost;
    private Spinner sp_payment_mode;
    private String[] payment_modes = {"Cash", "Cheque", "Credit Card", "Debit Card", "Internet Banking", "Paytm"};
    private PaymentModeEnum[] payment_modes_enum = {PaymentModeEnum.CA, PaymentModeEnum.CQ, PaymentModeEnum.CC, PaymentModeEnum.DC, PaymentModeEnum.NTB, PaymentModeEnum.PTM};
    private View rl_payment;
    private TextView tv_payment_mode, tv_payment_status, tv_address;
    public static UpdateWholeList updateWholeList;
    private JsonQueuedPerson jsonQueuedPerson;
    private ManageQueueApiCalls manageQueueApiCalls;
    private String qCodeQR;

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
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("jsonQueuedPerson");
        jsonPurchaseOrder = jsonQueuedPerson.getJsonPurchaseOrder();
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                // updateWholeList = null;
                finish();
            }
        });

        tv_toolbar_title.setText(getString(R.string.order_details));
        manageQueueApiCalls = new ManageQueueApiCalls();
        manageQueueApiCalls.setQueuePaymentPresenter(this);
        tv_payment_mode = findViewById(R.id.tv_payment_mode);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_address = findViewById(R.id.tv_address);
        tv_cost = findViewById(R.id.tv_cost);
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
        qCodeQR = getIntent().getStringExtra("qCodeQR");
        rl_payment = findViewById(R.id.rl_payment);
        Button btn_pay_now = findViewById(R.id.btn_pay_now);
        initProgress();
        updateUI();
        btn_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    progressDialog.show();
                    progressDialog.setMessage("Starting payment..");
                    JsonQueuedPerson jqp = new JsonQueuedPerson()
                            .setQueueUserId(jsonQueuedPerson.getQueueUserId())
                            .setToken(jsonQueuedPerson.getToken());

                    JsonPurchaseOrder jpo = new JsonPurchaseOrder()
                            .setQueueUserId(jsonQueuedPerson.getJsonPurchaseOrder().getQueueUserId())
                            .setCodeQR(qCodeQR)
                            .setBizStoreId(jsonQueuedPerson.getJsonPurchaseOrder().getBizStoreId())
                            .setTransactionId(jsonQueuedPerson.getTransactionId())
                            .setPaymentMode(payment_modes_enum[sp_payment_mode.getSelectedItemPosition()]);
                    jqp.setJsonPurchaseOrder(jpo);
                    manageQueueApiCalls.counterPayment(BaseLaunchActivity.getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(),
                            jqp);
                }
        });

    }

    private void updateUI() {
        tv_address.setText(Html.fromHtml(StringUtils.isBlank(jsonPurchaseOrder.getDeliveryAddress()) ? "N/A" : jsonPurchaseOrder.getDeliveryAddress())));
        String currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        if (PaymentStatusEnum.PP == jsonPurchaseOrder.getPaymentStatus()) {
            rl_payment.setVisibility(View.VISIBLE);
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

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(OrderDetailActivity.this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    @Override
    public void queuePaymentResponse(JsonQueuedPerson jsonQueuedPerson) {
        dismissProgress();
        if (null != jsonQueuedPerson) {
            jsonPurchaseOrder = jsonQueuedPerson.getJsonPurchaseOrder();
            if (jsonPurchaseOrder.getPaymentStatus() == PaymentStatusEnum.PA) {
                updateUI();
                Toast.makeText(OrderDetailActivity.this, "Payment updated successfully", Toast.LENGTH_LONG).show();
                if (null != updateWholeList) {
                    updateWholeList.updateWholeList();
                }
            }
        }
    }
}