package com.noqapp.android.merchant.views.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.DiscountTypeEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.CouponApiCalls;
import com.noqapp.android.merchant.model.ManageQueueApiCalls;
import com.noqapp.android.merchant.model.ReceiptInfoApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonCoupon;
import com.noqapp.android.merchant.presenter.beans.JsonDiscount;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CouponOnOrder;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.PermissionHelper;
import com.noqapp.android.merchant.utils.ReceiptGeneratorPDF;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.adapters.OrderItemAdapter;
import com.noqapp.android.merchant.views.interfaces.CouponOnOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.QueuePaymentPresenter;
import com.noqapp.android.merchant.views.interfaces.QueueRefundPaymentPresenter;
import com.noqapp.android.merchant.views.interfaces.ReceiptInfoPresenter;
import com.noqapp.android.merchant.views.pojos.Receipt;

import org.apache.commons.lang3.StringUtils;

public class OrderDetailActivity extends AppCompatActivity implements QueuePaymentPresenter,
        QueueRefundPaymentPresenter, ReceiptInfoPresenter, CouponOnOrderPresenter {
    private ProgressDialog progressDialog;
    protected ImageView actionbarBack;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private TextView tv_cost, tv_order_state, tv_transaction_id;
    private Spinner sp_payment_mode;
    private String[] payment_modes = {"Cash", "Cheque", "Credit Card", "Debit Card", "Internet Banking", "Paytm"};
    private PaymentModeEnum[] payment_modes_enum = {PaymentModeEnum.CA, PaymentModeEnum.CQ, PaymentModeEnum.CC, PaymentModeEnum.DC, PaymentModeEnum.NTB, PaymentModeEnum.PTM};
    private View rl_payment;
    private TextView tv_payment_mode, tv_payment_status, tv_address, tv_transaction_via;
    public static UpdateWholeList updateWholeList;
    private JsonQueuedPerson jsonQueuedPerson;
    private ManageQueueApiCalls manageQueueApiCalls;
    private String qCodeQR;
    private Button btn_refund, btn_pay_now,btn_discount;
    private TextView tv_paid_amount_value, tv_remaining_amount_value;
    private TextView tv_token, tv_q_name, tv_customer_name;
    private OrderItemAdapter adapter;
    private String currencySymbol;
    private long mLastClickTime = 0;
    private TextView tv_payment_msg;
    private TextView tv_discount_value;

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
        ListView listview = findViewById(R.id.listview);
        manageQueueApiCalls = new ManageQueueApiCalls();
        manageQueueApiCalls.setQueuePaymentPresenter(this);
        manageQueueApiCalls.setQueueRefundPaymentPresenter(this);
        tv_token = findViewById(R.id.tv_token);
        tv_q_name = findViewById(R.id.tv_q_name);
        tv_customer_name = findViewById(R.id.tv_customer_name);
        tv_payment_msg = findViewById(R.id.tv_payment_msg);

        tv_payment_mode = findViewById(R.id.tv_payment_mode);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_remaining_amount_value = findViewById(R.id.tv_remaining_amount_value);
        tv_paid_amount_value = findViewById(R.id.tv_paid_amount_value);
        tv_transaction_via = findViewById(R.id.tv_transaction_via);
        tv_address = findViewById(R.id.tv_address);
        tv_cost = findViewById(R.id.tv_cost);
        tv_order_state = findViewById(R.id.tv_order_state);
        tv_transaction_id = findViewById(R.id.tv_transaction_id);
        tv_discount_value = findViewById(R.id.tv_discount_value);
        sp_payment_mode = findViewById(R.id.sp_payment_mode);
        tv_discount_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(OrderDetailActivity.this, CouponActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, jsonPurchaseOrder.getCodeQR());
                startActivityForResult(in, Constants.ACTIVITTY_RESULT_BACK);
            }
        });
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
        qCodeQR = jsonPurchaseOrder.getCodeQR();
        rl_payment = findViewById(R.id.rl_payment);
        btn_pay_now = findViewById(R.id.btn_pay_now);
        btn_refund = findViewById(R.id.btn_refund);
        btn_discount = findViewById(R.id.btn_discount);
        btn_discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(OrderDetailActivity.this, CouponActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, jsonPurchaseOrder.getCodeQR());
                startActivityForResult(in, Constants.ACTIVITTY_RESULT_BACK);
            }
        });
        btn_refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                ShowCustomDialog showDialog = new ShowCustomDialog(OrderDetailActivity.this);
                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                    @Override
                    public void btnPositiveClick() {
                        if (LaunchActivity.getLaunchActivity().isOnline()) {
                            progressDialog.show();
                            progressDialog.setMessage("Starting payment refund..");
                            progressDialog.setCancelable(false);
                            progressDialog.setCanceledOnTouchOutside(false);
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
        currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        initProgress();
        updateUI();
        TextView tv_item_count = findViewById(R.id.tv_item_count);
        tv_item_count.setText("Total Items: (" + jsonPurchaseOrder.getPurchaseOrderProducts().size() + ")");
        btn_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.CO) {
                    new CustomToast().showToast(OrderDetailActivity.this, "Payment not allowed on cancelled order.");
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
                                    progressDialog.setCancelable(false);
                                    progressDialog.setCanceledOnTouchOutside(false);
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
                        new CustomToast().showToast(OrderDetailActivity.this, "Payment not allowed on Cancelled/Skipped order.");
                    }
                }
            }
        });
        ReceiptInfoApiCalls receiptInfoApiCalls = new ReceiptInfoApiCalls();
        receiptInfoApiCalls.setReceiptInfoPresenter(this);
        PermissionHelper permissionHelper = new PermissionHelper(this);
        Button btn_print = findViewById(R.id.btn_print);
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (TextUtils.isEmpty(jsonPurchaseOrder.getTransactionId())) {
                    ShowCustomDialog showDialog = new ShowCustomDialog(OrderDetailActivity.this,false);
                    showDialog.displayDialog("Alert", "Transaction Id is empty. Receipt can't be generated");
                }else{
                    if (permissionHelper.isStoragePermissionAllowed()) {
                        progressDialog.show();
                        progressDialog.setMessage("Fetching receipt info...");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        Receipt receipt = new Receipt();
                        receipt.setCodeQR(jsonPurchaseOrder.getCodeQR());
                        receipt.setQueueUserId(jsonPurchaseOrder.getQueueUserId());
                        receipt.setTransactionId(jsonPurchaseOrder.getTransactionId());
                        receiptInfoApiCalls.detail(BaseLaunchActivity.getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(), receipt);
                    } else {
                        permissionHelper.requestStoragePermission();
                    }
                }
            }
        });
        adapter = new OrderItemAdapter(this, jsonPurchaseOrder.getPurchaseOrderProducts(), currencySymbol);
        listview.setAdapter(adapter);
    }

    private void updateUI() {
        btn_refund.setVisibility(View.GONE);
        tv_customer_name.setText(jsonQueuedPerson.getCustomerName());
        tv_token.setText("Token/Order No. " + String.valueOf(jsonQueuedPerson.getToken()));
        tv_q_name.setText(jsonPurchaseOrder.getDisplayName());
        tv_address.setText(Html.fromHtml(StringUtils.isBlank(jsonPurchaseOrder.getDeliveryAddress()) ? "N/A" : jsonPurchaseOrder.getDeliveryAddress()));
        tv_paid_amount_value.setText(currencySymbol + " " + jsonPurchaseOrder.computePaidAmount());
        tv_remaining_amount_value.setText(currencySymbol + " " + jsonPurchaseOrder.computeBalanceAmount());
        if (PaymentStatusEnum.PP == jsonPurchaseOrder.getPaymentStatus()) {
            rl_payment.setVisibility(View.VISIBLE);
            btn_discount.setVisibility(View.VISIBLE);
        } else {
            rl_payment.setVisibility(View.GONE);
            btn_discount.setVisibility(View.GONE);
        }
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus() ||
                PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus() ||
                PaymentStatusEnum.PR == jsonPurchaseOrder.getPaymentStatus()) {
            tv_payment_mode.setText(jsonPurchaseOrder.getPaymentMode().getDescription());
            tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
            if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.PO) {
                btn_refund.setVisibility(View.VISIBLE);
            }
        } else {
            tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());

        }
        tv_order_state.setText(null == jsonPurchaseOrder.getPresentOrderState() ? "N/A" : jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
        tv_transaction_id.setText(null == jsonPurchaseOrder.getTransactionId() ? "N/A" : CommonHelper.transactionForDisplayOnly(jsonPurchaseOrder.getTransactionId()));
        if (null == jsonPurchaseOrder.getTransactionVia()) {
            tv_transaction_via.setText("N/A");
        } else {
            tv_transaction_via.setText(jsonPurchaseOrder.getTransactionVia().getDescription());
        }
        try {
            tv_cost.setText(currencySymbol + " " + CommonHelper.displayPrice((jsonPurchaseOrder.getOrderPrice())));
        } catch (Exception e) {
            e.printStackTrace();
            tv_cost.setText(currencySymbol + " " + String.valueOf(0 / 100));
        }

        if (PurchaseOrderStateEnum.CO == jsonPurchaseOrder.getPresentOrderState() && null == jsonPurchaseOrder.getPaymentMode()) {
            rl_payment.setVisibility(View.GONE);
            btn_discount.setVisibility(View.GONE);
            tv_payment_mode.setText("N/A");
        }
        tv_payment_msg.setVisibility(View.GONE);
        if(getIntent().getBooleanExtra(IBConstant.KEY_IS_PAYMENT_NOT_ALLOWED,false)){
            rl_payment.setVisibility(View.GONE);
            btn_discount.setVisibility(View.GONE);
            btn_refund.setVisibility(View.GONE);
            tv_payment_msg.setVisibility(View.VISIBLE);
            if(getIntent().getBooleanExtra(IBConstant.KEY_IS_HISTORY,false)){
                tv_payment_msg.setVisibility(View.GONE);
            }
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
                new CustomToast().showToast(OrderDetailActivity.this, "Payment updated successfully");
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
                new CustomToast().showToast(OrderDetailActivity.this, "Payment refund successfully");
                if (null != updateWholeList) {
                    updateWholeList.updateWholeList();
                }
            }
        }
    }

    @Override
    public void receiptInfoResponse(Receipt receipt) {
        try {
            Log.e("Data", receipt.toString());
            ReceiptGeneratorPDF receiptGeneratorPDF = new ReceiptGeneratorPDF(OrderDetailActivity.this);
            receiptGeneratorPDF.createPdf(receipt);
        }catch (Exception e){
            e.printStackTrace();
        }
        dismissProgress();
    }

    @Override
    public void discountOnOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if(null != jsonPurchaseOrder){
            this.jsonPurchaseOrder = jsonPurchaseOrder;
            updateUI();
            Log.e("Resp: JsonPurchaseOrder", jsonPurchaseOrder.toString());
        }else{
            new CustomToast().showToast(this,"JsonPurchaseOrder is NULL");
        }

        dismissProgress();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITTY_RESULT_BACK) {
            if (resultCode == RESULT_OK) {
                JsonCoupon jsonCoupon = (JsonCoupon) data.getSerializableExtra(IBConstant.KEY_OBJECT);
                Log.e("data recieve", jsonCoupon.toString());
                if(jsonCoupon.getDiscountType() == DiscountTypeEnum.F){
                    tv_discount_value.setText(currencySymbol + " " +jsonCoupon.getDiscountAmount());
                }else{
                    tv_discount_value.setText(jsonCoupon.getDiscountAmount()+"% discount");
                }

                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    progressDialog.show();
                    progressDialog.setMessage("Applying discount..");
                   // progressDialog.setCancelable(false);
                   // progressDialog.setCanceledOnTouchOutside(false);
                    CouponApiCalls couponApiCalls = new CouponApiCalls();
                    couponApiCalls.setCouponOnOrderPresenter(this);

                    CouponOnOrder couponOnOrder = new CouponOnOrder()
                            .setQueueUserId(jsonQueuedPerson.getJsonPurchaseOrder().getQueueUserId())
                            .setCouponId(jsonCoupon.getCouponId())
                            .setTransactionId(jsonQueuedPerson.getTransactionId());

                    couponApiCalls.apply(BaseLaunchActivity.getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(),
                            couponOnOrder);
                } else {
                    ShowAlertInformation.showNetworkDialog(OrderDetailActivity.this);
                }
            }
        }
    }
}