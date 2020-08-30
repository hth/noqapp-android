package com.noqapp.android.client.views.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ClientCouponApiCalls;
import com.noqapp.android.client.model.QueueApiAuthenticCall;
import com.noqapp.android.client.model.QueueApiUnAuthenticCall;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.presenter.AuthorizeResponsePresenter;
import com.noqapp.android.client.presenter.CashFreeNotifyQPresenter;
import com.noqapp.android.client.presenter.QueueJsonPurchaseOrderPresenter;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.QueueAuthorize;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonCoupon;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.CouponOnOrder;
import com.noqapp.android.common.beans.body.JoinQueue;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;
import com.noqapp.android.common.beans.payment.cashfree.JsonResponseWithCFToken;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.presenter.CouponApplyRemovePresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.squareup.picasso.Picasso;

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

/**
 * Created by chandra on 5/7/17.
 */
public class JoinActivity extends BaseActivity implements TokenPresenter, ResponsePresenter,
        ActivityCommunicator, CFClientInterface, CashFreeNotifyQPresenter,
        QueueJsonPurchaseOrderPresenter, CouponApplyRemovePresenter, AuthorizeResponsePresenter {
    private static final String TAG = JoinActivity.class.getSimpleName();
    private TextView tv_address;
    private TextView tv_mobile;
    private Button btn_cancel_queue;
    private TextView tv_name;
    private JsonToken jsonToken;
    private JsonTokenAndQueue jsonTokenAndQueue;
    private String codeQR;
    private String tokenValue;
    private String topic;
    private boolean isResumeFirst = true;
    private String queueUserId = "";
    private QueueApiUnAuthenticCall queueApiUnAuthenticCall;
    private QueueApiAuthenticCall queueApiAuthenticCall;
    private Button btn_pay;
    private CardView card_amount;
    private TextView tv_total_order_amt;
    private TextView tv_grand_total_amt;
    private TextView tv_coupon_amount;
    private TextView tv_coupon_name;
    private TextView tv_coupon_discount_amt;
    private CFPaymentService cfPaymentService;
    private JsonProfile jsonProfile;
    private RelativeLayout rl_apply_coupon, rl_coupon_applied;
    private String currencySymbol = "";
    private FrameLayout frame_coupon;
    private LinearLayout ll_order_details;
    private RelativeLayout rl_discount;
    private boolean isEnabledPayment;
    private CountDownTimer timer;
    private boolean isCancel = false;
    private boolean isPause = false;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        new InitPaymentGateway().execute();
        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_queue_name = findViewById(R.id.tv_queue_name);
        tv_address = findViewById(R.id.tv_address);
        tv_mobile = findViewById(R.id.tv_mobile);
        btn_cancel_queue = findViewById(R.id.btn_cancel_queue);
        ll_order_details = findViewById(R.id.ll_order_details);
        rl_discount = findViewById(R.id.rl_discount);
        btn_pay = findViewById(R.id.btn_pay);
        tv_coupon_amount = findViewById(R.id.tv_coupon_amount);
        tv_coupon_name = findViewById(R.id.tv_coupon_name);
        tv_coupon_discount_amt = findViewById(R.id.tv_coupon_discount_amt);
        tv_total_order_amt = findViewById(R.id.tv_total_order_amt);
        tv_grand_total_amt = findViewById(R.id.tv_grand_total_amt);
        TextView tv_hour_saved = findViewById(R.id.tv_hour_saved);
        TextView tv_add = findViewById(R.id.add_person);
        card_amount = findViewById(R.id.card_amount);
        tv_name = findViewById(R.id.tv_name);
        rl_apply_coupon = findViewById(R.id.rl_apply_coupon);
        rl_coupon_applied = findViewById(R.id.rl_coupon_applied);
        frame_coupon = findViewById(R.id.frame_coupon);
        frame_coupon.setVisibility(View.GONE);
        rl_apply_coupon.setOnClickListener((View v) -> {
            if (null != jsonToken && null != jsonToken.getJsonPurchaseOrder() && jsonToken.getJsonPurchaseOrder().isDiscountedPurchase()) {
                new CustomToast().showToast(JoinActivity.this, getString(R.string.discount_error));
            } else {
                Intent in = new Intent(JoinActivity.this, CouponsActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, codeQR);
                startActivityForResult(in, Constants.ACTIVITY_RESULT_BACK);
            }
        });
        TextView tv_remove_coupon = findViewById(R.id.tv_remove_coupon);
        tv_remove_coupon.setOnClickListener((View v) -> {
            ShowCustomDialog showDialog = new ShowCustomDialog(JoinActivity.this, true);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        showProgress();
                        setProgressMessage("Removing discount..");
                        // progressDialog.setCancelable(false);
                        // progressDialog.setCanceledOnTouchOutside(false);
                        ClientCouponApiCalls clientCouponApiCalls = new ClientCouponApiCalls();
                        clientCouponApiCalls.setCouponApplyRemovePresenter(JoinActivity.this);

                        CouponOnOrder couponOnOrder = new CouponOnOrder()
                                .setQueueUserId(jsonTokenAndQueue.getQueueUserId())
                                // .setCouponId(jsonCoupon.getCouponId())
                                .setTransactionId(jsonTokenAndQueue.getTransactionId());

                        clientCouponApiCalls.remove(UserUtils.getDeviceId(),
                                UserUtils.getEmail(),
                                UserUtils.getAuth(),
                                couponOnOrder);
                    } else {
                        ShowAlertInformation.showNetworkDialog(JoinActivity.this);
                    }
                }

                @Override
                public void btnNegativeClick() {
                    //Do nothing
                }
            });
            showDialog.displayDialog("Remove coupon", "Do you want to remove the coupon?");
        });
        ImageView iv_profile = findViewById(R.id.iv_profile);
        btn_cancel_queue.setOnClickListener((View v) -> {
            ShowCustomDialog showDialog = new ShowCustomDialog(JoinActivity.this, true);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    if (null != jsonTokenAndQueue) {
                        if (null == jsonTokenAndQueue.getJsonPurchaseOrder() || null == jsonTokenAndQueue.getJsonPurchaseOrder().getTransactionVia()) {
                            cancelQueue();
                        } else {
                            switch (jsonTokenAndQueue.getJsonPurchaseOrder().getTransactionVia()) {
                                case I:
                                    cancelQueue();
                                    break;
                                case E:
                                    cancelQueue();
                                    break;
                                case U:
                                    cancelQueue();
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
            showDialog.displayDialog("Cancel Queue", "Do you want to cancel the queue?");
        });
        btn_pay.setOnClickListener((View v) -> {
            //startTimer();
            if (null != timer) {
                timer.cancel();
            }

            if (new BigDecimal(jsonToken.getJsonPurchaseOrder().getOrderPrice()).intValue() > 0) {
                pay();
            } else {
                setProgressMessage("Starting token process...");
                showProgress();
                setProgressCancel(false);
                queueApiAuthenticCall.setCashFreeNotifyQPresenter(JoinActivity.this);
                JsonCashfreeNotification jsonCashfreeNotification = new JsonCashfreeNotification();
                jsonCashfreeNotification.setTxMsg(null);
                jsonCashfreeNotification.setTxTime(null);
                jsonCashfreeNotification.setReferenceId(null);
                jsonCashfreeNotification.setPaymentMode(null);  // cash
                jsonCashfreeNotification.setSignature(null);
                jsonCashfreeNotification.setOrderAmount(jsonToken.getJsonPurchaseOrder().getOrderPrice()); // amount
                jsonCashfreeNotification.setTxStatus("SUCCESS");   //SUCCESS
                jsonCashfreeNotification.setOrderId(jsonToken.getJsonPurchaseOrder().getTransactionId());   // transactionID
                queueApiAuthenticCall.cashFreeQNotify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonCashfreeNotification);
            }
        });
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.screen_qconfirm));
        queueApiUnAuthenticCall = new QueueApiUnAuthenticCall();
        queueApiAuthenticCall = new QueueApiAuthenticCall();
        queueApiAuthenticCall.setQueueJsonPurchaseOrderPresenter(this);
        queueApiAuthenticCall.setTokenPresenter(this);
        Intent bundle = getIntent();
        if (null != bundle) {
            jsonTokenAndQueue = (JsonTokenAndQueue) bundle.getSerializableExtra(IBConstant.KEY_JSON_TOKEN_QUEUE);
            isEnabledPayment = bundle.getBooleanExtra(IBConstant.KEY_IS_PAYMENT_ENABLE, false);
            if (null != jsonTokenAndQueue) {
                currencySymbol = AppUtils.getCurrencySymbol(jsonTokenAndQueue.getCountryShortName());
            }
            Log.d("AfterJoin bundle", jsonTokenAndQueue.toString());
            codeQR = bundle.getStringExtra(IBConstant.KEY_CODE_QR);
            topic = jsonTokenAndQueue.getTopic();
            tokenValue = String.valueOf(jsonTokenAndQueue.getToken());
            tv_store_name.setText(jsonTokenAndQueue.getBusinessName());
            tv_queue_name.setText(jsonTokenAndQueue.getDisplayName());
            tv_address.setText(jsonTokenAndQueue.getStoreAddress());
            queueUserId = bundle.getStringExtra("qUserId");
            List<JsonProfile> profileList = new ArrayList<>();
            if (UserUtils.isLogin()) {
                profileList = NoQueueBaseActivity.getAllProfileList();
            }
            if (!TextUtils.isEmpty(queueUserId)) {
                jsonProfile = AppUtils.getJsonProfileQueueUserID(queueUserId, profileList);
                tv_name.setText(jsonProfile.getName());
            }

            String imageUrl = bundle.getStringExtra(IBConstant.KEY_IMAGE_URL);
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get().load(imageUrl).
                        placeholder(getResources().getDrawable(R.drawable.profile_theme)).
                        error(getResources().getDrawable(R.drawable.profile_theme)).into(iv_profile);
            } else {
                Picasso.get().load(R.drawable.profile_theme).into(iv_profile);
            }
            actionbarBack.setOnClickListener((View v) -> {
                iv_home.performClick();
            });
            iv_home.setOnClickListener((View v) -> {
                Intent goToA = new Intent(JoinActivity.this, LaunchActivity.class);
                goToA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goToA);
            });
            switch (jsonTokenAndQueue.getBusinessType()) {
                case DO:
                case PH:
                    tv_add.setVisibility(View.VISIBLE);
                    tv_name.setVisibility(View.VISIBLE);
                    break;
                default:
                    tv_add.setVisibility(View.GONE);
                    tv_name.setVisibility(View.GONE);
            }
            String time = new AppUtils().formatTodayStoreTiming(this, jsonTokenAndQueue.getStartHour(), jsonTokenAndQueue.getEndHour());
            tv_hour_saved.setText(time);
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonTokenAndQueue.getCountryShortName(), jsonTokenAndQueue.getStorePhone()));
            tv_mobile.setOnClickListener((View v) -> {
                AppUtils.makeCall(JoinActivity.this, tv_mobile.getText().toString());
            });
            tv_address.setOnClickListener((View v) -> {
                AppUtils.openAddressInMap(JoinActivity.this, tv_address.getText().toString());
            });
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                if (isResumeFirst) {
                    setProgressMessage("Joining Queue..");
                    showProgress();
                    callQueue();
                }
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }

        }
    }

    private void startTimer() {
        //isPause = false;
        isCancel = false;
        if (null != timer) {
            timer.cancel();
        }
        Log.e("Start time", "");
        timer = new CountDownTimer(BuildConfig.TRANSACTION_TIMEOUT * 60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                //Some code
            }

            public void onFinish() {
                Log.e("End time", "");
                if (!isPause) {
                    onBackPressed();
                } else {
                    isCancel = true;
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    public void couponApplyResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        Log.e("coupon apply data: ", jsonPurchaseOrder.toString());
        jsonToken.getJsonPurchaseOrder().setOrderPrice(jsonPurchaseOrder.getOrderPrice());
        jsonToken.getJsonPurchaseOrder().setStoreDiscount(jsonPurchaseOrder.getStoreDiscount());
        jsonToken.getJsonPurchaseOrder().setCouponId(jsonPurchaseOrder.getCouponId());
        queueJsonPurchaseOrderResponse(jsonToken.getJsonPurchaseOrder());
        dismissProgress();
    }

    @Override
    public void couponRemoveResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        Log.e("coupon remove data: ", jsonPurchaseOrder.toString());
        new CustomToast().showToast(this, "Coupon removed successfully");
        jsonToken.getJsonPurchaseOrder().setOrderPrice(jsonPurchaseOrder.getOrderPrice());
        jsonToken.getJsonPurchaseOrder().setStoreDiscount(jsonPurchaseOrder.getStoreDiscount());
        jsonToken.getJsonPurchaseOrder().setCouponId(jsonPurchaseOrder.getCouponId());
        queueJsonPurchaseOrderResponse(jsonToken.getJsonPurchaseOrder());
        dismissProgress();
    }

    @Override
    public void authorizePresenterResponse(JsonResponse response) {
        if (null != alertDialog) {
            alertDialog.dismiss();
        }
        responsePresenterResponse(response);
    }

    @Override
    public void authorizePresenterError() {
        if (null != alertDialog) {
            alertDialog.dismiss();
        }
        responsePresenterResponse(null);
    }


    private class InitPaymentGateway extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
            cfPaymentService.setOrientation(0);
            cfPaymentService.setConfirmOnExit(true);
            return null;
        }

        protected void onPostExecute(String result) {
        }
    }

    @Override
    public void tokenPresenterResponse(JsonToken token) {
        Log.d(TAG, token.toString());
        this.jsonToken = token;
        tokenValue = String.valueOf(token.getToken());
        btn_cancel_queue.setEnabled(true);
        jsonTokenAndQueue.setServingNumber(token.getServingNumber());
        jsonTokenAndQueue.setToken(token.getToken());
        jsonTokenAndQueue.setDisplayToken(token.getDisplayToken());
        jsonTokenAndQueue.setQueueUserId(queueUserId);
        jsonTokenAndQueue.setTimeSlotMessage(jsonToken.getTimeSlotMessage());
        dismissProgress();
        if (UserUtils.isLogin()) {
            if (isEnabledPayment) {
                // do nothing
            } else {
                //navigate to after join screen
                navigateToAfterJoinScreen(token);
            }
        } else {
            //navigate to after join screen
            navigateToAfterJoinScreen(token);
        }
    }

    private void navigateToAfterJoinScreen(JsonToken jsonToken) {
        jsonTokenAndQueue.setServingNumber(jsonToken.getServingNumber());
        jsonTokenAndQueue.setToken(jsonToken.getToken());
        jsonTokenAndQueue.setDisplayToken(jsonToken.getDisplayToken());
        jsonTokenAndQueue.setQueueStatus(jsonToken.getQueueStatus());
        jsonTokenAndQueue.setServiceEndTime(jsonToken.getExpectedServiceBegin());
        jsonTokenAndQueue.setJsonPurchaseOrder(jsonToken.getJsonPurchaseOrder());
        //save data to DB
        TokenAndQueueDB.saveJoinQueueObject(jsonTokenAndQueue);
        NoQueueMessagingService.subscribeTopics(topic);
        Intent in = new Intent(this, AfterJoinActivity.class);
        in.putExtra(IBConstant.KEY_CODE_QR, jsonTokenAndQueue.getCodeQR());
        in.putExtra(IBConstant.KEY_FROM_LIST, false);
        in.putExtra(IBConstant.KEY_JSON_TOKEN_QUEUE, jsonTokenAndQueue);
        in.putExtra(Constants.ACTIVITY_TO_CLOSE, true);
        in.putExtra("qUserId", queueUserId);
        in.putExtra("imageUrl", getIntent().getStringExtra(IBConstant.KEY_IMAGE_URL));
        startActivityForResult(in, Constants.requestCodeAfterJoinQActivity);
    }

    @Override
    public void paidTokenPresenterResponse(JsonToken token) {
        this.jsonToken = token;
        if (null != token) {
            Log.d(TAG, token.toString());
            if (token.getJsonPurchaseOrder().getPresentOrderState() == PurchaseOrderStateEnum.VB) {
                queueJsonPurchaseOrderResponse(token.getJsonPurchaseOrder());
                tokenPresenterResponse(jsonToken);
            } else if (token.getJsonPurchaseOrder().getPresentOrderState() == PurchaseOrderStateEnum.PO) {
                new CustomToast().showToast(this, "You are already in the Queue");
                navigateToAfterJoinScreen(jsonToken);
            } else {
                new CustomToast().showToast(this, "Order failed.");
            }
        } else {
            //Show error
        }
        dismissProgress();
    }

    @Override
    public void unPaidTokenPresenterResponse(JsonToken token) {
        this.jsonToken = token;
        if (null != token) {
            Log.d(TAG, token.toString());
            if (token.getJsonPurchaseOrder().getPresentOrderState() == PurchaseOrderStateEnum.VB ||
                    token.getJsonPurchaseOrder().getPresentOrderState() == PurchaseOrderStateEnum.PO) {
                queueJsonPurchaseOrderResponse(token.getJsonPurchaseOrder());
                tokenPresenterResponse(jsonToken);
                btn_pay.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = setLayoutWidthParams(false);
                params.setMargins(0, 0, 20, 0);
                btn_pay.setLayoutParams(params);
                btn_cancel_queue.setLayoutParams(setLayoutWidthParams(false));
            } else {
                new CustomToast().showToast(this, "Order failed.");
            }
        } else {
            //Show error
        }
        dismissProgress();
    }

    @Override
    public void responsePresenterResponse(JsonResponse response) {
        NoQueueMessagingService.unSubscribeTopics(topic);
        TokenAndQueueDB.deleteTokenQueue(codeQR, tokenValue);
        iv_home.performClick();
        dismissProgress();
    }

    @Override
    public void responsePresenterError() {
        Log.d("", "responsePresenterError");
        dismissProgress();
    }

    private void cancelQueue() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            setProgressMessage("Canceling process...");
            showProgress();
            queueApiAuthenticCall.setResponsePresenter(this);
            queueApiAuthenticCall.cancelPayBeforeQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonToken);
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    private void callQueue() {
        if (codeQR != null) {
            if (UserUtils.isLogin()) {
                JsonProfile jp = NoQueueBaseActivity.getUserProfile();
                String qUserId;
                String guardianId = null;
                if (jp.getQueueUserId().equalsIgnoreCase(queueUserId)) {
                    qUserId = jp.getQueueUserId();
                } else {
                    qUserId = jsonProfile.getQueueUserId();
                    guardianId = jp.getQueueUserId();
                }
                JoinQueue joinQueue = new JoinQueue().setCodeQR(codeQR).setQueueUserId(qUserId).setGuardianQid(guardianId);
                if (isEnabledPayment) {
                    startTimer();
                    new CustomToast().showToast(
                            this,
                            "Please complete your transaction within " + BuildConfig.TRANSACTION_TIMEOUT + " minutes.");
                    queueApiAuthenticCall.payBeforeJoinQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), joinQueue);
                } else {
                    queueApiAuthenticCall.joinQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), joinQueue);
                }
            } else {
                queueApiUnAuthenticCall.setTokenPresenter(this);
                queueApiUnAuthenticCall.joinQueue(UserUtils.getDeviceId(), codeQR);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        /* Added to update the screen if app is in background & notification received */
        if (isResumeFirst) {
            isResumeFirst = false;
        }

        if (isCancel && isPause) {
            onBackPressed();
        } else {
            isPause = false;
        }
    }

    public String getCodeQR() {
        return codeQR;
    }


    public void setObject(JsonTokenAndQueue jq, String go_to) {
        // jsonTokenAndQueue = jq; removed to avoided the override of the data
        jsonTokenAndQueue.setServingNumber(jq.getServingNumber());
        jsonTokenAndQueue.setToken(jq.getToken());
        btn_cancel_queue.setEnabled(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean updateUI(String qrCode, JsonTokenAndQueue jq, String go_to) {
        if (codeQR.equals(qrCode) && tokenValue.equals(String.valueOf(jq.getServingNumber()))) {
            //updating the serving status
            setObject(jq, go_to);
            if (jq.afterHowLong() > 0) {
                return false;
            } else {
                return true;
            }
        } else
            return false;
    }

    @Override
    public void requestProcessed(String qrCode, String token) {
        if (codeQR.equals(qrCode) && tokenValue.equals(token)) {
            //remove the screen from stack
            returnResultBack();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (null != timer) {
            timer.cancel();
        }
        cancelQueue();
    }

    private void returnResultBack() {
        if (getIntent().getBooleanExtra(Constants.ACTIVITY_TO_CLOSE, false)) {
            Intent intent = new Intent();
            intent.putExtra(Constants.ACTIVITY_TO_CLOSE, true);
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, intent);
            } else {
                getParent().setResult(Activity.RESULT_OK, intent);
            }
        }
    }

    @Override
    public void onSuccess(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Success");
        for (Map.Entry entry : map.entrySet()) {
            Log.e("Payment success", entry.getKey() + " " + entry.getValue());
        }
        queueApiAuthenticCall.setCashFreeNotifyQPresenter(this);
        JsonCashfreeNotification jsonCashfreeNotification = new JsonCashfreeNotification();
        jsonCashfreeNotification.setTxMsg(map.get("txMsg"));
        jsonCashfreeNotification.setTxTime(map.get("txTime"));
        jsonCashfreeNotification.setReferenceId(map.get("referenceId"));
        jsonCashfreeNotification.setPaymentMode(map.get("paymentMode"));  // cash
        jsonCashfreeNotification.setSignature(map.get("signature"));
        jsonCashfreeNotification.setOrderAmount(map.get("orderAmount")); // amount
        jsonCashfreeNotification.setTxStatus(map.get("txStatus"));   //Success
        jsonCashfreeNotification.setOrderId(map.get("orderId"));   // transactionID
        queueApiAuthenticCall.cashFreeQNotify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonCashfreeNotification);
    }

    @Override
    public void onFailure(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Failure");
        new CustomToast().showToast(this, "Transaction Failed");
        finish();
    }

    @Override
    public void onNavigateBack() {
        Log.e("User Navigate Back", "Back without payment");
        new CustomToast().showToast(this, "Cancelled transaction. Please try again.");
        if (getIntent().getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
            // do nothing
        } else {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                setProgressMessage("Canceling token...");
                showProgress();
                queueApiAuthenticCall.setResponsePresenter(this);
                queueApiAuthenticCall.cancelPayBeforeQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonToken);
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }
    }

    private void triggerOnlinePayment() {
        if (NoQueueBaseActivity.isEmailVerified()) {
            String token = jsonToken.getJsonPurchaseOrder().getJsonResponseWithCFToken().getCftoken();
            String stage = BuildConfig.CASHFREE_STAGE;
            String appId = BuildConfig.CASHFREE_APP_ID;
            String orderId = jsonToken.getJsonPurchaseOrder().getTransactionId();
            String orderAmount = jsonToken.getJsonPurchaseOrder().getJsonResponseWithCFToken().getOrderAmount();
            // String orderAmount = CommonHelper.displayPrice(String.valueOf(Double.parseDouble(jsonToken.getJsonPurchaseOrder().getOrderPrice())));
            String orderNote = "Order: " + queueUserId;
            String customerName = LaunchActivity.getCustomerNameWithQid(tv_name.getText().toString(), queueUserId);
            String customerPhone = LaunchActivity.getOfficePhoneNo();
            String customerEmail = LaunchActivity.getOfficeMail();
            Map<String, String> params = new HashMap<>();
            params.put(PARAM_APP_ID, appId);
            params.put(PARAM_ORDER_ID, orderId);
            params.put(PARAM_ORDER_AMOUNT, orderAmount);
            params.put(PARAM_ORDER_NOTE, orderNote);
            params.put(PARAM_CUSTOMER_NAME, customerName);
            params.put(PARAM_CUSTOMER_PHONE, customerPhone);
            params.put(PARAM_CUSTOMER_EMAIL, customerEmail);
            cfPaymentService.doPayment(this, params, token, this, stage);
        } else {
            new CustomToast().showToast(this, "To pay, email is mandatory. In your profile add and verify email");
        }
    }

    @Override
    public void cashFreeNotifyQResponse(JsonToken jsonToken) {
        btn_pay.setVisibility(View.GONE);
        btn_cancel_queue.setLayoutParams(setLayoutWidthParams(true));
        if (PaymentStatusEnum.PA == jsonToken.getJsonPurchaseOrder().getPaymentStatus()) {
            new CustomToast().showToast(this, "Token generated successfully.");
            navigateToAfterJoinScreen(jsonToken);
        } else {
            new CustomToast().showToast(this, jsonToken.getJsonPurchaseOrder().getTransactionMessage());
        }
        dismissProgress();
    }

    @Override
    public void queueJsonPurchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        Log.e("respo: ", jsonPurchaseOrder.toString());
        btn_pay.setVisibility(View.VISIBLE);
        frame_coupon.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = setLayoutWidthParams(false);
        params.setMargins(0, 0, 20, 0);
        btn_pay.setLayoutParams(params);
        btn_cancel_queue.setLayoutParams(setLayoutWidthParams(false));
        this.jsonTokenAndQueue.setJsonPurchaseOrder(jsonPurchaseOrder);
        this.jsonTokenAndQueue.setTransactionId(jsonPurchaseOrder.getTransactionId());
        if (null == jsonToken) {
            jsonToken = new JsonToken();
        }
        jsonToken.setJsonPurchaseOrder(jsonPurchaseOrder);
        card_amount.setVisibility(View.VISIBLE);
        // tv_due_amt.setText(currencySymbol + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        tv_total_order_amt.setText(currencySymbol + jsonPurchaseOrder.computeFinalAmountWithDiscount());
        tv_grand_total_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        tv_coupon_amount.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
        tv_coupon_discount_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
        if (null != jsonPurchaseOrder.getJsonCoupon())
            tv_coupon_name.setText(jsonPurchaseOrder.getJsonCoupon().getDiscountName());
        ll_order_details.removeAllViews();
        for (int i = 0; i < jsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = jsonPurchaseOrder.getPurchaseOrderProducts().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_total_price = inflatedLayout.findViewById(R.id.tv_total_price);
            tv_title.setText(jsonPurchaseOrderProduct.getProductName() + " " + AppUtils.getPriceWithUnits(jsonPurchaseOrderProduct.getJsonStoreProduct()) + " " + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrderProduct.getProductPrice()) + " x " + String.valueOf(jsonPurchaseOrderProduct.getProductQuantity()));
            tv_total_price.setText(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jsonPurchaseOrderProduct.getProductPrice()).multiply(new BigDecimal(jsonPurchaseOrderProduct.getProductQuantity())).toString()));
            ll_order_details.addView(inflatedLayout);
        }
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            btn_pay.setVisibility(View.GONE);
            btn_cancel_queue.setLayoutParams(setLayoutWidthParams(true));
            frame_coupon.setVisibility(View.GONE);
            rl_apply_coupon.setClickable(false);
        } else {
            btn_pay.setVisibility(View.VISIBLE);
            btn_pay.setLayoutParams(params);
            btn_cancel_queue.setLayoutParams(setLayoutWidthParams(false));
            if (TextUtils.isEmpty(jsonPurchaseOrder.getCouponId())) {
                rl_apply_coupon.setVisibility(View.VISIBLE);
                rl_coupon_applied.setVisibility(View.GONE);
                rl_discount.setVisibility(View.GONE);

            } else {
                rl_apply_coupon.setVisibility(View.GONE);
                rl_coupon_applied.setVisibility(View.VISIBLE);
                rl_discount.setVisibility(View.VISIBLE);
            }
        }

        if (TextUtils.isEmpty(jsonPurchaseOrder.getOrderPrice()) ||
                Integer.parseInt(jsonPurchaseOrder.getOrderPrice()) == 0) {
            frame_coupon.setVisibility(View.GONE);
            rl_discount.setVisibility(View.GONE);
            btn_pay.setText("Confirm");
        }
        dismissProgress();
    }

    private LinearLayout.LayoutParams setLayoutWidthParams(boolean isMatchParent) {
        if (isMatchParent) {
            return new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    120, 1.0f);
        } else {
            return new LinearLayout.LayoutParams(0,
                    120, 0.49f);
        }
    }

    @Override
    public void paymentInitiateResponse(JsonResponseWithCFToken jsonResponseWithCFToken) {
        if (null != jsonResponseWithCFToken) {
            jsonToken.getJsonPurchaseOrder().setJsonResponseWithCFToken(jsonResponseWithCFToken);
            triggerOnlinePayment();
        }
        dismissProgress();
    }

    private void pay() {
        if (null != jsonTokenAndQueue.getJsonPurchaseOrder()) {
            setProgressMessage("Starting payment process...");
            showProgress();
            setProgressCancel(false);
            JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
                    .setCodeQR(codeQR)
                    .setQueueUserId(jsonTokenAndQueue.getQueueUserId())
                    .setTransactionId(jsonTokenAndQueue.getJsonPurchaseOrder().getTransactionId());
            queueApiAuthenticCall.payNow(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITY_RESULT_BACK) {
            if (resultCode == RESULT_OK) {
                JsonCoupon jsonCoupon = (JsonCoupon) data.getSerializableExtra(IBConstant.KEY_DATA_OBJECT);
                Log.e("Data receive", jsonCoupon.toString());

                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    showProgress();
                    setProgressMessage("Applying discount..");
                    // progressDialog.setCancelable(false);
                    // progressDialog.setCanceledOnTouchOutside(false);
                    ClientCouponApiCalls clientCouponApiCalls = new ClientCouponApiCalls();
                    clientCouponApiCalls.setCouponApplyRemovePresenter(this);
                    CouponOnOrder couponOnOrder = new CouponOnOrder()
                            .setQueueUserId(jsonTokenAndQueue.getQueueUserId())
                            .setCouponId(jsonCoupon.getCouponId())
                            .setTransactionId(jsonTokenAndQueue.getTransactionId());

                    clientCouponApiCalls.apply(UserUtils.getDeviceId(),
                            UserUtils.getEmail(),
                            UserUtils.getAuth(),
                            couponOnOrder);
                } else {
                    ShowAlertInformation.showNetworkDialog(JoinActivity.this);
                }
            }
        } else if (requestCode == Constants.requestCodeAfterJoinQActivity) {
            if (resultCode == RESULT_OK) {
                boolean toClose = data.getExtras().getBoolean(Constants.ACTIVITY_TO_CLOSE, false);
                if (toClose) {
                    returnResultBack();
                    finish();
                }
            }
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej) {
            try {
                if (MobileSystemErrorCodeEnum.valueOf(eej.getSystemError()) == MobileSystemErrorCodeEnum.JOIN_PRE_APPROVED_QUEUE_ONLY) {
                    showAuthorizationDialog(this);
                } else {
                    //new ErrorResponseHandler().processError(this, eej);
                    ShowCustomDialog showDialog = new ShowCustomDialog(JoinActivity.this);
                    showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                        @Override
                        public void btnPositiveClick() {
                            finish();
                        }

                        @Override
                        public void btnNegativeClick() {
                            //Do nothing
                        }
                    });
                    showDialog.displayDialog("Alert", eej.getReason());
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log("Error code missing " + eej.getSystemErrorCode());
                Log.e(TAG, "Error code missing" + eej.getSystemErrorCode(), e);
            }
        }
    }

    private void showAuthorizationDialog(final Context context) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_two_input);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        EditText edtGroceryCard = dialog.findViewById(R.id.edt_grocery_card);
        EditText edtLiquorCard = dialog.findViewById(R.id.edt_liquor_card);

        edtGroceryCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtGroceryCard.getText().toString().length() < 19) {
                    edtGroceryCard.setError(getString(R.string.grocery_card_number_entry_error));
                } else if (edtGroceryCard.getText().toString().charAt(0) != 'G') {
                    edtGroceryCard.setError(getString(R.string.grocery_card_number_prefix_error));
                } else {
                    edtGroceryCard.setError(null);
                }
            }
        });

        edtLiquorCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtLiquorCard.getText().toString().length() < 19) {
                    edtLiquorCard.setError(getString(R.string.liquor_card_number_entry_error));
                } else if (edtLiquorCard.getText().toString().charAt(0) != 'L') {
                    edtLiquorCard.setError(getString(R.string.liquor_card_number_prefix_error));
                } else {
                    edtLiquorCard.setError(null);
                }
            }
        });

        Button btn_positive = dialog.findViewById(R.id.btn_positive);
        btn_positive.setOnClickListener(v -> {
            if (btn_positive.getText().equals(this.getString(R.string.submit_button))) {
                String gCard = null;
                String lCard = null;
                if (edtGroceryCard.getError() == null && !TextUtils.isEmpty(edtGroceryCard.getText())) {
                    gCard = edtGroceryCard.getText().toString();
                }
                if (edtLiquorCard.getError() == null && !TextUtils.isEmpty(edtLiquorCard.getText())) {
                    lCard = edtLiquorCard.getText().toString();
                }

                if (!TextUtils.isEmpty(gCard) || !TextUtils.isEmpty(lCard)) {
                    QueueAuthorize queueAuthorize = new QueueAuthorize()
                            .setCodeQR(codeQR)
                            .setFirstCustomerId(gCard)
                            .setAdditionalCustomerId(lCard);
                    queueApiAuthenticCall.setAuthorizeResponsePresenter(this);
                    queueApiAuthenticCall.businessApprove(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), queueAuthorize);
                    AppUtils.hideKeyBoard(this);
                    new CustomToast().showToast(this, "Successfully submitted pre-approval. Please try to join the queue again.");
                    dialog.dismiss();
                } else {
                    if (edtGroceryCard.getText().toString().length() < 19) {
                        edtGroceryCard.setError(getString(R.string.grocery_card_number_entry_error));
                    } else if (edtLiquorCard.getText().toString().length() < 19) {
                        edtLiquorCard.setError(getString(R.string.liquor_card_number_entry_error));
                    }
                }
            }
        });
        Button btn_negative = dialog.findViewById(R.id.btn_negative);
        btn_negative.setOnClickListener(v -> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        try {
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
        } catch (Exception e) {
            // WindowManager$BadTokenException will be caught and the app would not display
            // the 'Force Close' message
        }
    }
}