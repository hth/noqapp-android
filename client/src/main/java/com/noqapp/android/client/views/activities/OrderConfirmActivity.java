package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.PurchaseOrderApiCall;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.presenter.PurchaseOrderPresenter;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.OrderDetail;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.FabricEvents;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.presenter.CashFreeNotifyPresenter;
import com.noqapp.android.common.utils.CommonHelper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;

public class OrderConfirmActivity extends BaseActivity implements PurchaseOrderPresenter, ActivityCommunicator, CFClientInterface, CashFreeNotifyPresenter {

    private PurchaseOrderApiCall purchaseOrderApiCall;
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
    private TextView tv_payment_status, tv_total_amt_paid, tv_total_amt_paid_label, tv_total_amt_remain;
    private Button btn_pay_now;
    private boolean isPayClick = false;
    private RelativeLayout rl_amount_remaining;
    private boolean isProductWithoutPrice = false;

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
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_total_amt_paid = findViewById(R.id.tv_total_amt_paid);
        tv_total_amt_paid_label = findViewById(R.id.tv_total_amt_paid_label);
        tv_total_amt_remain = findViewById(R.id.tv_total_amt_remain);
        rl_amount_remaining = findViewById(R.id.rl_amount_remaining);

        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_address = findViewById(R.id.tv_address);
        btn_cancel_order = findViewById(R.id.btn_cancel_order);
        btn_pay_now = findViewById(R.id.btn_pay_now);
        btn_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != jsonPurchaseOrder && (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.VB || jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.PO)) {
                    if (isProductWithoutPrice) {
                        Toast.makeText(OrderConfirmActivity.this, "Merchant have not set the price of the product.Hence payment cann't be proceed", Toast.LENGTH_LONG).show();
                    } else {
                        if (NoQueueBaseActivity.isEmailVerified()) {
                            if (LaunchActivity.getLaunchActivity().isOnline()) {
                                progressDialog.show();
                                progressDialog.setMessage("Starting payment process..");
                                purchaseOrderApiCall.payNow(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                                isPayClick = true;
                            }
                        } else {
                            Toast.makeText(OrderConfirmActivity.this, "Email is mandatory. Please add and verify it", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        LaunchActivity.getLaunchActivity().activityCommunicator = this;
        initActionsViews(true);
        purchaseOrderApiCall = new PurchaseOrderApiCall(this);
        tv_toolbar_title.setText(getString(R.string.screen_order_confirm));
        tv_store_name.setText(getIntent().getExtras().getString(IBConstant.KEY_STORE_NAME));
        tv_address.setText(getIntent().getExtras().getString(IBConstant.KEY_STORE_ADDRESS));
        codeQR = getIntent().getExtras().getString(IBConstant.KEY_CODE_QR);
        currentServing = getIntent().getExtras().getInt("currentServing");
        if (getIntent().getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
            tv_toolbar_title.setText(getString(R.string.order_details));
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                progressDialog.setMessage("Fetching order details in progress..");
                int token = getIntent().getExtras().getInt("token");
                purchaseOrderApiCall.orderDetail(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new OrderDetail().setCodeQR(codeQR).setToken(token));
                if (AppUtilities.isRelease()) {
                    Answers.getInstance().logCustom(new CustomEvent(FabricEvents.EVENT_PLACE_ORDER)
                            .putCustomAttribute("Order Id", jsonPurchaseOrder.getTransactionId()));
                }
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
                        purchaseOrderApiCall.cancelOrder(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);

                        if (AppUtilities.isRelease()) {
                            Answers.getInstance().logCustom(new CustomEvent(FabricEvents.EVENT_CANCEL_ORDER)
                                    .putCustomAttribute("Order Id", jsonPurchaseOrder.getTransactionId()));
                        }
                    } else {
                        ShowAlertInformation.showNetworkDialog(OrderConfirmActivity.this);
                    }
                }
            }
        });
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

    private void updateUI() {
        String currencySymbol = getIntent().getExtras().getString(AppUtilities.CURRENCY_SYMBOL);
        tv_tax_amt.setText(currencySymbol + "0.00");
        if (jsonPurchaseOrder.getBusinessType() == BusinessTypeEnum.PH) {   // to avoid crash it is added for  Pharmacy order place from merchant side directly
            jsonPurchaseOrder.setOrderPrice("0");
        }
        tv_due_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        tv_total_order_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            tv_payment_status.setText("Paid via: " + jsonPurchaseOrder.getPaymentMode().getDescription());
        } else {
            tv_payment_status.setText("Payment status: " + jsonPurchaseOrder.getPaymentStatus().getDescription());
        }

        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            rl_amount_remaining.setVisibility(View.GONE);
            tv_total_amt_paid.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
            tv_total_amt_remain.setText(currencySymbol + "0.00");
            tv_total_amt_paid_label.setText(getString(R.string.total_amount_paid));
        } else if (PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
            rl_amount_remaining.setVisibility(View.VISIBLE);
            tv_total_amt_paid.setText(currencySymbol + "" + CommonHelper.displayPrice(jsonPurchaseOrder.getPartialPayment()));
            tv_total_amt_remain.setText(currencySymbol + (new BigDecimal(jsonPurchaseOrder.getOrderPrice()).subtract(new BigDecimal(jsonPurchaseOrder.getPartialPayment())).divide(new BigDecimal(100)));
            tv_total_amt_paid_label.setText("Total Amount Paid (In Cash):");
        } else {
            tv_total_amt_paid.setText(currencySymbol + "0.00");
            rl_amount_remaining.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < oldjsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = oldjsonPurchaseOrder.getPurchaseOrderProducts().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_total_price = inflatedLayout.findViewById(R.id.tv_total_price);
            tv_title.setText(jsonPurchaseOrderProduct.getProductName() + " " + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrderProduct.getProductPrice()) + " x " + String.valueOf(jsonPurchaseOrderProduct.getProductQuantity()));
            tv_total_price.setText(currencySymbol + new BigDecimal(jsonPurchaseOrderProduct.getProductPrice()).multiply(new BigDecimal(jsonPurchaseOrderProduct.getProductQuantity()).divide(new BigDecimal(100));
            if (jsonPurchaseOrder.getBusinessType() == BusinessTypeEnum.PH) {
                //added for  Pharmacy order place from merchant side directly
                findViewById(R.id.ll_amount).setVisibility(View.GONE);
                tv_total_price.setVisibility(View.GONE);
            }
            ll_order_details.addView(inflatedLayout);
        }
        int currentTemp = currentServing == -1 ? jsonPurchaseOrder.getServingNumber() : currentServing;
        tv_serving_no.setText(jsonPurchaseOrder.getToken() - currentTemp <= 0 ? String.valueOf(jsonPurchaseOrder.getToken()) : String.valueOf(currentTemp));
        btn_pay_now.setVisibility(View.GONE);
        switch (jsonPurchaseOrder.getPresentOrderState()) {
            case OP:
                tv_status.setText("Order being prepared");
                break;
            case PO:
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getDescription());
                if (jsonPurchaseOrder.getPaymentStatus() == PaymentStatusEnum.MP) {
                    btn_pay_now.setVisibility(View.VISIBLE);
                } else {
                    btn_pay_now.setVisibility(View.GONE);
                }
                break;
            case RD:
            case RP:
            case OD:
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getDescription());
                break;
            case VB: {
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getDescription());
                btn_pay_now.setVisibility(View.VISIBLE);
            }
            break;
            default:
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getDescription());
        }
        tv_token.setText(String.valueOf(jsonPurchaseOrder.getToken()));
        tv_estimated_time.setText(getString(R.string.will_be_served, "30 Min *"));
        if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.CO) {
            btn_cancel_order.setVisibility(View.GONE);
        }
        checkProductWithZeroPrice();
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
            if (isPayClick) {
                triggerOnlinePayment();
                isPayClick = false;
            }
        } else {
            //Show error
        }
        dismissProgress();
    }

    @Override
    public void payCashResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        // implementation not required here
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
            tv_serving_no.setText(jq.getToken() - currentTemp <= 0 ? String.valueOf(jsonPurchaseOrder.getToken()) : String.valueOf(currentTemp));
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

    private void triggerOnlinePayment() {
        String token = jsonPurchaseOrder.getJsonResponseWithCFToken().getCftoken();
        String stage = Constants.stage;
        String appId = Constants.appId;
        String orderId = jsonPurchaseOrder.getTransactionId();
        String orderAmount = jsonPurchaseOrder.getJsonResponseWithCFToken().getOrderAmount();
        String orderNote = "Test Order";
        String customerName = LaunchActivity.getUserName();
        String customerPhone = LaunchActivity.getPhoneNo();
        String customerEmail = LaunchActivity.getActualMail();

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_APP_ID, appId);
        params.put(PARAM_ORDER_ID, orderId);
        params.put(PARAM_ORDER_AMOUNT, orderAmount);
        params.put(PARAM_ORDER_NOTE, orderNote);
        params.put(PARAM_CUSTOMER_NAME, customerName);
        params.put(PARAM_CUSTOMER_PHONE, customerPhone);
        params.put(PARAM_CUSTOMER_EMAIL, customerEmail);

        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
        cfPaymentService.setOrientation(0);
        cfPaymentService.setConfirmOnExit(true);
        cfPaymentService.doPayment(this, params, token, this, stage);

    }

    @Override
    public void onSuccess(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Success");
        for (Map.Entry entry : map.entrySet()) {
            Log.e("Payment success", entry.getKey() + " " + entry.getValue());
        }
        purchaseOrderApiCall.setCashFreeNotifyPresenter(this);
        JsonCashfreeNotification jsonCashfreeNotification = new JsonCashfreeNotification();
        jsonCashfreeNotification.setTxMsg(map.get("txMsg"));
        jsonCashfreeNotification.setTxTime(map.get("txTime"));
        jsonCashfreeNotification.setReferenceId(map.get("referenceId"));
        jsonCashfreeNotification.setPaymentMode(map.get("paymentMode"));
        jsonCashfreeNotification.setSignature(map.get("signature"));
        jsonCashfreeNotification.setOrderAmount(map.get("orderAmount"));
        jsonCashfreeNotification.setTxStatus(map.get("txStatus"));
        jsonCashfreeNotification.setOrderId(map.get("orderId"));
        purchaseOrderApiCall.cashFreeNotify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonCashfreeNotification);
    }

    @Override
    public void onFailure(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Failure");
        Toast.makeText(this, "Transaction Failed", Toast.LENGTH_LONG).show();
        isPayClick = false;
    }

    @Override
    public void onNavigateBack() {
        Log.e("User Navigate Back", "Back without payment");
        Toast.makeText(this, "You canceled the transaction.Please try again", Toast.LENGTH_LONG).show();
        isPayClick = false;
    }

    @Override
    public void cashFreeNotifyResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        this.jsonPurchaseOrder = jsonPurchaseOrder;
        oldjsonPurchaseOrder = jsonPurchaseOrder;
        updateUI();
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            Toast.makeText(this, "Order placed successfully.", Toast.LENGTH_LONG).show();
            NoQueueMessagingService.subscribeTopics(getIntent().getExtras().getString("topic"));
        } else {
            Toast.makeText(this, jsonPurchaseOrder.getTransactionMessage(), Toast.LENGTH_LONG).show();
        }

    }

}
