package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiAuthenticCall;
import com.noqapp.android.client.model.QueueApiUnAuthenticCall;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.presenter.CashFreeNotifyQPresenter;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.GetTimeAgoUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.DependentAdapter;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.JoinQueue;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AfterJoinActivity extends BaseActivity implements TokenPresenter, ResponsePresenter, ActivityCommunicator, CFClientInterface,  CashFreeNotifyQPresenter {
    private static final String TAG = AfterJoinActivity.class.getSimpleName();
    private TextView tv_address;
    private TextView tv_mobile;
    private TextView tv_serving_no;
    private TextView tv_token;
    private TextView tv_how_long;
    private Button btn_cancel_queue;
    private TextView tv_after;
    private TextView tv_estimated_time;
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
    private int profile_pos;
    private List<JsonProfile> profileList;
    private String queueUserId = "";
    private QueueApiUnAuthenticCall queueApiUnAuthenticCall;
    private QueueApiAuthenticCall queueApiAuthenticCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_join);
        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_queue_name = findViewById(R.id.tv_queue_name);
        tv_address = findViewById(R.id.tv_address);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_serving_no = findViewById(R.id.tv_serving_no);
        tv_token = findViewById(R.id.tv_token);
        tv_how_long = findViewById(R.id.tv_how_long);
        btn_cancel_queue = findViewById(R.id.btn_cancel_queue);
        tv_after = findViewById(R.id.tv_after);
        TextView tv_hour_saved = findViewById(R.id.tv_hour_saved);
        tv_estimated_time = findViewById(R.id.tv_estimated_time);
        TextView tv_add = findViewById(R.id.add_person);
        tv_vibrator_off = findViewById(R.id.tv_vibrator_off);
        ll_change_bg = findViewById(R.id.ll_change_bg);
        TextView tv_name = findViewById(R.id.tv_name);
        ImageView iv_profile = findViewById(R.id.iv_profile);
        btn_cancel_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelQueue();
            }
        });
        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.screen_qdetails));
        queueApiUnAuthenticCall = new QueueApiUnAuthenticCall();
        queueApiAuthenticCall = new QueueApiAuthenticCall();
        LaunchActivity.getLaunchActivity().activityCommunicator = this;
        Intent bundle = getIntent();
        if (null != bundle) {
            jsonQueue = (JsonQueue) bundle.getSerializableExtra(IBConstant.KEY_JSON_QUEUE);
            jsonTokenAndQueue = (JsonTokenAndQueue) bundle.getSerializableExtra(IBConstant.KEY_JSON_TOKEN_QUEUE);
            Log.d("AfterJoin bundle", jsonTokenAndQueue.toString());
            codeQR = bundle.getStringExtra(IBConstant.KEY_CODE_QR);
            topic = jsonTokenAndQueue.getTopic();
            tokenValue = String.valueOf(jsonTokenAndQueue.getToken());
            tv_store_name.setText(jsonTokenAndQueue.getBusinessName());
            tv_queue_name.setText(jsonTokenAndQueue.getDisplayName());
            tv_address.setText(jsonTokenAndQueue.getStoreAddress());
            profile_pos = bundle.getIntExtra("profile_pos", 1);
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
                    Intent goToA = new Intent(AfterJoinActivity.this, LaunchActivity.class);
                    goToA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(goToA);
                }
            });
            if (UserUtils.isLogin()) {
                profileList = NoQueueBaseActivity.getUserProfile().getDependents();
                profileList.add(0, NoQueueBaseActivity.getUserProfile());
                profileList.add(0, new JsonProfile().setName("Select Patient"));
                DependentAdapter adapter = new DependentAdapter(this, profileList);
                tv_name.setText(profileList.get(profile_pos).getName());
                queueUserId = profileList.get(profile_pos).getQueueUserId();
            }
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
            String time = new AppUtilities().formatTodayStoreTiming(this,jsonTokenAndQueue.getStartHour(),jsonTokenAndQueue.getEndHour());;
            if (jsonTokenAndQueue.getDelayedInMinutes() > 0) {
                String red = "<font color='#e92270'><b>Late " + jsonTokenAndQueue.getDelayedInMinutes() + " minutes.</b></font>";
                time = time + " " + red;
            }
            tv_hour_saved.setText(Html.fromHtml(time));
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonTokenAndQueue.getCountryShortName(), jsonTokenAndQueue.getStorePhone()));
            tv_mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtilities.makeCall(AfterJoinActivity.this, tv_mobile.getText().toString());
                }
            });
            tv_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtilities.openAddressInMap(AfterJoinActivity.this, tv_address.getText().toString());
                }
            });
            gotoPerson = null != ReviewDB.getValue(codeQR, tokenValue) ? ReviewDB.getValue(codeQR, tokenValue).getGotoCounter() : "";
            if (bundle.getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
                tv_serving_no.setText(String.valueOf(jsonTokenAndQueue.getServingNumber()));
                tv_token.setText(String.valueOf(jsonTokenAndQueue.getToken()));
                tv_how_long.setText(String.valueOf(jsonTokenAndQueue.afterHowLong()));
                setBackGround(jsonTokenAndQueue.afterHowLong() > 0 ? jsonTokenAndQueue.afterHowLong() : 0);
                tv_name.setText(AppUtilities.getNameFromQueueUserID(jsonTokenAndQueue.getQueueUserId(), profileList));
                tv_vibrator_off.setVisibility(isVibratorOff() ? View.VISIBLE : View.GONE);
                if (isVibratorOff())
                    ShowAlertInformation.showThemeDialog(this, "Vibrator off", getString(R.string.msg_vibrator_off));
            } else {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    if (isResumeFirst) {
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
        if (isVibratorOff())
            ShowAlertInformation.showThemeDialog(this, "Vibrator off", getString(R.string.msg_vibrator_off));
        dismissProgress();
    }

    @Override
    public void paidTokenPresenterResponse(JsonToken token) {
        this.jsonToken = token;
        if (null != token) {
            Log.d(TAG, token.toString());
            if (token.getJsonPurchaseOrder().getPresentOrderState() == PurchaseOrderStateEnum.VB) {
                if(getIntent().getBooleanExtra("isPayBeforeJoin", false)){
                    triggerOnlinePayment();
                }else{
                    tokenPresenterResponse(jsonToken);
                }

            } else {
                Toast.makeText(this, "Order failed.", Toast.LENGTH_LONG).show();
            }
        } else {
            //Show error
        }
        dismissProgress();
    }

    @Override
    public void responsePresenterResponse(JsonResponse response) {
        if (null != response) {
            if (response.getResponse() == 1) {
                Toast.makeText(this, getString(R.string.cancel_queue), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.fail_to_cancel), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.fail_to_cancel), Toast.LENGTH_LONG).show();
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


    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        if (errorCode == Constants.INVALID_BAR_CODE) {
            ShowAlertInformation.showBarcodeErrorDialog(this);
        } else {
            new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
        }
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }

    private void cancelQueue() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            if (UserUtils.isLogin()) {
                queueApiAuthenticCall.setResponsePresenter(this);
                queueApiAuthenticCall.abortQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
            } else {
                queueApiUnAuthenticCall.setResponsePresenter(this);
                queueApiUnAuthenticCall.abortQueue(UserUtils.getDeviceId(), codeQR);
            }
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    private void callQueue() {
        if (codeQR != null) {
            if (UserUtils.isLogin()) {
                JsonProfile jsonProfile = NoQueueBaseActivity.getUserProfile();
                String queueUserId;
                String guardianId = null;
                if (profile_pos > 1) {
                    queueUserId = profileList.get(profile_pos).getQueueUserId();
                    guardianId = jsonProfile.getQueueUserId();
                } else {
                    queueUserId = jsonProfile.getQueueUserId();
                }
                JoinQueue joinQueue = new JoinQueue().setCodeQR(codeQR).setQueueUserId(queueUserId).setGuardianQid(guardianId);
                queueApiAuthenticCall.setTokenPresenter(this);
                if (jsonQueue.isEnabledPayment()) {
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
                ll_change_bg.setBackgroundResource(R.drawable.grey_gradient);
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
        if (codeQR.equals(qrCode) && tokenValue.equals(String.valueOf(jq.getToken()))) {
            //updating the serving status
            setObject(jq, go_to);
            if (jq.afterHowLong() > 0)
                return false;
            else
                return true;
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
        jsonCashfreeNotification.setPaymentMode(map.get("paymentMode"));
        jsonCashfreeNotification.setSignature(map.get("signature"));
        jsonCashfreeNotification.setOrderAmount(map.get("orderAmount"));
        jsonCashfreeNotification.setTxStatus(map.get("txStatus"));
        jsonCashfreeNotification.setOrderId(map.get("orderId"));
        queueApiAuthenticCall.cashFreeQNotify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonCashfreeNotification);
    }

    @Override
    public void onFailure(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Failure");
        Toast.makeText(this, "Transaction Failed", Toast.LENGTH_LONG).show();
        // enableDisableOrderButton(false);
        finish();
    }

    @Override
    public void onNavigateBack() {
        Log.e("User Navigate Back", "Back without payment");
        Toast.makeText(this, "You canceled the transaction.Please try again", Toast.LENGTH_LONG).show();
        //enableDisableOrderButton(false);
        finish();
    }

    private void triggerOnlinePayment() {
        String token = jsonToken.getJsonPurchaseOrder().getJsonResponseWithCFToken().getCftoken();
        String stage = Constants.stage;
        String appId = Constants.appId;
        String orderId = jsonToken.getJsonPurchaseOrder().getTransactionId();
        String orderAmount = jsonToken.getJsonPurchaseOrder().getJsonResponseWithCFToken().getOrderAmount();
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
    public void cashFreeNotifyQResponse(JsonToken jsonToken) {
        if (PaymentStatusEnum.PA == jsonToken.getJsonPurchaseOrder().getPaymentStatus()) {
            Toast.makeText(this, "Token generated successfully.", Toast.LENGTH_LONG).show();
            tokenPresenterResponse(jsonToken);
        } else {
            Toast.makeText(this, jsonToken.getJsonPurchaseOrder().getTransactionMessage(), Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "Failed to notify server.", Toast.LENGTH_LONG).show();
        }
    }
}
