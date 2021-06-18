package com.noqapp.android.client.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.google.common.cache.Cache;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ClientCouponApiCalls;
import com.noqapp.android.client.model.QueueApiAuthenticCall;
import com.noqapp.android.client.model.QueueApiUnAuthenticCall;
import com.noqapp.android.client.presenter.CashFreeNotifyQPresenter;
import com.noqapp.android.client.presenter.QueueJsonPurchaseOrderPresenter;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.TokenStatusUtils;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.client.views.version_2.HomeActivity;
import com.noqapp.android.client.views.version_2.db.helper_models.ForegroundNotificationModel;
import com.noqapp.android.client.views.version_2.viewmodels.AfterJoinViewModel;
import com.noqapp.android.common.beans.JsonCoupon;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.CouponOnOrder;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;
import com.noqapp.android.common.beans.payment.cashfree.JsonResponseWithCFToken;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.presenter.CouponApplyRemovePresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.common.utils.TextToSpeechHelper;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;
import static com.google.common.cache.CacheBuilder.newBuilder;

/**
 * Created by chandra on 5/7/17.
 */
public class AfterJoinActivity
        extends BaseActivity
        implements ResponsePresenter, ActivityCommunicator, QueueJsonPurchaseOrderPresenter, CouponApplyRemovePresenter,
        CFClientInterface, CashFreeNotifyQPresenter {
    private static final String TAG = AfterJoinActivity.class.getSimpleName();
    private TextView tv_address;
    private TextView tv_business_name;
    private TextView tv_mobile;
    //private TextView tv_serving_no; // No longer displayed, to be deleted
    private TextView tv_token;
    private LinearLayout ll_change_bg;
    private TextView tv_position_in_queue_label;
    private TextView tv_position_in_queue;
    private Button btn_cancel_queue;
    private Button btn_go_back;
    private TextView tv_estimated_time, tv_left;
    private TextView tv_name;
    private JsonToken jsonToken;
    private JsonTokenAndQueue jsonTokenAndQueue;
    private String codeQR;
    private String tokenValue;
    private String topic;
    private boolean isResumeFirst = true;
    private String gotoPerson = "";
    private String queueUserId = "";
    private QueueApiUnAuthenticCall queueApiUnAuthenticCall;
    private QueueApiAuthenticCall queueApiAuthenticCall;
    private TextView tv_payment_status, tv_due_amt;
    private CardView card_amount;
    private TextView tv_total_order_amt;
    private JsonProfile jsonProfile;
    private LinearLayout ll_order_details;
    private TextView tv_grand_total_amt;
    private TextView tv_coupon_discount_amt;
    private RelativeLayout rl_discount;
    private String currencySymbol = "";
    private FrameLayout frame_coupon;
    private RelativeLayout rl_apply_coupon, rl_coupon_applied;
    private Button btn_pay;
    private TextView tv_coupon_amount;
    private TextView tv_coupon_name;
    private CFPaymentService cfPaymentService;
    private ImageView iv_codeqr;
    private TextView tv_token_day, tv_token_time;
    private AfterJoinViewModel afterJoinViewModel;
    private TextToSpeechHelper textToSpeechHelper;
    private final Cache<String, ArrayList<String>> cacheMsgIds = newBuilder().maximumSize(1).build();
    private final String MSG_IDS = "messageIds";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_join);

        afterJoinViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(AfterJoinViewModel.class);
        textToSpeechHelper = new TextToSpeechHelper(this);

        observeValues();

        new InitPaymentGateway().execute();
        TextView tv_queue_name = findViewById(R.id.tv_queue_name);
        tv_address = findViewById(R.id.tv_address);
        tv_business_name = findViewById(R.id.tv_business_name_label);
        TextView tv_delay_in_time = findViewById(R.id.tv_delay_in_time);
        ImageView iv_right_bg = findViewById(R.id.iv_right_bg);
        ImageView iv_left_bg = findViewById(R.id.iv_left_bg);
        tv_mobile = findViewById(R.id.tv_mobile);
        //tv_serving_no = findViewById(R.id.tv_serving_no);
        tv_token = findViewById(R.id.tv_token);
        ll_change_bg = findViewById(R.id.ll_change_bg);
        tv_position_in_queue = findViewById(R.id.tv_position_in_queue_value);
        btn_cancel_queue = findViewById(R.id.btn_cancel_queue);
        btn_go_back = findViewById(R.id.btn_go_back);
        tv_position_in_queue_label = findViewById(R.id.tv_position_in_queue_label);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_due_amt = findViewById(R.id.tv_due_amt);
        tv_total_order_amt = findViewById(R.id.tv_total_order_amt);
        TextView tv_hour_saved = findViewById(R.id.tv_hour_saved);
        tv_estimated_time = findViewById(R.id.tv_estimated_time);
        tv_left = findViewById(R.id.tv_left);
        TextView tv_add = findViewById(R.id.add_person);
        TextView tv_vibrator_off = findViewById(R.id.tv_vibrator_off);
        ll_order_details = findViewById(R.id.ll_order_details);
        rl_discount = findViewById(R.id.rl_discount);
        card_amount = findViewById(R.id.card_amount);
        tv_name = findViewById(R.id.tv_name);
        tv_coupon_discount_amt = findViewById(R.id.tv_coupon_discount_amt);
        tv_grand_total_amt = findViewById(R.id.tv_grand_total_amt);
        btn_pay = findViewById(R.id.btn_pay);
        iv_codeqr = findViewById(R.id.iv_codeqr);
        tv_token_day = findViewById(R.id.tv_token_day);
        tv_token_time = findViewById(R.id.tv_token_time);
        rl_apply_coupon = findViewById(R.id.rl_apply_coupon);
        rl_coupon_applied = findViewById(R.id.rl_coupon_applied);
        frame_coupon = findViewById(R.id.frame_coupon);
        frame_coupon.setVisibility(View.GONE);
        tv_coupon_amount = findViewById(R.id.tv_coupon_amount);
        tv_coupon_name = findViewById(R.id.tv_coupon_name);
        rl_apply_coupon.setOnClickListener((View v) -> {
            if (null != jsonToken && null != jsonToken.getJsonPurchaseOrder() && jsonToken.getJsonPurchaseOrder().isDiscountedPurchase()) {
                new CustomToast().showToast(AfterJoinActivity.this, getString(R.string.discount_error));
            } else {
                Intent in = new Intent(AfterJoinActivity.this, CouponsActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, codeQR);
                startActivityForResult(in, Constants.ACTIVITY_RESULT_BACK);
            }
        });
        TextView tv_remove_coupon = findViewById(R.id.tv_remove_coupon);
        tv_remove_coupon.setOnClickListener((View v) -> {
            ShowCustomDialog showDialog = new ShowCustomDialog(AfterJoinActivity.this, true);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    if (isOnline()) {
                        showProgress();
                        setProgressMessage("Removing discount..");
                        // progressDialog.setCancelable(false);
                        // progressDialog.setCanceledOnTouchOutside(false);
                        ClientCouponApiCalls clientCouponApiCalls = new ClientCouponApiCalls();
                        clientCouponApiCalls.setCouponApplyRemovePresenter(AfterJoinActivity.this);

                        CouponOnOrder couponOnOrder = new CouponOnOrder()
                                .setQueueUserId(jsonTokenAndQueue.getQueueUserId())
                                // .setCouponId(jsonCoupon.getCouponId())
                                .setTransactionId(jsonTokenAndQueue.getTransactionId());

                        clientCouponApiCalls.remove(
                                UserUtils.getDeviceId(),
                                UserUtils.getEmail(),
                                UserUtils.getAuth(),
                                couponOnOrder);
                    } else {
                        ShowAlertInformation.showNetworkDialog(AfterJoinActivity.this);
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
            ShowCustomDialog showDialog = new ShowCustomDialog(AfterJoinActivity.this, true);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    if (null != jsonTokenAndQueue) {
                        if (null == jsonTokenAndQueue.getJsonPurchaseOrder() || null == jsonTokenAndQueue.getJsonPurchaseOrder().getTransactionVia()) {
                            cancelQueue();
                        } else {
                            switch (jsonTokenAndQueue.getJsonPurchaseOrder().getTransactionVia()) {
                                case I:
                                case E:
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
            showDialog.displayDialog("Cancel Your Token", "Choose OK to cancel your token. You will loose your current token number.");
        });
        btn_pay.setOnClickListener((View v) -> {
            if (new BigDecimal(jsonToken.getJsonPurchaseOrder().getOrderPrice()).intValue() > 0) {
                pay();
            } else {
                //do nothing
            }
        });
        btn_go_back.setOnClickListener(v -> iv_home.performClick());
        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.screen_qdetails));
        queueApiUnAuthenticCall = new QueueApiUnAuthenticCall();
        queueApiAuthenticCall = new QueueApiAuthenticCall();
        queueApiAuthenticCall.setQueueJsonPurchaseOrderPresenter(this);
        AppInitialize.activityCommunicator = this;
        Intent bundle = getIntent();
        if (null != bundle) {
            jsonTokenAndQueue = (JsonTokenAndQueue) bundle.getSerializableExtra(IBConstant.KEY_JSON_TOKEN_QUEUE);
            Log.d("AfterJoin bundle", jsonTokenAndQueue.toString());
            if (null != jsonTokenAndQueue) {
                currencySymbol = AppUtils.getCurrencySymbol(jsonTokenAndQueue.getCountryShortName());
            }
            codeQR = bundle.getStringExtra(IBConstant.KEY_CODE_QR);
            topic = jsonTokenAndQueue.getTopic();
            tokenValue = String.valueOf(jsonTokenAndQueue.getToken());
            tv_queue_name.setText(jsonTokenAndQueue.getDisplayName());
            tv_business_name.setText(jsonTokenAndQueue.getBusinessName());
            tv_address.setText(jsonTokenAndQueue.getStoreAddress());
            queueUserId = bundle.getStringExtra("qUserId");
            List<JsonProfile> profileList = new ArrayList<>();
            if (UserUtils.isLogin()) {
                profileList = AppInitialize.getAllProfileList();
            }

            afterJoinViewModel.insertTokenAndQueue(jsonTokenAndQueue);

            if (!TextUtils.isEmpty(queueUserId)) {
                jsonProfile = AppUtils.getJsonProfileQueueUserID(queueUserId, profileList);
                tv_name.setText(TextUtils.isEmpty(jsonProfile.getName()) ? "" : jsonProfile.getName());
            }

            String imageUrl = bundle.getStringExtra(IBConstant.KEY_IMAGE_URL);
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get().load(imageUrl)
                        .placeholder(ContextCompat.getDrawable(this, R.drawable.profile_theme))
                        .error(ContextCompat.getDrawable(this, R.drawable.profile_theme)).into(iv_profile);
            } else {
                Picasso.get().load(R.drawable.profile_theme).into(iv_profile);
            }
            actionbarBack.setOnClickListener((View v) -> iv_home.performClick());
            iv_home.setOnClickListener((View v) -> {
                AppInitialize.activityCommunicator = null;
                Intent goToA = new Intent(AfterJoinActivity.this, HomeActivity.class);
                startActivity(goToA);
                finish();
            });

            switch (jsonTokenAndQueue.getBusinessType()) {
                case DO:
                case PH:
                    tv_add.setVisibility(View.VISIBLE);
                    tv_name.setVisibility(View.VISIBLE);
                    iv_left_bg.setVisibility(View.VISIBLE);
                    iv_right_bg.setVisibility(View.VISIBLE);
                    break;
                default:
                    tv_add.setVisibility(View.GONE);
                    tv_name.setVisibility(View.GONE);
                    iv_left_bg.setVisibility(View.GONE);
                    iv_right_bg.setVisibility(View.GONE);
            }
            if (jsonTokenAndQueue.getDelayedInMinutes() > 0) {
                int hours = jsonTokenAndQueue.getDelayedInMinutes() / 60;
                int minutes = jsonTokenAndQueue.getDelayedInMinutes() % 60;
                String red = "<b>Delayed by " + hours + " Hrs " + minutes + " minutes.</b>";
                tv_delay_in_time.setText(Html.fromHtml(red));
            } else {
                tv_delay_in_time.setVisibility(View.GONE);
            }
            String time = new AppUtils().formatTodayStoreTiming(this, jsonTokenAndQueue.getStartHour(), jsonTokenAndQueue.getEndHour());
            tv_hour_saved.setText(time);
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonTokenAndQueue.getCountryShortName(), jsonTokenAndQueue.getStorePhone()));
            tv_mobile.setOnClickListener((View v) -> AppUtils.makeCall(AfterJoinActivity.this, tv_mobile.getText().toString()));
            tv_address.setOnClickListener((View v) -> AppUtils.openNavigationInMap(AfterJoinActivity.this, tv_address.getText().toString()));

            afterJoinViewModel.getReviewData(codeQR, tokenValue).observe(this, reviewData -> {
                if (reviewData != null) {
                    gotoPerson = reviewData.getGotoCounter();
                } else {
                    gotoPerson = "";
                }
            });

            //tv_serving_no.setText(String.valueOf(jsonTokenAndQueue.getServingNumber()));
            tv_token.setText(jsonTokenAndQueue.getDisplayToken());
            tv_position_in_queue.setText(String.valueOf(jsonTokenAndQueue.afterHowLong()));
            // Store the currently serving and avg wait time in the app preference
            SharedPreferences prefs = this.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE);
            prefs.edit().putInt(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR), jsonTokenAndQueue.getServingNumber()).apply();
            if (jsonTokenAndQueue.getAverageServiceTime() != 0) {
                prefs.edit().putLong(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR), jsonTokenAndQueue.getAverageServiceTime()).apply();
            }
            updateEstimatedTime();
            setBackGround(jsonTokenAndQueue.afterHowLong() > 0 ? jsonTokenAndQueue.afterHowLong() : 0);
            if (null != jsonProfile && null != jsonProfile.getName()) {
                //TODO(chandra) this is not required as this is set above
                tv_name.setText(jsonProfile.getName());
            }
            tv_vibrator_off.setVisibility(isVibratorOff() ? View.VISIBLE : View.GONE);
            generateQRCode();
            if (bundle.getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
                if (!TextUtils.isEmpty(jsonTokenAndQueue.getTransactionId())) {
                    setProgressMessage("Fetching Queue data...");
                    showProgress();
                    queueApiAuthenticCall.purchaseOrder(
                            UserUtils.getDeviceId(),
                            UserUtils.getEmail(),
                            UserUtils.getAuth(),
                            String.valueOf(jsonTokenAndQueue.getToken()),
                            codeQR);
                }
            } else {
                queueJsonPurchaseOrderResponse(jsonTokenAndQueue.getJsonPurchaseOrder());
            }
        }
    }

    private void observeValues() {

        afterJoinViewModel.getForegroundNotificationLiveData().observe(this, new Observer<ForegroundNotificationModel>() {
            @Override
            public void onChanged(ForegroundNotificationModel foregroundNotificationModel) {
                if (foregroundNotificationModel != null) {
                    handleBuzzer(foregroundNotificationModel);
                }
            }
        });

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
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic + "_A");
        afterJoinViewModel.deleteTokenAndQueue(codeQR, tokenValue);
        // Clear entry from App preferences
        SharedPreferences prefs = this.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE);
        prefs.edit().remove(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR)).apply();
        prefs.edit().remove(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR)).apply();
        onBackPressed();
        dismissProgress();
    }

    @Override
    public void responsePresenterError() {
        Log.d("", "responsePresenterError");
        dismissProgress();
    }

    private void cancelQueue() {
        if (isOnline()) {
            setProgressMessage("Cancel Queue");
            showProgress();
            if (UserUtils.isLogin()) {
                queueApiAuthenticCall.setResponsePresenter(this);
                queueApiAuthenticCall.abortQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
            } else {
                queueApiUnAuthenticCall.setResponsePresenter(this);
                queueApiUnAuthenticCall.abortQueue(UserUtils.getDeviceId(), codeQR);
            }
            if (AppUtils.isRelease()) {
                try {
                    String displayName = null != jsonTokenAndQueue ? jsonTokenAndQueue.getDisplayName() : "N/A";
                    Bundle params = new Bundle();
                    params.putString("Queue_Name", displayName);
                    AppInitialize.getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_CANCEL_QUEUE, params);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public void couponApplyResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        Log.e("Coupon apply data: ", jsonPurchaseOrder.toString());
        jsonToken.getJsonPurchaseOrder().setOrderPrice(jsonPurchaseOrder.getOrderPrice());
        jsonToken.getJsonPurchaseOrder().setStoreDiscount(jsonPurchaseOrder.getStoreDiscount());
        jsonToken.getJsonPurchaseOrder().setCouponId(jsonPurchaseOrder.getCouponId());
        queueJsonPurchaseOrderResponse(jsonToken.getJsonPurchaseOrder());
        dismissProgress();
    }

    @Override
    public void couponRemoveResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        Log.e("Coupon remove data: ", jsonPurchaseOrder.toString());
        new CustomToast().showToast(this, "Coupon removed successfully");
        jsonToken.getJsonPurchaseOrder().setOrderPrice(jsonPurchaseOrder.getOrderPrice());
        jsonToken.getJsonPurchaseOrder().setStoreDiscount(jsonPurchaseOrder.getStoreDiscount());
        jsonToken.getJsonPurchaseOrder().setCouponId(jsonPurchaseOrder.getCouponId());
        queueJsonPurchaseOrderResponse(jsonToken.getJsonPurchaseOrder());
        dismissProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Added to update the screen if app is in background & notification received */
        if (!isResumeFirst) {
            afterJoinViewModel.getCurrentQueueObject(codeQR, tokenValue);
            afterJoinViewModel.getCurrentQueueObjectLiveData().observe(this, new Observer<JsonTokenAndQueue>() {
                @Override
                public void onChanged(JsonTokenAndQueue jsonTokenAndQueue) {
                    if (jsonTokenAndQueue != null) {
                        if (TextUtils.isEmpty(gotoPerson)) {
                            afterJoinViewModel.getReviewData(codeQR, tokenValue).observe(AfterJoinActivity.this, reviewData -> {
                                if (reviewData != null) {
                                    gotoPerson = reviewData.getGotoCounter();
                                } else {
                                    gotoPerson = "";
                                }
                            });
                        }
                        setObject(jsonTokenAndQueue, gotoPerson);
                    }
                }
            });
        }
        if (isResumeFirst) {
            isResumeFirst = false;
        }
    }

    public String getCodeQR() {
        return codeQR;
    }

    public void setBackGround(int pos) {
        tv_position_in_queue_label.setText(R.string.position_in_queue_label);
        btn_cancel_queue.setEnabled(true);
        switch (pos) {
            case 0:
                tv_position_in_queue_label.setText(R.string.your_turn);
                ll_change_bg.setBackgroundResource(R.drawable.green_gradient);
                tv_position_in_queue.setText(gotoPerson);
                btn_cancel_queue.setVisibility(View.GONE);
                break;
            case 1:
                tv_position_in_queue_label.setText(R.string.your_are_next_label);
                ll_change_bg.setBackgroundResource(R.drawable.green_gradient);
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                ll_change_bg.setBackgroundResource(R.drawable.green_gradient);
                break;
            default:
                tv_position_in_queue_label.setText(R.string.position_in_queue_label);
                break;
        }
    }

    public void setObject(JsonTokenAndQueue jq, String go_to) {
        gotoPerson = go_to;
        // jsonTokenAndQueue = jq; removed to avoided the override of the data
        jsonTokenAndQueue.setServingNumber(jq.getServingNumber());
        jsonTokenAndQueue.setToken(jq.getToken());
        //tv_serving_no.setText(String.valueOf(jsonTokenAndQueue.getServingNumber()));
        tv_token.setText(jsonTokenAndQueue.getDisplayToken());
        tv_position_in_queue.setText(String.valueOf(jsonTokenAndQueue.afterHowLong()));
        updateEstimatedTime();
        setBackGround(jq.afterHowLong() > 0 ? jq.afterHowLong() : 0);
    }

    private void updateEstimatedTime() {
        try {
            long avgServiceTime = jsonTokenAndQueue.getAverageServiceTime();
            if (avgServiceTime == 0) {
                SharedPreferences prefs = this.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE);
                avgServiceTime = prefs.getLong(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, jsonTokenAndQueue.getCodeQR()), 0);
            }
            switch (jsonTokenAndQueue.getBusinessType()) {
                case CD:
                case CDQ:
                    String slot = jsonTokenAndQueue.getTimeSlotMessage();
                    tv_estimated_time.setText(slot);
                    tv_left.setText(R.string.time_slot);
                    break;
                default:
                    String waitTime = TokenStatusUtils.calculateEstimatedWaitTime(
                            avgServiceTime,
                            jsonTokenAndQueue.afterHowLong(),
                            jsonTokenAndQueue.getQueueStatus(),
                            jsonTokenAndQueue.getStartHour(),
                            this);
                    if (!TextUtils.isEmpty(waitTime)) {
                        tv_estimated_time.setText(waitTime);
                        tv_left.setText(R.string.wait_time);
                    }
            }
        } catch (Exception e) {
            Log.e("", "Error setting estimated wait time reason: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppInitialize.activityCommunicator = null;
    }

    @Override
    public boolean updateUI(String qrCode, JsonTokenAndQueue jq, String go_to) {
        if (codeQR.equals(qrCode) && (Integer.parseInt(tokenValue) >= jq.getServingNumber())) {
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
            if (null == getParent()) {
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
    public void queueJsonPurchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        // Todo: If queue-only store then return
        generateQRCode();
        try {
            if (null != jsonPurchaseOrder) {
                Log.e("Response: ", jsonPurchaseOrder.toString());
                frame_coupon.setVisibility(View.VISIBLE);
                this.jsonTokenAndQueue.setJsonPurchaseOrder(jsonPurchaseOrder);
                LinearLayout.LayoutParams params = setLayoutWidthParams(false);
                params.setMargins(0, 0, 20, 0);
                btn_pay.setLayoutParams(params);
                btn_cancel_queue.setLayoutParams(setLayoutWidthParams(false));
                if (null == jsonToken) {
                    jsonToken = new JsonToken();
                }
                jsonToken.setJsonPurchaseOrder(jsonPurchaseOrder);
                card_amount.setVisibility(View.VISIBLE);
                // tv_due_amt.setText(currencySymbol + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
                tv_total_order_amt.setText(currencySymbol + jsonPurchaseOrder.computeFinalAmountWithDiscount());
                tv_grand_total_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
                tv_coupon_amount.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
                if (null != jsonPurchaseOrder.getJsonCoupon()) {
                    /* This condition works but this elements is not displayed. */
                    tv_coupon_name.setText(jsonPurchaseOrder.getJsonCoupon().getDiscountName());
                }
                if (TextUtils.isEmpty(jsonPurchaseOrder.getCouponId())) {
                    rl_discount.setVisibility(View.GONE);
                } else {
                    tv_coupon_discount_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
                }
                ll_order_details.removeAllViews();
                for (int i = 0; i < jsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
                    JsonPurchaseOrderProduct jsonPurchaseOrderProduct = jsonPurchaseOrder.getPurchaseOrderProducts().get(i);
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
                    TextView tv_title = inflatedLayout.findViewById(R.id.tv_title);
                    TextView tv_total_price = inflatedLayout.findViewById(R.id.tv_total_price);
                    tv_title.setText(jsonPurchaseOrderProduct.getProductName()
                            + " "
                            + AppUtils.getPriceWithUnits(jsonPurchaseOrderProduct.getJsonStoreProduct())
                            + " " + currencySymbol
                            + CommonHelper.displayPrice(jsonPurchaseOrderProduct.getProductPrice())
                            + " x "
                            + jsonPurchaseOrderProduct.getProductQuantity());
                    tv_total_price.setText(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jsonPurchaseOrderProduct.getProductPrice()).multiply(new BigDecimal(jsonPurchaseOrderProduct.getProductQuantity())).toString()));
                    ll_order_details.addView(inflatedLayout);
                }
                if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
                    tv_payment_status.setText("Paid via: " + jsonPurchaseOrder.getPaymentMode().getDescription());
                    btn_pay.setVisibility(View.GONE);
                    btn_cancel_queue.setLayoutParams(setLayoutWidthParams(true));
                    frame_coupon.setVisibility(View.GONE);
                    rl_apply_coupon.setClickable(false);
                } else {
                    tv_payment_status.setText("Payment status: " + jsonPurchaseOrder.getPaymentStatus().getDescription());
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

                if (TextUtils.isEmpty(jsonPurchaseOrder.getOrderPrice()) || 0 == Integer.parseInt(jsonPurchaseOrder.getOrderPrice())) {
                    frame_coupon.setVisibility(View.GONE);
                    rl_discount.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dismissProgress();
    }

    @Override
    public void paymentInitiateResponse(JsonResponseWithCFToken jsonResponseWithCFToken) {
        if (null != jsonResponseWithCFToken) {
            jsonToken.getJsonPurchaseOrder().setJsonResponseWithCFToken(jsonResponseWithCFToken);
            triggerOnlinePayment();
        }
        dismissProgress();
    }

    private void triggerOnlinePayment() {
        if (AppInitialize.isEmailVerified()) {
            String token = jsonToken.getJsonPurchaseOrder().getJsonResponseWithCFToken().getCftoken();
            String stage = BuildConfig.CASHFREE_STAGE;
            String appId = BuildConfig.CASHFREE_APP_ID;
            String orderId = jsonToken.getJsonPurchaseOrder().getTransactionId();
            String orderAmount = jsonToken.getJsonPurchaseOrder().getJsonResponseWithCFToken().getOrderAmount();
            String orderNote = "Order: " + queueUserId;
            String customerName = AppInitialize.getCustomerNameWithQid(tv_name.getText().toString(), queueUserId);
            String customerPhone = AppInitialize.getOfficePhoneNo();
            String customerEmail = AppInitialize.getOfficeMail();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITY_RESULT_BACK) {
            if (RESULT_OK == resultCode) {
                JsonCoupon jsonCoupon = (JsonCoupon) data.getSerializableExtra(IBConstant.KEY_DATA_OBJECT);
                Log.e("Data received", jsonCoupon.toString());

                if (isOnline()) {
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
                    ShowAlertInformation.showNetworkDialog(AfterJoinActivity.this);
                }
            }
        }
    }

    private LinearLayout.LayoutParams setLayoutWidthParams(boolean isMatchParent) {
        if (isMatchParent) {
            return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120, 1.0f);
        } else {
            return new LinearLayout.LayoutParams(0, 120, 0.49f);
        }
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

    public void tokenPresenterResponse(JsonToken jsonToken) {
        Log.d(TAG, jsonToken.toString());
        this.jsonToken = jsonToken;
        tokenValue = String.valueOf(jsonToken.getToken());
        btn_cancel_queue.setEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic(topic + "_A");
        jsonTokenAndQueue.setServingNumber(jsonToken.getServingNumber());
        jsonTokenAndQueue.setToken(jsonToken.getToken());
        jsonTokenAndQueue.setDisplayToken(jsonToken.getDisplayToken());
        jsonTokenAndQueue.setQueueUserId(queueUserId);
        //save data to DB
        afterJoinViewModel.insertTokenAndQueue(jsonTokenAndQueue);
        generateQRCode();
        dismissProgress();
    }

    private void generateQRCode() {
        String authenticateInQueue = "https://q.noqapp.com/" + codeQR + "#" + tokenValue + "#" + "_" + MessageOriginEnum.AU;
        Writer writer = new QRCodeWriter();
        int width = 250;
        int height = 250;
        try {
            BitMatrix bm = writer.encode(authenticateInQueue, BarcodeFormat.QR_CODE, width, height);

            Bitmap imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    imageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            iv_codeqr.setVisibility(View.VISIBLE);
            tv_token_day.setVisibility(View.VISIBLE);
            tv_token_time.setVisibility(View.VISIBLE);
            iv_codeqr.setImageBitmap(imageBitmap);

            SimpleDateFormat sdf = new SimpleDateFormat("EEE/dd", Locale.getDefault());
            tv_token_day.setText(CommonHelper.formatStringDate(sdf, jsonTokenAndQueue.getServiceEndTime()));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void handleBuzzer(ForegroundNotificationModel foregroundNotification) {
        afterJoinViewModel.getCurrentQueueObjectList(foregroundNotification.getQrCode());

        afterJoinViewModel.getCurrentQueueObjectListLiveData().observe(this, jsonTokenAndQueues -> {
            if (jsonTokenAndQueues != null) {
                for (JsonTokenAndQueue jtk : jsonTokenAndQueues) {
                    if (Integer.parseInt(foregroundNotification.getCurrentServing()) == jtk.getToken()) {
                        if (MessageOriginEnum.valueOf(foregroundNotification.getMessageOrigin()) == MessageOriginEnum.Q) {
                            Intent blinkerIntent =new Intent(AfterJoinActivity.this, BlinkerActivity.class);
                            blinkerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(blinkerIntent);
                            if (AppInitialize.isMsgAnnouncementEnable()) {
                                if(foregroundNotification.getJsonTextToSpeeches() != null) {
                                    makeAnnouncement(foregroundNotification.getJsonTextToSpeeches(), foregroundNotification.getMsgId());
                                }
                            }
                        } else if (MessageOriginEnum.valueOf(foregroundNotification.getMessageOrigin()) == MessageOriginEnum.O) {
                            if (foregroundNotification.getPurchaseOrderStateEnum() == PurchaseOrderStateEnum.RD) {
                                Intent blinkerIntent =new Intent(AfterJoinActivity.this, BlinkerActivity.class);
                                blinkerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(blinkerIntent);
                                if (AppInitialize.isMsgAnnouncementEnable()) {
                                    if(foregroundNotification.getJsonTextToSpeeches() != null) {
                                        makeAnnouncement(foregroundNotification.getJsonTextToSpeeches(), foregroundNotification.getMsgId());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        afterJoinViewModel.deleteForegroundNotification();
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
