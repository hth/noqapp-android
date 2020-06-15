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

import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;
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
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.network.NoQueueMessagingService;
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
import com.noqapp.android.common.beans.JsonCoupon;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.CouponOnOrder;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;
import com.noqapp.android.common.beans.payment.cashfree.JsonResponseWithCFToken;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.presenter.CouponApplyRemovePresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
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

/**
 * Created by chandra on 5/7/17.
 */
public class AfterJoinActivity
        extends BaseActivity
        implements ResponsePresenter, ActivityCommunicator, QueueJsonPurchaseOrderPresenter, CouponApplyRemovePresenter,
        CFClientInterface, CashFreeNotifyQPresenter {
    private static final String TAG = AfterJoinActivity.class.getSimpleName();
    private TextView tv_address;
    private TextView tv_mobile;
    private TextView tv_serving_no;
    private TextView tv_token;
    private TextView tv_position_in_queue_label;
    private TextView tv_position_in_queue;
    private Button btn_cancel_queue;
    private TextView tv_estimated_time;
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
    private LinearLayout ll_qr_token;
    private TextView tv_token_qr, tv_token_day, tv_token_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_join);
        new InitPaymentGateway().execute();
        TextView tv_queue_name = findViewById(R.id.tv_queue_name);
        tv_address = findViewById(R.id.tv_address);
        TextView tv_delay_in_time = findViewById(R.id.tv_delay_in_time);
        ImageView iv_right_bg = findViewById(R.id.iv_right_bg);
        ImageView iv_left_bg = findViewById(R.id.iv_left_bg);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_serving_no = findViewById(R.id.tv_serving_no);
        tv_token = findViewById(R.id.tv_token);
        tv_position_in_queue = findViewById(R.id.tv_position_in_queue_value);
        btn_cancel_queue = findViewById(R.id.btn_cancel_queue);
        tv_position_in_queue_label = findViewById(R.id.tv_position_in_queue_label);
        tv_payment_status = findViewById(R.id.tv_payment_status);
        tv_due_amt = findViewById(R.id.tv_due_amt);
        tv_total_order_amt = findViewById(R.id.tv_total_order_amt);
        TextView tv_hour_saved = findViewById(R.id.tv_hour_saved);
        tv_estimated_time = findViewById(R.id.tv_estimated_time);
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
        ll_qr_token = findViewById(R.id.ll_qr_token);
        tv_token_qr = findViewById(R.id.tv_token_qr);
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
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
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

                        clientCouponApiCalls.remove(UserUtils.getDeviceId(),
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
            showDialog.displayDialog("Quit Queue", "Choose OK to quit the queue. You will loose your current token number.");
        });
        btn_pay.setOnClickListener((View v) -> {
            if (new BigDecimal(jsonToken.getJsonPurchaseOrder().getOrderPrice()).intValue() > 0) {
                pay();
            } else {
                //do nothing
            }
        });
        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.screen_qdetails));
        queueApiUnAuthenticCall = new QueueApiUnAuthenticCall();
        queueApiAuthenticCall = new QueueApiAuthenticCall();
        queueApiAuthenticCall.setQueueJsonPurchaseOrderPresenter(this);
        LaunchActivity.getLaunchActivity().activityCommunicator = this;
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
            actionbarBack.setOnClickListener((View v) -> iv_home.performClick());
            iv_home.setOnClickListener((View v) -> {
                LaunchActivity.getLaunchActivity().activityCommunicator = null;
                Intent goToA = new Intent(AfterJoinActivity.this, LaunchActivity.class);
                if (LaunchActivity.isLockMode) {
                    goToA.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                } else {
                    goToA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
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
            tv_address.setOnClickListener((View v) -> AppUtils.openNavigationInMap(AfterJoinActivity.this,
                    tv_address.getText().toString()));
            gotoPerson = (null != ReviewDB.getValue(codeQR, tokenValue)) ? ReviewDB.getValue(codeQR, tokenValue).getGotoCounter() : "";
            tv_serving_no.setText(String.valueOf(jsonTokenAndQueue.getServingNumber()));
            tv_token.setText(String.valueOf(jsonTokenAndQueue.getToken()));
            tv_position_in_queue.setText(String.valueOf(jsonTokenAndQueue.afterHowLong()));
            // Store the currently serving and avg wait time in the app preference
            SharedPreferences prefs = this.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE);
            prefs.edit().putInt(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR), jsonTokenAndQueue.getServingNumber()).apply();
            if (jsonTokenAndQueue.getAverageServiceTime() != 0) {
                prefs.edit().putLong(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR), jsonTokenAndQueue.getAverageServiceTime()).apply();
            }
            updateEstimatedTime();
            setBackGround(jsonTokenAndQueue.afterHowLong() > 0 ? jsonTokenAndQueue.afterHowLong() : 0);
            tv_name.setText(jsonProfile.getName());
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
        NoQueueMessagingService.unSubscribeTopics(topic);
        TokenAndQueueDB.deleteTokenQueue(codeQR, tokenValue);
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
        if (LaunchActivity.getLaunchActivity().isOnline()) {
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
                    LaunchActivity.getLaunchActivity().getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_CANCEL_QUEUE, params);
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
        tv_position_in_queue_label.setText(R.string.your_position_in_queue_label);
        btn_cancel_queue.setEnabled(true);
        switch (pos) {
            case 0:
                tv_position_in_queue_label.setText(R.string.your_turn);
                tv_position_in_queue.setText(gotoPerson);
                btn_cancel_queue.setVisibility(View.GONE);
                break;
            case 1:
                tv_position_in_queue_label.setText(R.string.your_are_next_label);
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                break;
            default:
                tv_position_in_queue_label.setText(R.string.your_position_in_queue_label);
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
                    String slot = TokenStatusUtils.timeSlot(jsonTokenAndQueue.getServiceEndTime());
                    tv_estimated_time.setText(String.format(getString(R.string.time_slot), slot));
                    break;
                default:
                    String waitTime = TokenStatusUtils.calculateEstimatedWaitTime(
                            avgServiceTime,
                            jsonTokenAndQueue.afterHowLong(),
                            jsonTokenAndQueue.getQueueStatus(),
                            jsonTokenAndQueue.getStartHour());
                    if (!TextUtils.isEmpty(waitTime)) {
                        tv_estimated_time.setText(String.format(getString(R.string.estimated_time), waitTime));
                    }
            }
        } catch (Exception e) {
            Log.e("", "Error setting estimated wait time reason: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LaunchActivity.getLaunchActivity().activityCommunicator = null;
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
        try {
            generateQRCode();
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
            if (null != jsonPurchaseOrder.getJsonCoupon())
                tv_coupon_name.setText(jsonPurchaseOrder.getJsonCoupon().getDiscountName());
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
                tv_total_price.setText(currencySymbol
                        + CommonHelper.displayPrice(new BigDecimal(jsonPurchaseOrderProduct.getProductPrice()).multiply(new BigDecimal(jsonPurchaseOrderProduct.getProductQuantity())).toString()));
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
            new CustomToast().showToast(this, "To pay, email is mandatory. In your profile add and verify email");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITY_RESULT_BACK) {
            if (RESULT_OK == resultCode) {
                JsonCoupon jsonCoupon = (JsonCoupon) data.getSerializableExtra(IBConstant.KEY_DATA_OBJECT);
                Log.e("Data received", jsonCoupon.toString());

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

    public void tokenPresenterResponse(JsonToken token) {
        Log.d(TAG, token.toString());
        this.jsonToken = token;
        tokenValue = String.valueOf(token.getToken());
        btn_cancel_queue.setEnabled(true);
        NoQueueMessagingService.subscribeTopics(topic);
        jsonTokenAndQueue.setServingNumber(token.getServingNumber());
        jsonTokenAndQueue.setToken(token.getToken());
        jsonTokenAndQueue.setQueueUserId(queueUserId);
        //save data to DB
        TokenAndQueueDB.saveJoinQueueObject(jsonTokenAndQueue);
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
            ll_qr_token.setVisibility(View.VISIBLE);
            tv_token_qr.setVisibility(View.VISIBLE);
            tv_token_day.setVisibility(View.VISIBLE);
            tv_token_time.setVisibility(View.VISIBLE);
            iv_codeqr.setImageBitmap(imageBitmap);
            tv_token_qr.setText(tokenValue);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/EEE", Locale.getDefault());
            tv_token_day.setText(CommonHelper.formatStringDate(sdf, jsonTokenAndQueue.getCreateDate()));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
