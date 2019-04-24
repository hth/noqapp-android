package com.noqapp.android.merchant.views.activities;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.interfaces.QueuePaymentPresenter;
import com.noqapp.android.merchant.views.interfaces.QueueRefundPaymentPresenter;

import org.apache.commons.lang3.StringUtils;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OrderDetailActivity extends AppCompatActivity implements QueuePaymentPresenter, QueueRefundPaymentPresenter {
    private ProgressDialog progressDialog;
    protected ImageView actionbarBack;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private TextView tv_cost, tv_order_state, tv_consultation_fee_value;
    private Spinner sp_payment_mode;
    private String[] payment_modes = {"Cash", "Cheque", "Credit Card", "Debit Card", "Internet Banking", "Paytm"};
    private PaymentModeEnum[] payment_modes_enum = {PaymentModeEnum.CA, PaymentModeEnum.CQ, PaymentModeEnum.CC, PaymentModeEnum.DC, PaymentModeEnum.NTB, PaymentModeEnum.PTM};
    private View rl_payment;
    private TextView tv_payment_mode, tv_payment_status, tv_address, tv_transaction_via;
    public static UpdateWholeList updateWholeList;
    private JsonQueuedPerson jsonQueuedPerson;
    private ManageQueueApiCalls manageQueueApiCalls;
    private String qCodeQR;
    private Button btn_refund, btn_pay_now;
    private TextView tv_paid_amount_value, tv_remaining_amount_value;
    private TextView tv_token,tv_q_name,tv_customer_name;

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
        manageQueueApiCalls.setQueueRefundPaymentPresenter(this);
        tv_token = findViewById(R.id.tv_token);
        tv_q_name = findViewById(R.id.tv_q_name);
        tv_customer_name = findViewById(R.id.tv_customer_name);

        tv_payment_mode = findViewById(R.id.tv_payment_mode);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_remaining_amount_value = findViewById(R.id.tv_remaining_amount_value);
        tv_paid_amount_value = findViewById(R.id.tv_paid_amount_value);
        tv_transaction_via = findViewById(R.id.tv_transaction_via);
        tv_address = findViewById(R.id.tv_address);
        tv_cost = findViewById(R.id.tv_cost);
        tv_consultation_fee_value = findViewById(R.id.tv_consultation_fee_value);
        tv_order_state = findViewById(R.id.tv_order_state);
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
        btn_pay_now = findViewById(R.id.btn_pay_now);
        btn_refund = findViewById(R.id.btn_refund);
        btn_refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ShowCustomDialog showDialog = new ShowCustomDialog(OrderDetailActivity.this);
                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                    @Override
                    public void btnPositiveClick() {
                        if (LaunchActivity.getLaunchActivity().isOnline()) {
                            progressDialog.show();
                            progressDialog.setMessage("Starting payment refund..");
                            JsonQueuedPerson jqp = new JsonQueuedPerson()
                                    .setQueueUserId(jsonQueuedPerson.getQueueUserId())
                                    .setToken(jsonQueuedPerson.getToken());

                            JsonPurchaseOrder jpo = new JsonPurchaseOrder()
                                    .setQueueUserId(jsonQueuedPerson.getJsonPurchaseOrder().getQueueUserId())
                                    .setCodeQR(qCodeQR)
                                    .setBizStoreId(jsonQueuedPerson.getJsonPurchaseOrder().getBizStoreId())
                                    .setToken(jsonQueuedPerson.getToken())
                                    .setTransactionId(jsonQueuedPerson.getTransactionId());
                            jqp.setJsonPurchaseOrder(jpo);
                            manageQueueApiCalls.cancel(BaseLaunchActivity.getDeviceID(),
                                    LaunchActivity.getLaunchActivity().getEmail(),
                                    LaunchActivity.getLaunchActivity().getAuth(),
                                    jqp);
                        } else {
                            ShowAlertInformation.showNetworkDialog(OrderDetailActivity.this);
                        }
                    }

                    @Override
                    public void btnNegativeClick() {
                        //Do nothing
                    }
                });
                showDialog.displayDialog("Alert", "You are initiating refund process. Please confirm");
            }
        });
        initProgress();
        updateUI();
        TextView tv_item_count = findViewById(R.id.tv_item_count);
        tv_item_count.setText("Total Items: (1)");
        btn_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.CO) {
                    Toast.makeText(OrderDetailActivity.this, "Payment not allowed on cancelled order.", Toast.LENGTH_SHORT).show();
                } else {
                    if (jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q ||
                            jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.S) {

                        ShowCustomDialog showDialog = new ShowCustomDialog(OrderDetailActivity.this);
                        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                            @Override
                            public void btnPositiveClick() {
                                if (LaunchActivity.getLaunchActivity().isOnline()) {
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
                                } else {
                                    ShowAlertInformation.showNetworkDialog(OrderDetailActivity.this);
                                }
                            }

                            @Override
                            public void btnNegativeClick() {
                                //Do nothing
                            }
                        });
                        showDialog.displayDialog("Alert", "You are initiating payment process. Please confirm");
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "Payment not allowed on Cancelled/Skipped order.", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }

    private void updateUI() {
        btn_refund.setVisibility(View.GONE);
        tv_customer_name.setText(jsonQueuedPerson.getCustomerName());
        tv_token.setText("Token/Order No. "+String.valueOf(jsonQueuedPerson.getToken()));
        tv_q_name.setText(getIntent().getStringExtra("qName"));
        tv_address.setText(Html.fromHtml(StringUtils.isBlank(jsonPurchaseOrder.getDeliveryAddress()) ? "N/A" : jsonPurchaseOrder.getDeliveryAddress()));
        String currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        try {
            if (TextUtils.isEmpty(jsonPurchaseOrder.getPartialPayment())) {
                tv_paid_amount_value.setText(currencySymbol + " " + String.valueOf(0));
                tv_remaining_amount_value.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100));
            } else {
                tv_paid_amount_value.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getPartialPayment()) / 100));
                tv_remaining_amount_value.setText(currencySymbol + " " + String.valueOf((Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) - Double.parseDouble(jsonPurchaseOrder.getPartialPayment())) / 100));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (PaymentStatusEnum.PP == jsonPurchaseOrder.getPaymentStatus()) {
            rl_payment.setVisibility(View.VISIBLE);
        } else {
            rl_payment.setVisibility(View.GONE);
        }
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus() ||
                PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus() ||
                PaymentStatusEnum.PR == jsonPurchaseOrder.getPaymentStatus()) {
            tv_payment_mode.setText(jsonPurchaseOrder.getPaymentMode().getDescription());
            tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
            if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
                tv_paid_amount_value.setText(currencySymbol + " " + String.valueOf(Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100));
                tv_remaining_amount_value.setText(currencySymbol + " 0");
            }
            if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.PO) {
                btn_refund.setVisibility(View.VISIBLE);
            }
        } else {
            tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());

        }
        tv_order_state.setText(null == jsonPurchaseOrder.getPresentOrderState() ? "N/A":jsonPurchaseOrder.getPresentOrderState().getDescription());
        if (null == jsonPurchaseOrder.getTransactionVia()) {
            tv_transaction_via.setText("N/A");
        } else {
            tv_transaction_via.setText(jsonPurchaseOrder.getTransactionVia().getDescription());
        }
        try {
            tv_cost.setText(currencySymbol + " " + CommonHelper.displayPrice((jsonPurchaseOrder.getOrderPrice())));
            tv_consultation_fee_value.setText(currencySymbol + " " + CommonHelper.displayPrice((jsonPurchaseOrder.getOrderPrice())));
        } catch (Exception e) {
            //TODO log error
            tv_cost.setText(currencySymbol + " " + String.valueOf(0 / 100));
            tv_consultation_fee_value.setText(currencySymbol + " " + String.valueOf(0 / 100));
        }

        if (PurchaseOrderStateEnum.CO == jsonPurchaseOrder.getPresentOrderState()&& null == jsonPurchaseOrder.getPaymentMode()) {
            rl_payment.setVisibility(View.GONE);
            tv_payment_mode.setText("N/A");
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

    @Override
    public void queueRefundPaymentResponse(JsonQueuedPerson jsonQueuedPerson) {
        dismissProgress();
        if (null != jsonQueuedPerson) {
            jsonPurchaseOrder = jsonQueuedPerson.getJsonPurchaseOrder();
            if (jsonPurchaseOrder.getPaymentStatus() == PaymentStatusEnum.PR) {
                updateUI();
                Toast.makeText(OrderDetailActivity.this, "Payment refund successfully", Toast.LENGTH_LONG).show();
                if (null != updateWholeList) {
                    updateWholeList.updateWholeList();
                }
            }
        }
    }
}