package com.noqapp.android.client.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.CouponApiCalls;
import com.noqapp.android.client.model.QueueApiAuthenticCall;
import com.noqapp.android.client.model.QueueApiUnAuthenticCall;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.presenter.CashFreeNotifyQPresenter;
import com.noqapp.android.client.presenter.CouponApplyPresenter;
import com.noqapp.android.client.presenter.QueueJsonPurchaseOrderPresenter;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.FabricEvents;
import com.noqapp.android.client.utils.GetTimeAgoUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.common.beans.JsonCoupon;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.CouponOnOrder;
import com.noqapp.android.common.beans.body.JoinQueue;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;
import com.noqapp.android.common.beans.payment.cashfree.JsonResponseWithCFToken;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.SkipPaymentGatewayEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;
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
public class JoinActivity extends BaseActivity implements TokenPresenter, ResponsePresenter, ActivityCommunicator,
        CFClientInterface, CashFreeNotifyQPresenter, QueueJsonPurchaseOrderPresenter, CouponApplyPresenter {
    private static final String TAG = JoinActivity.class.getSimpleName();
    private TextView tv_address;
    private TextView tv_mobile;
    private TextView tv_serving_no;
    private TextView tv_token;
    private TextView tv_how_long;
    private Button btn_cancel_queue;
    private TextView tv_after;
    private TextView tv_estimated_time;
    private TextView tv_name;
    private TextView tv_vibrator_off;
    private LinearLayout ll_change_bg;
    private JsonToken jsonToken;
    private JsonTokenAndQueue jsonTokenAndQueue;
    private JsonQueue jsonQueue;
    private String codeQR;
    private String tokenValue;
    private String topic;
    private boolean isResumeFirst = true;
    private String gotoPerson = "";
    private String queueUserId = "";
    private QueueApiUnAuthenticCall queueApiUnAuthenticCall;
    private QueueApiAuthenticCall queueApiAuthenticCall;
    private Button btn_pay;
    private TextView tv_payment_status, tv_due_amt;
    private CardView card_amount;
    private TextView tv_total_order_amt;
    private TextView tv_grand_total_amt;
    private TextView tv_coupon_amount;
    private TextView tv_coupon_name;
    private TextView tv_coupon_discount_amt;
    private CFPaymentService cfPaymentService;
    private JsonProfile jsonProfile;
    private RelativeLayout rl_apply_coupon,rl_coupon_applied;
    private String currencySymbol = "Rs.";
    private  TextView tv_remove_coupon;
    private FrameLayout frame_coupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        new InitPaymentGateway().execute();
        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_queue_name = findViewById(R.id.tv_queue_name);
        tv_address = findViewById(R.id.tv_address);
        TextView tv_delay_in_time = findViewById(R.id.tv_delay_in_time);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_serving_no = findViewById(R.id.tv_serving_no);
        tv_token = findViewById(R.id.tv_token);
        tv_how_long = findViewById(R.id.tv_how_long);
        btn_cancel_queue = findViewById(R.id.btn_cancel_queue);
        btn_pay = findViewById(R.id.btn_pay);
        tv_after = findViewById(R.id.tv_after);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_due_amt = findViewById(R.id.tv_due_amt);
        tv_coupon_amount = findViewById(R.id.tv_coupon_amount);
        tv_coupon_name = findViewById(R.id.tv_coupon_name);
        tv_coupon_discount_amt = findViewById(R.id.tv_coupon_discount_amt);
        tv_total_order_amt = findViewById(R.id.tv_total_order_amt);
        tv_grand_total_amt = findViewById(R.id.tv_grand_total_amt);
        TextView tv_hour_saved = findViewById(R.id.tv_hour_saved);
        tv_estimated_time = findViewById(R.id.tv_estimated_time);
        TextView tv_add = findViewById(R.id.add_person);
        tv_vibrator_off = findViewById(R.id.tv_vibrator_off);
        ll_change_bg = findViewById(R.id.ll_change_bg);
        card_amount = findViewById(R.id.card_amount);
        tv_name = findViewById(R.id.tv_name);
        rl_apply_coupon = findViewById(R.id.rl_apply_coupon);
        rl_coupon_applied = findViewById(R.id.rl_coupon_applied);
        frame_coupon = findViewById(R.id.frame_coupon);
        rl_apply_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // new CustomToast().showToast(JoinActivity.this,"Apply Coupon");
                Intent in = new Intent(JoinActivity.this, CouponsActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, codeQR);
                startActivityForResult(in, Constants.ACTIVITTY_RESULT_BACK);
            }
        });
        tv_remove_coupon = findViewById(R.id.tv_remove_coupon);
        tv_remove_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_apply_coupon.performClick();
            }
        });
        ImageView iv_profile = findViewById(R.id.iv_profile);
        btn_cancel_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        });
        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.screen_qdetails));
        queueApiUnAuthenticCall = new QueueApiUnAuthenticCall();
        queueApiAuthenticCall = new QueueApiAuthenticCall();
        queueApiAuthenticCall.setQueueJsonPurchaseOrderPresenter(this);
        queueApiAuthenticCall.setTokenPresenter(this);
        LaunchActivity.getLaunchActivity().activityCommunicator = this;
        Intent bundle = getIntent();
        if (null != bundle) {
            jsonQueue = (JsonQueue) bundle.getSerializableExtra(IBConstant.KEY_JSON_QUEUE);
            jsonTokenAndQueue = (JsonTokenAndQueue) bundle.getSerializableExtra(IBConstant.KEY_JSON_TOKEN_QUEUE);
            if(null != jsonQueue){
                currencySymbol = AppUtilities.getCurrencySymbol(jsonQueue.getCountryShortName());
            } else if (null != jsonTokenAndQueue) {
                currencySymbol = AppUtilities.getCurrencySymbol(jsonTokenAndQueue.getCountryShortName());
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
                jsonProfile = AppUtilities.getJsonProfileQueueUserID(queueUserId, profileList);
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
            actionbarBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_home.performClick();
                }
            });
            iv_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LaunchActivity.getLaunchActivity().activityCommunicator = null;
                    Intent goToA = new Intent(JoinActivity.this, LaunchActivity.class);
                    goToA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(goToA);
                }
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
            if (jsonTokenAndQueue.getDelayedInMinutes() > 0) {
                int hours = jsonTokenAndQueue.getDelayedInMinutes() / 60;
                int minutes = jsonTokenAndQueue.getDelayedInMinutes() % 60;
                String red = "<b>Delayed by " + hours + " Hrs " + minutes + " minutes.</b>";
                tv_delay_in_time.setText(Html.fromHtml(red));
            } else {
                tv_delay_in_time.setVisibility(View.GONE);
            }
            String time = new AppUtilities().formatTodayStoreTiming(this, jsonTokenAndQueue.getStartHour(), jsonTokenAndQueue.getEndHour());
            tv_hour_saved.setText(time);
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonTokenAndQueue.getCountryShortName(), jsonTokenAndQueue.getStorePhone()));
            tv_mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtilities.makeCall(JoinActivity.this, tv_mobile.getText().toString());
                }
            });
            tv_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtilities.openAddressInMap(JoinActivity.this, tv_address.getText().toString());
                }
            });
            gotoPerson = null != ReviewDB.getValue(codeQR, tokenValue) ? ReviewDB.getValue(codeQR, tokenValue).getGotoCounter() : "";
            if (bundle.getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
                tv_serving_no.setText(String.valueOf(jsonTokenAndQueue.getServingNumber()));
                tv_token.setText(String.valueOf(jsonTokenAndQueue.getToken()));
                tv_how_long.setText(String.valueOf(jsonTokenAndQueue.afterHowLong()));
                setBackGround(jsonTokenAndQueue.afterHowLong() > 0 ? jsonTokenAndQueue.afterHowLong() : 0);
                tv_name.setText(jsonProfile.getName());
                tv_vibrator_off.setVisibility(isVibratorOff() ? View.VISIBLE : View.GONE);
                if (isVibratorOff()) {
                    ShowAlertInformation.showThemeDialog(this, "Vibrator off", getString(R.string.msg_vibrator_off));
                }

                if (!TextUtils.isEmpty(jsonTokenAndQueue.getTransactionId())) {
                    progressDialog.setMessage("Fetching Queue data..");
                    progressDialog.show();
                    queueApiAuthenticCall.purchaseOrder(
                            UserUtils.getDeviceId(),
                            UserUtils.getEmail(),
                            UserUtils.getAuth(),
                            String.valueOf(jsonTokenAndQueue.getToken()),
                            codeQR);
                }
            } else {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    if (isResumeFirst) {
                        progressDialog.setMessage("Joining Queue..");
                        progressDialog.show();
                        callQueue();
                    }
                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                }
            }
        }
    }

    @Override
    public void couponApplyResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        Log.e("data is: ",jsonPurchaseOrder.toString());
        queueJsonPurchaseOrderResponse(jsonPurchaseOrder);
        dismissProgress();
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
        tv_serving_no.setText(String.valueOf(token.getServingNumber()));
        tv_token.setText(String.valueOf(token.getToken()));
        tokenValue = String.valueOf(token.getToken());
        tv_how_long.setText(String.valueOf(token.afterHowLong()));
        setBackGround(token.afterHowLong() > 0 ? token.afterHowLong() : 0);
        NoQueueMessagingService.subscribeTopics(topic);
        jsonTokenAndQueue.setServingNumber(token.getServingNumber());
        jsonTokenAndQueue.setToken(token.getToken());
        jsonTokenAndQueue.setQueueUserId(queueUserId);
        updateEstimatedTime();
        //save data to DB
        TokenAndQueueDB.saveJoinQueueObject(jsonTokenAndQueue);
        tv_vibrator_off.setVisibility(isVibratorOff() ? View.VISIBLE : View.GONE);
        if (isVibratorOff()) {
            ShowAlertInformation.showThemeDialog(this, "Vibrator off", getString(R.string.msg_vibrator_off));
        }
        dismissProgress();
    }

    @Override
    public void paidTokenPresenterResponse(JsonToken token) {
        this.jsonToken = token;
        if (null != token) {
            Log.d(TAG, token.toString());
            if (token.getJsonPurchaseOrder().getPresentOrderState() == PurchaseOrderStateEnum.VB) {
                if (SkipPaymentGatewayEnum.YES == token.getJsonPurchaseOrder().getJsonResponseWithCFToken().getSkipPaymentGateway()) {
                    new CustomToast().showToast(this, "You are already in the Queue");
                    queueJsonPurchaseOrderResponse(token.getJsonPurchaseOrder());
                    tokenPresenterResponse(jsonToken);
                } else {
                    if (new BigDecimal(token.getJsonPurchaseOrder().getOrderPrice()).intValue() > 0) {
                        triggerOnlinePayment();
                    } else {
                        queueApiAuthenticCall.setCashFreeNotifyQPresenter(this);
                        JsonCashfreeNotification jsonCashfreeNotification = new JsonCashfreeNotification();
                        jsonCashfreeNotification.setTxMsg(null);
                        jsonCashfreeNotification.setTxTime(null);
                        jsonCashfreeNotification.setReferenceId(null);
                        jsonCashfreeNotification.setPaymentMode(null);  // cash
                        jsonCashfreeNotification.setSignature(null);
                        jsonCashfreeNotification.setOrderAmount(token.getJsonPurchaseOrder().getOrderPrice()); // amount
                        jsonCashfreeNotification.setTxStatus("SUCCESS");   //SUCCESS
                        jsonCashfreeNotification.setOrderId(token.getJsonPurchaseOrder().getTransactionId());   // transactionID
                        queueApiAuthenticCall.cashFreeQNotify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonCashfreeNotification);
                    }
                }
            } else if (token.getJsonPurchaseOrder().getPresentOrderState() == PurchaseOrderStateEnum.PO) {
                new CustomToast().showToast(this, "You are already in the Queue");
                queueJsonPurchaseOrderResponse(token.getJsonPurchaseOrder());
                tokenPresenterResponse(jsonToken);
                btn_pay.setVisibility(View.GONE);
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
                btn_pay.setVisibility(View.GONE);
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
        if (null != response) {
            if (response.getResponse() == Constants.SUCCESS) {
                new CustomToast().showToast(this, getString(R.string.cancel_queue));
            } else {
                new CustomToast().showToast(this, getString(R.string.fail_to_cancel));
            }
        } else {
            new CustomToast().showToast(this, getString(R.string.fail_to_cancel));
        }
        NoQueueMessagingService.unSubscribeTopics(topic);
        TokenAndQueueDB.deleteTokenQueue(codeQR, tokenValue);
        onBackPressed();
        dismissProgress();
    }

    @Override
    public void responsePresenterError() {
        Log.d("", "responsePresenterError");
        dismissProgress();
    }

    private void cancelQueue() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.setMessage("Cancel Queue");
            progressDialog.show();
            if (UserUtils.isLogin()) {
                queueApiAuthenticCall.setResponsePresenter(this);
                queueApiAuthenticCall.abortQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
            } else {
                queueApiUnAuthenticCall.setResponsePresenter(this);
                queueApiUnAuthenticCall.abortQueue(UserUtils.getDeviceId(), codeQR);
            }
            if (AppUtilities.isRelease()) {
                try {
                    String displayName = null != jsonQueue ? jsonQueue.getDisplayName() : (null != jsonTokenAndQueue ? jsonTokenAndQueue.getDisplayName() : "N/A");
                    Answers.getInstance().logCustom(new CustomEvent(FabricEvents.EVENT_CANCEL_QUEUE)
                            .putCustomAttribute("Queue Name", displayName));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
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

                if (jsonQueue.isEnabledPayment()) {
//                    if (getIntent().getBooleanExtra("isPayBeforeJoin", false)) {
//                        queueApiAuthenticCall.payBeforeJoinQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), joinQueue);
//                    } else {
                        queueApiAuthenticCall.skipPayBeforeQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), joinQueue);
                //    }
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
        if (!isResumeFirst) {
            JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(codeQR, tokenValue);
            if (null != jtk) {
                if (TextUtils.isEmpty(gotoPerson))
                    gotoPerson = null != ReviewDB.getValue(codeQR, tokenValue) ? ReviewDB.getValue(codeQR, tokenValue).getGotoCounter() : "";
                setObject(jtk, gotoPerson);
            }
        }
        if (isResumeFirst) {
            isResumeFirst = false;
        }
    }

    public String getCodeQR() {
        return codeQR;
    }

    public void setBackGround(int pos) {
        tv_after.setTextColor(Color.WHITE);
        tv_how_long.setTextColor(Color.WHITE);
        // tv_estimated_time.setTextColor(Color.WHITE);
        tv_after.setText("Soon is your turn! You are:");
        btn_cancel_queue.setEnabled(true);
        switch (pos) {
            case 0:
                ll_change_bg.setBackgroundResource(R.drawable.blue_gradient);
                tv_after.setText("It's your turn!!!");
                tv_how_long.setText(gotoPerson);
                btn_cancel_queue.setVisibility(View.GONE);
                break;
            case 1:
                ll_change_bg.setBackgroundResource(R.drawable.blue_gradient);
                tv_after.setText("Next is your turn! You are:");
                break;
            case 2:
                ll_change_bg.setBackgroundResource(R.drawable.blue_gradient);
                break;
            case 3:
                ll_change_bg.setBackgroundResource(R.drawable.blue_gradient);
                break;
            case 4:
                ll_change_bg.setBackgroundResource(R.drawable.blue_gradient);
                break;
            case 5:
                ll_change_bg.setBackgroundResource(R.drawable.blue_gradient);
                break;
            default:
                tv_after.setText("You are:");
                tv_after.setTextColor(ContextCompat.getColor(this, R.color.colorActionbar));
                tv_how_long.setTextColor(ContextCompat.getColor(this, R.color.colorActionbar));
                ll_change_bg.setBackgroundResource(R.drawable.btn_bg_inactive);
                // tv_estimated_time.setTextColor(ContextCompat.getColor(this, R.color.colorActionbar));
                break;
        }
    }

    public void setObject(JsonTokenAndQueue jq, String go_to) {
        gotoPerson = go_to;
        // jsonTokenAndQueue = jq; removed to avoided the override of the data
        jsonTokenAndQueue.setServingNumber(jq.getServingNumber());
        jsonTokenAndQueue.setToken(jq.getToken());
        tv_serving_no.setText(String.valueOf(jsonTokenAndQueue.getServingNumber()));
        tv_token.setText(String.valueOf(jsonTokenAndQueue.getToken()));
        tv_how_long.setText(String.valueOf(jsonTokenAndQueue.afterHowLong()));
        updateEstimatedTime();
        setBackGround(jq.afterHowLong() > 0 ? jq.afterHowLong() : 0);
    }

    private void updateEstimatedTime() {
        try {
            if (!TextUtils.isEmpty(jsonToken.getExpectedServiceBegin())) {
                tv_estimated_time.setText(String.format(getString(R.string.estimated_time), Formatter.getTimeAsString(Formatter.getDateFromString(jsonToken.getExpectedServiceBegin()))));
                tv_estimated_time.setVisibility(View.VISIBLE);
            } else {
                if (!TextUtils.isEmpty("" + jsonTokenAndQueue.getAverageServiceTime()) && jsonTokenAndQueue.getAverageServiceTime() > 0) {
                    String output = GetTimeAgoUtils.getTimeAgo(jsonTokenAndQueue.afterHowLong() * jsonTokenAndQueue.getAverageServiceTime());
                    if (null == output) {
                        tv_estimated_time.setVisibility(View.INVISIBLE);
                    } else {
                        tv_estimated_time.setText(String.format(getString(R.string.estimated_time), output));
                        tv_estimated_time.setVisibility(View.VISIBLE);
                    }
                } else {
                    tv_estimated_time.setVisibility(View.INVISIBLE);
                }
            }
        } catch (Exception e) {
            Log.e("", "Error setting data reason=" + e.getLocalizedMessage(), e);
            tv_estimated_time.setVisibility(View.INVISIBLE);
        }
        tv_estimated_time.setText(getString(R.string.will_be_served, "30 Min *"));
        tv_estimated_time.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LaunchActivity.getLaunchActivity().activityCommunicator = null;
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
        iv_home.performClick();
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

    private boolean isVibratorOff() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (null != am) {
            switch (am.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT:
                    Log.e(TAG, "Silent mode");
                    return true;
                case AudioManager.RINGER_MODE_VIBRATE:
                    Log.e(TAG, "Vibrate mode");
                    return false;
                case AudioManager.RINGER_MODE_NORMAL:
                    Log.e(TAG, "Normal mode");
                    return false;
                default:
                    return true;
            }
        } else {
            return true;
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
        // enableDisableOrderButton(false);
        finish();
    }

    @Override
    public void onNavigateBack() {
        Log.e("User Navigate Back", "Back without payment");
        new CustomToast().showToast(this, "Cancelled transaction. Please try again.");
        //enableDisableOrderButton(false);
        // finish();
        if (getIntent().getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
            // do nothing
        } else {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
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
            new CustomToast().showToast(this, "Email is mandatory. Please add and verify it");
        }
    }

    @Override
    public void cashFreeNotifyQResponse(JsonToken jsonToken) {
        btn_pay.setVisibility(View.GONE);
        if (PaymentStatusEnum.PA == jsonToken.getJsonPurchaseOrder().getPaymentStatus()) {
            if (!getIntent().getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
                // show only when it comes from join screen
                new CustomToast().showToast(this, "Token generated successfully.");
            }
            queueJsonPurchaseOrderResponse(jsonToken.getJsonPurchaseOrder());
            tokenPresenterResponse(jsonToken);
        } else {
            new CustomToast().showToast(this, jsonToken.getJsonPurchaseOrder().getTransactionMessage());
        }
    }

    @Override
    public void queueJsonPurchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        Log.e("respo: ", jsonPurchaseOrder.toString());
        btn_pay.setVisibility(View.VISIBLE);
        this.jsonTokenAndQueue.setJsonPurchaseOrder(jsonPurchaseOrder);
        if (null == jsonToken) {
            jsonToken = new JsonToken();
        }
        jsonToken.setJsonPurchaseOrder(jsonPurchaseOrder);
        card_amount.setVisibility(View.VISIBLE);
        // tv_due_amt.setText(currencySymbol + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        tv_total_order_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        tv_grand_total_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        tv_coupon_amount.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
        tv_coupon_discount_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
       // tv_coupon_name.setText(jsonCoupon.getDiscountName());
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            tv_payment_status.setText("Paid via: " + jsonPurchaseOrder.getPaymentMode().getDescription());
            btn_pay.setVisibility(View.GONE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );
            btn_cancel_queue.setLayoutParams(param);
            frame_coupon.setVisibility(View.GONE);
            rl_apply_coupon.setClickable(false);
        } else {
            tv_payment_status.setText("Payment status: " + jsonPurchaseOrder.getPaymentStatus().getDescription());
            btn_pay.setVisibility(View.VISIBLE);
        }
        dismissProgress();

        //  if (getIntent().getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
        if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.VB) {
            if (new BigDecimal(jsonPurchaseOrder.getOrderPrice()).intValue() == 0) {
                queueApiAuthenticCall.setCashFreeNotifyQPresenter(this);
                JsonCashfreeNotification jsonCashfreeNotification = new JsonCashfreeNotification();
                jsonCashfreeNotification.setTxMsg(null);
                jsonCashfreeNotification.setTxTime(null);
                jsonCashfreeNotification.setReferenceId(null);
                jsonCashfreeNotification.setPaymentMode(null);  // cash
                jsonCashfreeNotification.setSignature(null);
                jsonCashfreeNotification.setOrderAmount(jsonPurchaseOrder.getOrderPrice()); // amount
                jsonCashfreeNotification.setTxStatus("SUCCESS");   //SUCCESS
                jsonCashfreeNotification.setOrderId(jsonPurchaseOrder.getTransactionId());   // transactionID
                queueApiAuthenticCall.cashFreeQNotify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonCashfreeNotification);
            }
        }
        // }
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
            progressDialog.setMessage("Starting payment process...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
                    .setCodeQR(codeQR)
                    .setQueueUserId(jsonTokenAndQueue.getQueueUserId())
                    .setTransactionId(jsonTokenAndQueue.getJsonPurchaseOrder().getTransactionId());
            queueApiAuthenticCall.paymentInitiate(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITTY_RESULT_BACK) {
            if (resultCode == RESULT_OK) {
                JsonCoupon jsonCoupon = (JsonCoupon) data.getSerializableExtra(IBConstant.KEY_DATA_OBJECT);
                Log.e("data recieve", jsonCoupon.toString());
                rl_coupon_applied.setVisibility(View.VISIBLE);
                rl_apply_coupon.setVisibility(View.GONE);
                tv_coupon_amount.setText(currencySymbol + String.valueOf(jsonCoupon.getDiscountAmount()));
                tv_coupon_name.setText(jsonCoupon.getDiscountName());
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    progressDialog.show();
                    progressDialog.setMessage("Applying discount..");
                    // progressDialog.setCancelable(false);
                    // progressDialog.setCanceledOnTouchOutside(false);
                    CouponApiCalls couponApiCalls = new CouponApiCalls();
                    couponApiCalls.setCouponApplyPresenter(this);

                    CouponOnOrder couponOnOrder = new CouponOnOrder()
                            .setQueueUserId(jsonTokenAndQueue.getQueueUserId())
                            .setCouponId(jsonCoupon.getCouponId())
                            .setTransactionId(jsonTokenAndQueue.getTransactionId());

                    couponApiCalls.apply(UserUtils.getDeviceId(),
                            UserUtils.getEmail(),
                            UserUtils.getAuth(),
                            couponOnOrder);
                } else {
                    ShowAlertInformation.showNetworkDialog(JoinActivity.this);
                }
            }
        }
    }
}
