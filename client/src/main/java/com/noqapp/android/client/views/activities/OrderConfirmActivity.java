package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.cache.Cache;
import com.google.firebase.messaging.FirebaseMessaging;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.api.PurchaseOrderApiImpl;
import com.noqapp.android.client.presenter.PurchaseOrderPresenter;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.OrderDetail;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.GeoHashUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.fragments.MapFragment;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.client.views.version_2.HomeActivity;
import com.noqapp.android.client.views.version_2.db.helper_models.ForegroundNotificationModel;
import com.noqapp.android.client.views.version_2.viewmodels.AfterJoinOrderViewModel;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.presenter.CashFreeNotifyPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.ProductUtils;
import com.noqapp.android.common.utils.TextToSpeechHelper;
import com.xw.repo.BubbleSeekBar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;
import static com.google.common.cache.CacheBuilder.newBuilder;

public class OrderConfirmActivity extends BaseActivity implements PurchaseOrderPresenter,
        ActivityCommunicator, CFClientInterface, CashFreeNotifyPresenter {

    private PurchaseOrderApiImpl purchaseOrderApiImpl;
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
    private String displayCurrentServing;
    private TextView tv_payment_status, tv_total_amt_paid, tv_total_amt_paid_label, tv_total_amt_remain;
    private TextView tv_grand_total_amt;
    private TextView tv_coupon_discount_amt;
    private Button btn_pay_now;
    private boolean isPayClick = false;
    private RelativeLayout rl_amount_remaining;
    private boolean isProductWithoutPrice = false;
    private BubbleSeekBar bsb_order_status;
    private JsonTokenAndQueue jsonTokenAndQueue;

    private AfterJoinOrderViewModel afterJoinOrderViewModel;
    private TextToSpeechHelper textToSpeechHelper;
    private final Cache<String, ArrayList<String>> cacheMsgIds = newBuilder().maximumSize(1).build();
    private final String MSG_IDS = "messageIds";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        afterJoinOrderViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(AfterJoinOrderViewModel.class);
        textToSpeechHelper = new TextToSpeechHelper(this);

        observeValues();

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
        tv_coupon_discount_amt = findViewById(R.id.tv_coupon_discount_amt);
        tv_grand_total_amt = findViewById(R.id.tv_grand_total_amt);
        bsb_order_status = findViewById(R.id.bsb_order_status);
        bsb_order_status.getConfigBuilder().sectionCount(PurchaseOrderStateEnum.HD.size()).build();
        bsb_order_status.setCustomSectionTextArray((sectionCount, array) -> {
            array.clear();
            int i = 0;
            for (PurchaseOrderStateEnum value : PurchaseOrderStateEnum.HD) {
                array.put(i, value.getFriendlyDescription());
            }
            return array;
        });
        bsb_order_status.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                int color = ContextCompat.getColor(OrderConfirmActivity.this, R.color.colorAccent);
                bubbleSeekBar.setSecondTrackColor(color);
                bubbleSeekBar.setThumbColor(color);
                bubbleSeekBar.setBubbleColor(color);
            }
        });
        bsb_order_status.setOnTouchListener((view, motionEvent) -> true);
        TextView tv_view_receipt = findViewById(R.id.tv_view_receipt);
        tv_view_receipt.setOnClickListener(v -> {
            showReceiptDialog(oldjsonPurchaseOrder.getPurchaseOrderProducts());
        });

        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_address = findViewById(R.id.tv_address);
        btn_cancel_order = findViewById(R.id.btn_cancel_order);
        btn_pay_now = findViewById(R.id.btn_pay_now);
        btn_pay_now.setOnClickListener((View v) -> {
            if (null != jsonPurchaseOrder && (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.VB || jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.PO)) {
                if (isProductWithoutPrice) {
                    new CustomToast().showToast(
                            OrderConfirmActivity.this,
                            "Payment cannot proceed as business has not set the price of the product");
                } else {
                    if (NoQueueClientApplication.isEmailVerified()) {
                        if (isOnline()) {
                            showProgress();
                            setProgressMessage("Starting payment process..");
                            setProgressCancel(false);
                            purchaseOrderApiImpl.payNow(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                            isPayClick = true;
                        }
                    } else {
                        new CustomToast().showToast(
                                OrderConfirmActivity.this,
                                "To pay, email is mandatory. In your profile add and verify email.");
                    }
                }
            }
        });
        NoQueueClientApplication.activityCommunicator = this;
        initActionsViews(true);
        iv_home.setOnClickListener((View v) -> {
            Intent goToA = new Intent(OrderConfirmActivity.this, HomeActivity.class);
            startActivity(goToA);
        });
        purchaseOrderApiImpl = new PurchaseOrderApiImpl(this);
        tv_toolbar_title.setText(getString(R.string.screen_order_confirm));
        tv_store_name.setText(getIntent().getExtras().getString(IBConstant.KEY_STORE_NAME));
        tv_address.setText(getIntent().getExtras().getString(IBConstant.KEY_STORE_ADDRESS));

        jsonTokenAndQueue = (JsonTokenAndQueue) getIntent().getSerializableExtra(IBConstant.KEY_JSON_TOKEN_QUEUE);
        codeQR = getIntent().getExtras().getString(IBConstant.KEY_CODE_QR);
        currentServing = getIntent().getExtras().getInt("currentServing");
        displayCurrentServing = getIntent().getExtras().getString("displayCurrentServing");

        if (getIntent().getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
            tv_toolbar_title.setText(getString(R.string.order_details));
            if (isOnline()) {
                showProgress();
                setProgressMessage("Fetching order details in progress...");
                int token = getIntent().getExtras().getInt("token");
                purchaseOrderApiImpl.orderDetail(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new OrderDetail().setCodeQR(codeQR).setToken(token));
                if (AppUtils.isRelease()) {
                    if (null != jsonPurchaseOrder && null != jsonPurchaseOrder.getTransactionId()) {
                        Bundle params = new Bundle();
                        params.putString("Order_Id", jsonPurchaseOrder.getTransactionId());
                        NoQueueClientApplication.getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_PLACE_ORDER, params);
                    }
                }
            } else {
                ShowAlertInformation.showNetworkDialog(OrderConfirmActivity.this);
            }
        } else {
            jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("data");
            oldjsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("oldData");
            updateUI();
        }
        actionbarBack.setOnClickListener((View v) -> {
            NoQueueClientApplication.activityCommunicator = null;
            iv_home.performClick();
        });
        btn_cancel_order.setOnClickListener((View v) -> {
            ShowCustomDialog showDialog = new ShowCustomDialog(OrderConfirmActivity.this, true);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    if (null != jsonPurchaseOrder) {
                        if (null == jsonPurchaseOrder.getTransactionVia()) {
                            cancelOrder();
                        } else {
                            switch (jsonPurchaseOrder.getTransactionVia()) {
                                case I:
                                    cancelOrder();
                                    break;
                                case E:
                                    cancelOrder();
                                    break;
                                case U:
                                    cancelOrder();
                                    break;
                            }
                        }
                    }
                }

                @Override
                public void btnNegativeClick() {
                    //Do nothing
                }
            });
            showDialog.displayDialog("Cancel Order", "Do you want to cancel the order?");
        });
    }

    private void cancelOrder() {
        if (isOnline()) {
            showProgress();
            setProgressMessage("Order cancel in progress...");
            purchaseOrderApiImpl.cancelOrder(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);

            if (AppUtils.isRelease()) {
                if (null != jsonPurchaseOrder && null != jsonPurchaseOrder.getTransactionId()) {
                    Bundle params = new Bundle();
                    params.putString("Order_Id", jsonPurchaseOrder.getTransactionId());
                    NoQueueClientApplication.getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_CANCEL_ORDER, params);
                }
            }
        } else {
            ShowAlertInformation.showNetworkDialog(OrderConfirmActivity.this);
        }
    }

    public void checkProductWithZeroPrice() {
        isProductWithoutPrice = false;
        if (null != jsonPurchaseOrder && null != jsonPurchaseOrder.getPurchaseOrderProducts() && jsonPurchaseOrder.getPurchaseOrderProducts().size() > 0) {
            for (JsonPurchaseOrderProduct jpop : jsonPurchaseOrder.getPurchaseOrderProducts()) {
                if (jpop.getProductPrice() == 0) {
                    isProductWithoutPrice = true;
                    break;
                }
            }
        }
    }

    private void updateUI() {
        String currencySymbol = getIntent().getExtras().getString(AppUtils.CURRENCY_SYMBOL);
        tv_tax_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getTax()));
        if (jsonPurchaseOrder.getBusinessType() == BusinessTypeEnum.PH) {   // to avoid crash it is added for Pharmacy order place from merchant side directly
            jsonPurchaseOrder.setOrderPrice("0");
        }
        tv_due_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.total()));
        tv_total_order_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        tv_grand_total_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.grandTotal()));
        tv_coupon_discount_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            tv_payment_status.setText("Paid via: " + jsonPurchaseOrder.getPaymentMode().getDescription());
        } else {
            tv_payment_status.setText("Payment status: " + jsonPurchaseOrder.getPaymentStatus().getDescription());
        }

        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            rl_amount_remaining.setVisibility(View.GONE);
            tv_total_amt_paid.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.total()));
            tv_total_amt_remain.setText(currencySymbol + "0.00");
            tv_total_amt_paid_label.setText(getString(R.string.total_amount_paid));
        } else if (PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
            rl_amount_remaining.setVisibility(View.VISIBLE);
            tv_total_amt_paid.setText(currencySymbol + "" + CommonHelper.displayPrice(jsonPurchaseOrder.getPartialPayment()));
            tv_total_amt_remain.setText(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jsonPurchaseOrder.total()).subtract(new BigDecimal(jsonPurchaseOrder.getPartialPayment())).toString()));
            tv_total_amt_paid_label.setText("Total Amount Paid (In Cash):");
        } else {
            tv_total_amt_paid.setText(currencySymbol + "0.00");
            rl_amount_remaining.setVisibility(View.GONE);
        }

        for (int i = 0; i < oldjsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = oldjsonPurchaseOrder.getPurchaseOrderProducts().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_total_price = inflatedLayout.findViewById(R.id.tv_total_price);
            //tv_title.setText(jsonPurchaseOrderProduct.getProductName() + " " + AppUtils.getPriceWithUnits(jsonPurchaseOrderProduct.getJsonStoreProduct()) + " " + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrderProduct.getProductPrice()) + " x " + String.valueOf(jsonPurchaseOrderProduct.getProductQuantity()));
            tv_title.setText(jsonPurchaseOrderProduct.getProductName() + "\n" + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrderProduct.getProductPrice()) + " x " + jsonPurchaseOrderProduct.getProductQuantity());
            tv_total_price.setText(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jsonPurchaseOrderProduct.getProductPrice()).multiply(new BigDecimal(jsonPurchaseOrderProduct.getProductQuantity())).toString()));
            if (jsonPurchaseOrder.getBusinessType() == BusinessTypeEnum.PH) {
                //added for  Pharmacy order place from merchant side directly
                findViewById(R.id.ll_amount).setVisibility(View.GONE);
                tv_total_price.setVisibility(View.GONE);
            }
            ll_order_details.addView(inflatedLayout);
        }
        int currentTemp = currentServing == -1 ? jsonPurchaseOrder.getServingNumber() : currentServing;
        tv_serving_no.setText(jsonPurchaseOrder.getToken() - currentTemp <= 0 ? String.valueOf(jsonPurchaseOrder.getDisplayToken()) : displayCurrentServing);
        btn_pay_now.setVisibility(View.GONE);
        switch (jsonPurchaseOrder.getPresentOrderState()) {
            case VB: {
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
                btn_pay_now.setVisibility(View.VISIBLE);
            }
            break;
            case PO:
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
                if (jsonPurchaseOrder.getPaymentStatus() == PaymentStatusEnum.MP) {
                    btn_pay_now.setVisibility(View.VISIBLE);
                } else {
                    btn_pay_now.setVisibility(View.GONE);
                }
                bsb_order_status.setProgress(1);
                break;
            case OP: {
                bsb_order_status.setProgress(2);
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
                btn_cancel_order.setVisibility(View.GONE);
            }
            break;
            case PR: {
                bsb_order_status.setProgress(3);
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
                btn_cancel_order.setVisibility(View.GONE);
            }
            break;
            case RP: {
                bsb_order_status.setProgress(4);
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
                btn_cancel_order.setVisibility(View.GONE);
            }
            break;
            case RD:
            case OW:
            case LO:
            case FD:
            case DA:
            case OD:
            case CO:
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
                btn_cancel_order.setVisibility(View.GONE);
                break;
            default:
                tv_status.setText(jsonPurchaseOrder.getPresentOrderState().getFriendlyDescription());
        }
        tv_token.setText(jsonPurchaseOrder.getDisplayToken());
        tv_estimated_time.setText(getString(R.string.will_be_served, "30 Min *"));

        //TODO   Revert After Corona crisis
        tv_estimated_time.setVisibility(View.INVISIBLE);
        //
        LatLng source = new LatLng(NoQueueClientApplication.getLocationPreference().getLatitude(), NoQueueClientApplication.getLocationPreference().getLongitude());
        String geoHash = getIntent().getStringExtra("GeoHash");
        LatLng destination = new LatLng(GeoHashUtils.decodeLatitude(geoHash), GeoHashUtils.decodeLongitude(geoHash));
        replaceFragmentWithoutBackStack(R.id.frame_map, MapFragment.getInstance(source, destination));
        checkProductWithZeroPrice();

        if (!getIntent().getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
            closeKioskScreen();
        }

        afterJoinOrderViewModel.getCurrentQueueObject(jsonPurchaseOrder.getCodeQR(), String.valueOf(jsonPurchaseOrder.getToken()));
        afterJoinOrderViewModel.getCurrentQueueObjectLiveData().observe(this, jtk -> {
            if (jtk == null) {
                jsonTokenAndQueue.setToken(jsonPurchaseOrder.getToken());
                jsonTokenAndQueue.setDisplayToken(jsonTokenAndQueue.getDisplayServingNumber().substring(0, 1) + (100 + jsonPurchaseOrder.getToken()));
                afterJoinOrderViewModel.insertTokenAndQueue(jsonTokenAndQueue);
            }
        });
    }

    @Override
    public void onBackPressed() {
        iv_home.performClick();
        NoQueueClientApplication.activityCommunicator = null;
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
                new CustomToast().showToast(this, "Order cancelled successfully");
                iv_home.performClick();
            } else {
                new CustomToast().showToast(this, "Failed to cancel order");
            }
        } else {
            //Show error
        }
        dismissProgress();
    }

    @Override
    public void purchaseOrderActivateResponse(JsonPurchaseOrderHistorical jsonPurchaseOrderHistorical) {
        // implementation not required here
        dismissProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NoQueueClientApplication.activityCommunicator = null;
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
                        tv_status.setText(jq.getPurchaseOrderState().getFriendlyDescription());
                        break;
                    default:
                        tv_status.setText(jq.getPurchaseOrderState().getFriendlyDescription());
                }
            }
            int currentTemp = currentServing == -1 ? jq.getServingNumber() : currentServing;
            tv_serving_no.setText(jq.getToken() - currentTemp <= 0 ? String.valueOf(jsonPurchaseOrder.getDisplayToken()) : jq.getDisplayServingNumber());
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
        String cashfreeStage = BuildConfig.CASHFREE_STAGE;
        String appId = BuildConfig.CASHFREE_APP_ID;
        String orderId = jsonPurchaseOrder.getTransactionId();
        String orderAmount = jsonPurchaseOrder.getJsonResponseWithCFToken().getOrderAmount();
        String orderNote = "Test Order";
        String customerName = NoQueueClientApplication.getCustomerNameWithQid(NoQueueClientApplication.getUserName(), NoQueueClientApplication.getUserProfile().getQueueUserId());
        String customerPhone = NoQueueClientApplication.getOfficePhoneNo();
        String customerEmail = NoQueueClientApplication.getOfficeMail();

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
        cfPaymentService.doPayment(this, params, token, this, cashfreeStage);
    }

    @Override
    public void onSuccess(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Success");
        for (Map.Entry entry : map.entrySet()) {
            Log.e("Payment success", entry.getKey() + " " + entry.getValue());
        }
        purchaseOrderApiImpl.setCashFreeNotifyPresenter(this);
        JsonCashfreeNotification jsonCashfreeNotification = new JsonCashfreeNotification();
        jsonCashfreeNotification.setTxMsg(map.get("txMsg"));
        jsonCashfreeNotification.setTxTime(map.get("txTime"));
        jsonCashfreeNotification.setReferenceId(map.get("referenceId"));
        jsonCashfreeNotification.setPaymentMode(map.get("paymentMode"));
        jsonCashfreeNotification.setSignature(map.get("signature"));
        jsonCashfreeNotification.setOrderAmount(map.get("orderAmount"));
        jsonCashfreeNotification.setTxStatus(map.get("txStatus"));
        jsonCashfreeNotification.setOrderId(map.get("orderId"));
        purchaseOrderApiImpl.cashFreeNotify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonCashfreeNotification);
    }

    @Override
    public void onFailure(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Failure");
        new CustomToast().showToast(this, "Transaction Failed");
        isPayClick = false;
    }

    @Override
    public void onNavigateBack() {
        Log.e("User Navigate Back", "Back without payment");
        new CustomToast().showToast(this, "Cancelled transaction. Please try again.");
        isPayClick = false;
    }

    @Override
    public void cashFreeNotifyResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        this.jsonPurchaseOrder = jsonPurchaseOrder;
        oldjsonPurchaseOrder = jsonPurchaseOrder;
        updateUI();
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            new CustomToast().showToast(this, "Order placed successfully.");
            FirebaseMessaging.getInstance().subscribeToTopic(getIntent().getExtras().getString("topic") + "_A");
            closeKioskScreen();
        } else {
            new CustomToast().showToast(this, jsonPurchaseOrder.getTransactionMessage());
        }
    }

    private void closeKioskScreen() {
        if (NoQueueClientApplication.isLockMode) {
            Handler handler = new Handler();

            handler.postDelayed(() -> {
                try {
                    iv_home.performClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, Constants.SCREEN_TIME_OUT);
        }
    }

    public void showReceiptDialog(List<JsonPurchaseOrderProduct> productList) {
        String currencySymbol = getIntent().getExtras().getString(AppUtils.CURRENCY_SYMBOL);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        builder.setTitle(null);
        View customDialogView = layoutInflater.inflate(R.layout.dialog_receipt_detail, null, false);
        LinearLayout ll_order_details = customDialogView.findViewById(R.id.ll_order_details);
        TextView tv_total_order_amt = customDialogView.findViewById(R.id.tv_total_order_amt);
        TextView tv_tax_amt = customDialogView.findViewById(R.id.tv_tax_amt);
        TextView tv_due_amt = customDialogView.findViewById(R.id.tv_due_amt);
        TextView tv_grand_total_amt = customDialogView.findViewById(R.id.tv_grand_total_amt);
        TextView tv_coupon_discount_label = customDialogView.findViewById(R.id.tv_coupon_discount_label);
        TextView tv_coupon_discount_amt = customDialogView.findViewById(R.id.tv_coupon_discount_amt);
        TextView tv_payment_status = customDialogView.findViewById(R.id.tv_payment_status);
        TextView tv_total_amt_paid = customDialogView.findViewById(R.id.tv_total_amt_paid);
        TextView tv_total_amt_paid_label = customDialogView.findViewById(R.id.tv_total_amt_paid_label);
        TextView tv_total_amt_remain = customDialogView.findViewById(R.id.tv_total_amt_remain);
        RelativeLayout rl_amount_remaining = customDialogView.findViewById(R.id.rl_amount_remaining);
        tv_tax_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getTax()));
        if (jsonPurchaseOrder.getBusinessType() == BusinessTypeEnum.PH) {   // to avoid crash it is added for  Pharmacy order place from merchant side directly
            jsonPurchaseOrder.setOrderPrice("0");
        }
        tv_due_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.total()));
        tv_total_order_amt.setText(currencySymbol + jsonPurchaseOrder.computeItemTotal());
        tv_grand_total_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.total()));

        if (jsonPurchaseOrder.getStoreDiscount() > 0) {
            tv_coupon_discount_label.setText(getString(R.string.discount_with_coupon, jsonPurchaseOrder.getJsonCoupon().getDiscountName()));
            tv_coupon_discount_amt.setText(Constants.DASH + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
        } else {
            tv_coupon_discount_label.setText(getString(R.string.discount));
            tv_coupon_discount_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
        }

        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            tv_payment_status.setText("Paid via: " + jsonPurchaseOrder.getPaymentMode().getDescription());
        } else {
            tv_payment_status.setText("Payment status: " + jsonPurchaseOrder.getPaymentStatus().getDescription());
        }

        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            rl_amount_remaining.setVisibility(View.GONE);
            tv_total_amt_paid.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.total()));
            tv_total_amt_remain.setText(currencySymbol + "0.00");
            tv_total_amt_paid_label.setText(getString(R.string.total_amount_paid));
        } else if (PaymentStatusEnum.MP == jsonPurchaseOrder.getPaymentStatus()) {
            rl_amount_remaining.setVisibility(View.VISIBLE);
            tv_total_amt_paid.setText(currencySymbol + "" + CommonHelper.displayPrice(jsonPurchaseOrder.getPartialPayment()));
            tv_total_amt_remain.setText(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jsonPurchaseOrder.total()).subtract(new BigDecimal(jsonPurchaseOrder.getPartialPayment())).toString()));
            tv_total_amt_paid_label.setText("Total Amount Paid (In Cash):");
        } else {
            tv_total_amt_paid.setText(currencySymbol + "0.00");
            rl_amount_remaining.setVisibility(View.GONE);
        }
        for (int i = 0; i < productList.size(); i++) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = productList.get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_total_price = inflatedLayout.findViewById(R.id.tv_total_price);
            TextView tv_price = inflatedLayout.findViewById(R.id.tv_price);
            TextView tv_discounted_price = inflatedLayout.findViewById(R.id.tv_discounted_price);
            TextView tv_product_quantity = inflatedLayout.findViewById(R.id.tv_product_quantity);
            tv_title.setText(jsonPurchaseOrderProduct.getProductName());
            tv_total_price.setText(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jsonPurchaseOrderProduct.getProductPrice()).multiply(new BigDecimal(jsonPurchaseOrderProduct.getProductQuantity())).toString()));
            tv_price.setText(currencySymbol + jsonPurchaseOrderProduct.getDisplayPrice());
            tv_product_quantity.setText(" x " + jsonPurchaseOrderProduct.getProductQuantity());
            tv_discounted_price.setText(currencySymbol + ProductUtils.calculateDiscountPrice(jsonPurchaseOrderProduct.getDisplayPrice(), jsonPurchaseOrderProduct.getDisplayDiscount()));
            if (jsonPurchaseOrderProduct.getProductDiscount() > 0) {
                tv_price.setPaintFlags(tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv_discounted_price.setVisibility(View.VISIBLE);
                tv_total_price.setText(currencySymbol + ProductUtils.calculateDiscountPrice(jsonPurchaseOrderProduct.getDisplayPrice(), jsonPurchaseOrderProduct.getDisplayDiscount()));
            } else {
                tv_price.setPaintFlags(tv_price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tv_discounted_price.setVisibility(View.GONE);
                tv_total_price.setText(currencySymbol + jsonPurchaseOrderProduct.getDisplayPrice());
            }
            if (jsonPurchaseOrder.getBusinessType() == BusinessTypeEnum.PH) {
                //added for  Pharmacy order place from merchant side directly
                findViewById(R.id.ll_amount).setVisibility(View.GONE);
                tv_total_price.setVisibility(View.GONE);
            }
            ll_order_details.addView(inflatedLayout);
        }
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        TextView tv_close = customDialogView.findViewById(R.id.tv_close);
        tv_close.setOnClickListener(v -> mAlertDialog.dismiss());
        mAlertDialog.show();
    }

    private void observeValues() {
        afterJoinOrderViewModel.getForegroundNotificationLiveData().observe(this, foregroundNotificationModel -> {
            if (foregroundNotificationModel != null) {
                handleBuzzer(foregroundNotificationModel);
            }
        });

        afterJoinOrderViewModel.getReviewData(Constants.NotificationTypeConstant.FOREGROUND).observe(this, reviewData -> {
            if (reviewData != null && reviewData.getIsReviewShown().equals("-1"))
                startActivity(new Intent(OrderConfirmActivity.this, HomeActivity.class));
        });
    }

    private void handleBuzzer(ForegroundNotificationModel foregroundNotification) {
        afterJoinOrderViewModel.getCurrentQueueObjectList(foregroundNotification.getQrCode());

        afterJoinOrderViewModel.getCurrentQueueObjectListLiveData().observe(this, jsonTokenAndQueues -> {
            if (jsonTokenAndQueues != null) {
                for (JsonTokenAndQueue jtk : jsonTokenAndQueues) {
                    //TODO(vivek) this will not work as which token is being compared
                    if (jtk.getToken() - jtk.getServingNumber() <= 0) {
                        if (jtk.getPurchaseOrderState() == PurchaseOrderStateEnum.OP) {
                            tv_status.setText(getString(R.string.txt_order_being_prepared));
                        } else {
                            tv_status.setText(jtk.getPurchaseOrderState().getFriendlyDescription());
                        }
                    } else if (jtk.getServingNumber() == 0) {
                        tv_status.setText(getString(R.string.queue_not_started));
                    } else {
                        tv_status.setText(getString(R.string.serving_now) + " " + jtk.afterHowLongForDisplay());
                        //tv_status.setText("Its your turn!!!"); to be added when its there turn instead
                    }
                }
            }
        });

        if (foregroundNotification.getCurrentServing().equals(foregroundNotification.getUserCurrentToken())) {
            if (MessageOriginEnum.valueOf(foregroundNotification.getMessageOrigin()) == MessageOriginEnum.Q) {
                Intent blinkerIntent = new Intent(OrderConfirmActivity.this, BlinkerActivity.class);
                blinkerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(blinkerIntent);
                if (NoQueueClientApplication.isMsgAnnouncementEnable()) {
                    if (null != foregroundNotification.getJsonTextToSpeeches()) {
                        makeAnnouncement(foregroundNotification.getJsonTextToSpeeches(), foregroundNotification.getMsgId());
                    }
                }
            } else if (MessageOriginEnum.valueOf(foregroundNotification.getMessageOrigin()) == MessageOriginEnum.O) {
                switch (foregroundNotification.getPurchaseOrderStateEnum()) {
                    case RD:
                    case RP:
                        Intent blinkerIntent = new Intent(OrderConfirmActivity.this, BlinkerActivity.class);
                        blinkerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(blinkerIntent);
                        if (NoQueueClientApplication.isMsgAnnouncementEnable()) {
                            if (null != foregroundNotification.getJsonTextToSpeeches()) {
                                makeAnnouncement(foregroundNotification.getJsonTextToSpeeches(), foregroundNotification.getMsgId());
                            }
                        }
                        break;
                    default:
                        //Do nothing
                }
            }
        }

        afterJoinOrderViewModel.deleteForegroundNotification();
    }

    private void makeAnnouncement(List<JsonTextToSpeech> jsonTextToSpeeches, String msgId) {
        if (null == cacheMsgIds.getIfPresent(MSG_IDS)) {
            cacheMsgIds.put(MSG_IDS, new ArrayList<>());
        }

        ArrayList<String> msgIds = cacheMsgIds.getIfPresent(MSG_IDS);
        if (null == msgIds) {
            msgIds = new ArrayList<>();
        }
        if (!TextUtils.isEmpty(msgId) && !msgIds.contains(msgId)) {
            msgIds.add(msgId);
            cacheMsgIds.put(MSG_IDS, msgIds);
            textToSpeechHelper.makeAnnouncement(jsonTextToSpeeches);
        }
    }
}
