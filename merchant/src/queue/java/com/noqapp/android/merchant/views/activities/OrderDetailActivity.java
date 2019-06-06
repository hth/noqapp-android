package com.noqapp.android.merchant.views.activities;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ReceiptInfoApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.merchant.OrderServed;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.PermissionHelper;
import com.noqapp.android.merchant.utils.ReceiptGeneratorPDF;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.OrderItemAdapter;
import com.noqapp.android.merchant.views.interfaces.ModifyOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.OrderProcessedPresenter;
import com.noqapp.android.merchant.views.interfaces.PaymentProcessPresenter;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.ReceiptInfoPresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderApiCalls;
import com.noqapp.android.merchant.views.pojos.Receipt;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class OrderDetailActivity extends AppCompatActivity implements PaymentProcessPresenter, PurchaseOrderPresenter, ModifyOrderPresenter, OrderProcessedPresenter, ReceiptInfoPresenter {
    private ProgressDialog progressDialog;
    protected ImageView actionbarBack;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private boolean isProductWithoutPrice = false;
    private TextView tv_cost;
    private String currencySymbol;
    private TextView tv_paid_amount_value, tv_remaining_amount_value, tv_notes;
    private Spinner sp_payment_mode;
    private String[] payment_modes = {"Cash", "Cheque", "Credit Card", "Debit Card", "Internet Banking", "Paytm"};
    private PaymentModeEnum[] payment_modes_enum = {PaymentModeEnum.CA, PaymentModeEnum.CQ, PaymentModeEnum.CC, PaymentModeEnum.DC, PaymentModeEnum.NTB, PaymentModeEnum.PTM};
    private Button btn_update_price;
    private CardView cv_notes;
    private EditText edt_amount;
    private View rl_payment;
    private TextView tv_payment_mode, tv_payment_status, tv_address, tv_multiple_payment, tv_transaction_via, tv_order_state, tv_transaction_id;
    private Button btn_pay_partial, btn_refund;
    public static UpdateWholeList updateWholeList;
    private RelativeLayout rl_multiple;
    private TextView tv_token, tv_q_name, tv_customer_name;
    private OrderItemAdapter adapter;
    private long mLastClickTime = 0;
    private TextView tv_payment_msg;

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
        checkProductWithZeroPrice();
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                // updateWholeList = null;
                finish();
            }
        });

        tv_toolbar_title.setText(getString(R.string.order_details));
        tv_token = findViewById(R.id.tv_token);
        tv_q_name = findViewById(R.id.tv_q_name);
        tv_customer_name = findViewById(R.id.tv_customer_name);
        tv_payment_msg = findViewById(R.id.tv_payment_msg);
        ListView listview = findViewById(R.id.listview);
        TextView tv_item_count = findViewById(R.id.tv_item_count);
        tv_payment_mode = findViewById(R.id.tv_payment_mode);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_address = findViewById(R.id.tv_address);
        btn_update_price = findViewById(R.id.btn_update_price);
        tv_cost = findViewById(R.id.tv_cost);
        tv_multiple_payment = findViewById(R.id.tv_multiple_payment);
        tv_transaction_via = findViewById(R.id.tv_transaction_via);
        tv_order_state = findViewById(R.id.tv_order_state);
        tv_transaction_id = findViewById(R.id.tv_transaction_id);
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
        tv_notes = findViewById(R.id.tv_notes);
        tv_remaining_amount_value = findViewById(R.id.tv_remaining_amount_value);
        tv_paid_amount_value = findViewById(R.id.tv_paid_amount_value);
        cv_notes = findViewById(R.id.cv_notes);
        edt_amount = findViewById(R.id.edt_amount);
        rl_payment = findViewById(R.id.rl_payment);
        btn_refund = findViewById(R.id.btn_refund);
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
                            OrderServed orderServed = new OrderServed();
                            orderServed.setCodeQR(jsonPurchaseOrder.getCodeQR());
                            orderServed.setServedNumber(jsonPurchaseOrder.getToken());
                            orderServed.setQueueUserId(jsonPurchaseOrder.getQueueUserId());
                            orderServed.setTransactionId(jsonPurchaseOrder.getTransactionId());
                            orderServed.setQueueStatus(QueueStatusEnum.N);
                            orderServed.setPurchaseOrderState(jsonPurchaseOrder.getPresentOrderState());
                            PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                            purchaseOrderApiCalls.setOrderProcessedPresenter(OrderDetailActivity.this);
                            purchaseOrderApiCalls.cancel(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderServed);

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
        Button btn_pay_now = findViewById(R.id.btn_pay_now);
        btn_pay_partial = findViewById(R.id.btn_pay_partial);
        initProgress();
        btn_pay_partial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isProductWithoutPrice) {
                    new CustomToast().showToast(OrderDetailActivity.this, "Some product having 0 price. Please set price to them");
                } else {
                    if (TextUtils.isEmpty(edt_amount.getText().toString())) {
                        new CustomToast().showToast(OrderDetailActivity.this, "Please enter amount to pay.");
                    } else {
                        if (Double.parseDouble(edt_amount.getText().toString()) * 100 > Double.parseDouble(jsonPurchaseOrder.getOrderPrice())) {
                            new CustomToast().showToast(OrderDetailActivity.this, "Please enter amount less or equal to order amount.");
                        } else {

                            ShowCustomDialog showDialog = new ShowCustomDialog(OrderDetailActivity.this);
                            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                                @Override
                                public void btnPositiveClick() {
                                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                                        progressDialog.show();
                                        progressDialog.setMessage("Starting payment..");
                                        progressDialog.setCancelable(false);
                                        progressDialog.setCanceledOnTouchOutside(false);
                                        jsonPurchaseOrder.setPaymentMode(payment_modes_enum[sp_payment_mode.getSelectedItemPosition()]);
                                        jsonPurchaseOrder.setPartialPayment(new BigDecimal(edt_amount.getText().toString()).multiply(new BigDecimal("100")).toString());
                                        PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                                        purchaseOrderApiCalls.setPaymentProcessPresenter(OrderDetailActivity.this);
                                        purchaseOrderApiCalls.partialCounterPayment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
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
                        }
                    }
                }
            }
        });
        btn_update_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isProductWithoutPrice) {
                    new CustomToast().showToast(OrderDetailActivity.this, "Some product having 0 price. Please set price to them");
                } else {
                    progressDialog.show();
                    progressDialog.setMessage("Updating price..");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                    purchaseOrderApiCalls.setModifyOrderPresenter(OrderDetailActivity.this);
                    purchaseOrderApiCalls.modify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                }
            }
        });

        btn_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isProductWithoutPrice) {
                    new CustomToast().showToast(OrderDetailActivity.this, "Some product having 0 price. Please set price to them");
                } else {
                    ShowCustomDialog showDialog = new ShowCustomDialog(OrderDetailActivity.this);
                    showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                        @Override
                        public void btnPositiveClick() {
                            if (LaunchActivity.getLaunchActivity().isOnline()) {
                                progressDialog.show();
                                progressDialog.setMessage("Starting payment..");
                                progressDialog.setCancelable(false);
                                progressDialog.setCanceledOnTouchOutside(false);
                                jsonPurchaseOrder.setPaymentMode(payment_modes_enum[sp_payment_mode.getSelectedItemPosition()]);
                                jsonPurchaseOrder.setPartialPayment(jsonPurchaseOrder.getOrderPrice());
                                PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
                                purchaseOrderApiCalls.setPaymentProcessPresenter(OrderDetailActivity.this);
                                purchaseOrderApiCalls.setPurchaseOrderPresenter(OrderDetailActivity.this);
                                purchaseOrderApiCalls.counterPayment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);

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
                }

            }
        });
        currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        tv_item_count.setText("Total Items: (" + jsonPurchaseOrder.getPurchaseOrderProducts().size() + ")");
        adapter = new OrderItemAdapter(this, jsonPurchaseOrder.getPurchaseOrderProducts(), currencySymbol, this);
        listview.setAdapter(adapter);
        updateUI();
        PermissionHelper permissionHelper = new PermissionHelper(this);
        ReceiptInfoApiCalls receiptInfoApiCalls = new ReceiptInfoApiCalls();
        receiptInfoApiCalls.setReceiptInfoPresenter(this);
        Button btn_print = findViewById(R.id.btn_print);
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (TextUtils.isEmpty(jsonPurchaseOrder.getTransactionId())) {
                    ShowCustomDialog showDialog = new ShowCustomDialog(OrderDetailActivity.this, false);
                    showDialog.displayDialog("Alert", "Transaction Id is empty. Receipt can't be generated");
                } else {
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
    }

    private void updateUI() {
        btn_refund.setVisibility(View.GONE);
        tv_customer_name.setText(jsonPurchaseOrder.getCustomerName());
        tv_token.setText("Token/Order No. " + String.valueOf(jsonPurchaseOrder.getToken()));
        tv_q_name.setText(jsonPurchaseOrder.getDisplayName());
        tv_notes.setText("Additional Notes: " + jsonPurchaseOrder.getAdditionalNote());
        cv_notes.setVisibility(TextUtils.isEmpty(jsonPurchaseOrder.getAdditionalNote()) ? View.GONE : View.VISIBLE);
        tv_address.setText(Html.fromHtml(StringUtils.isBlank(jsonPurchaseOrder.getDeliveryAddress()) ? "N/A" : jsonPurchaseOrder.getDeliveryAddress()));
        tv_order_state.setText(null == jsonPurchaseOrder.getPresentOrderState() ? "N/A" : jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
        tv_transaction_id.setText(null == jsonPurchaseOrder.getTransactionId() ? "N/A" : jsonPurchaseOrder.getTransactionId());
        tv_paid_amount_value.setText(currencySymbol + " " + jsonPurchaseOrder.computePaidAmount());
        tv_remaining_amount_value.setText(currencySymbol + " " + jsonPurchaseOrder.computeBalanceAmount());

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
                btn_pay_partial.setVisibility(View.INVISIBLE);
                edt_amount.setVisibility(View.INVISIBLE);
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
                PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus() ||
                PaymentStatusEnum.PR == jsonPurchaseOrder.getPaymentStatus()) {
            if (null != jsonPurchaseOrder.getPaymentMode()) {
                tv_payment_mode.setText(jsonPurchaseOrder.getPaymentMode().getDescription());
            }
            tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
            if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.PO) {
                btn_refund.setVisibility(View.VISIBLE);
            }
        } else {
            tv_payment_status.setText(jsonPurchaseOrder.getPaymentStatus().getDescription());
        }

        try {
            tv_cost.setText(currencySymbol + " " + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        } catch (Exception e) {
            tv_cost.setText(currencySymbol + " " + String.valueOf(0 / 100));
        }

        if (null == jsonPurchaseOrder.getTransactionVia()) {
            tv_transaction_via.setText("N/A");
        } else {
            tv_transaction_via.setText(jsonPurchaseOrder.getTransactionVia().getDescription());
        }

        if (PurchaseOrderStateEnum.CO == jsonPurchaseOrder.getPresentOrderState() && null == jsonPurchaseOrder.getPaymentMode()) {
            rl_payment.setVisibility(View.GONE);
            btn_update_price.setVisibility(View.GONE);
            tv_payment_mode.setText("N/A");
            adapter.setClickEnable(false);
        }
        tv_payment_msg.setVisibility(View.GONE);
        if (getIntent().getBooleanExtra(IBConstant.KEY_IS_PAYMENT_NOT_ALLOWED, false)) {
            rl_payment.setVisibility(View.GONE);
            btn_update_price.setVisibility(View.GONE);
            rl_multiple.setVisibility(View.GONE);
            btn_refund.setVisibility(View.GONE);
            tv_payment_msg.setVisibility(View.VISIBLE);
            if (getIntent().getBooleanExtra(IBConstant.KEY_IS_HISTORY, false)) {
                tv_payment_msg.setVisibility(View.GONE);
            }
        }
    }

    public void updateProductPriceList(JsonPurchaseOrderProduct jpop, int pos) {
        jsonPurchaseOrder.getPurchaseOrderProducts().set(pos, jpop);
        checkProductWithZeroPrice();
        jsonPurchaseOrder.setOrderPrice(String.valueOf(calculateTotalPrice()));
        tv_cost.setText(currencySymbol + " " + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        tv_paid_amount_value.setText(currencySymbol + " " + jsonPurchaseOrder.computePaidAmount());
        tv_remaining_amount_value.setText(currencySymbol + " " + jsonPurchaseOrder.computeBalanceAmount());

    }

    private double calculateTotalPrice() {
        int amount = 0;
        for (int i = 0; i < jsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
            amount += 1 * jsonPurchaseOrder.getPurchaseOrderProducts().get(i).getProductPrice();
        }
        return amount;
    }

    public void checkProductWithZeroPrice() {
        isProductWithoutPrice = false;
        if (null != jsonPurchaseOrder && null != jsonPurchaseOrder.getPurchaseOrderProducts() && jsonPurchaseOrder.getPurchaseOrderProducts().size() > 0) {
            for (JsonPurchaseOrderProduct jpop :
                    jsonPurchaseOrder.getPurchaseOrderProducts()) {
                if (jpop.getProductPrice() == 0) {
                    isProductWithoutPrice = true;
                    break;
                }
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
    public void paymentProcessResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        dismissProgress();
        if (null != jsonPurchaseOrder) {
            if (jsonPurchaseOrder.getPaymentStatus() == PaymentStatusEnum.PA ||
                    jsonPurchaseOrder.getPaymentStatus() == PaymentStatusEnum.MP) {
                this.jsonPurchaseOrder = jsonPurchaseOrder;
                updateUI();
                new CustomToast().showToast(OrderDetailActivity.this, "Payment updated successfully");
                if (null != updateWholeList) {
                    updateWholeList.updateWholeList();
                }
            }
        }
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        dismissProgress();
        if (null != jsonPurchaseOrderList) {
            Log.v("order data:", jsonPurchaseOrderList.toString());
            finish();
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
        }
    }

    @Override
    public void purchaseOrderError() {
        dismissProgress();
    }

    @Override
    public void modifyOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        dismissProgress();
        if (null != jsonPurchaseOrder) {
            this.jsonPurchaseOrder = jsonPurchaseOrder;
            updateUI();
            Log.v("modify order data:", jsonPurchaseOrder.toString());
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
        }
    }

    @Override
    public void orderProcessedResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        dismissProgress();
        if (null != jsonPurchaseOrderList && jsonPurchaseOrderList.getPurchaseOrders().size() > 0) {
            jsonPurchaseOrder = jsonPurchaseOrderList.getPurchaseOrders().get(0);
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
    public void orderProcessedError() {
        dismissProgress();
    }

    @Override
    public void receiptInfoResponse(Receipt receipt) {
        try {
            Log.e("Data", receipt.toString());
            ReceiptGeneratorPDF receiptGeneratorPDF = new ReceiptGeneratorPDF(OrderDetailActivity.this);
            receiptGeneratorPDF.createPdf(receipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dismissProgress();
    }
}
