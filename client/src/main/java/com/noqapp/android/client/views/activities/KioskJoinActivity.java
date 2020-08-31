package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.KioskApiCalls;
import com.noqapp.android.client.model.QueueApiAuthenticCall;
import com.noqapp.android.client.model.QueueApiUnAuthenticCall;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.wrapper.JoinQueueState;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.JoinQueueUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.DependentAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.body.JoinQueue;
import com.noqapp.android.common.customviews.CustomToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class KioskJoinActivity extends BaseActivity implements QueuePresenter, TokenPresenter {
    private final String TAG = KioskJoinActivity.class.getSimpleName();
    private TextView tv_consult_fees, tv_cancellation_fees;
    private TextView tv_serving_no;
    private TextView tv_people_in_q;
    private TextView tv_hour_saved;
    private TextView tv_rating_review;
    private TextView tv_add, add_person;
    private TextView tv_rating;
    private TextView tv_delay_in_time;
    private TextView tv_timer;
    private Spinner sp_name_list;
    private String codeQR;
    private JsonQueue jsonQueue;
    private boolean isJoinNotPossible = false;
    private String joinErrorMsg = "";
    private Button btn_joinQueue;
    private ImageView iv_right_bg, iv_left_bg;
    private TextView tv_right, tv_left, tv_name;
    private int time = Constants.SCREEN_TIME_OUT / 1000;
    private CountDownTimer waitTimer = null;
    private TextView tv_queue_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_join);
        tv_timer = findViewById(R.id.tv_timer);
        tv_queue_name = findViewById(R.id.tv_queue_name);
        tv_delay_in_time = findViewById(R.id.tv_delay_in_time);
        iv_right_bg = findViewById(R.id.iv_right_bg);
        iv_left_bg = findViewById(R.id.iv_left_bg);
        tv_right = findViewById(R.id.tv_right);
        tv_left = findViewById(R.id.tv_left);
        tv_consult_fees = findViewById(R.id.tv_consult_fees);
        tv_cancellation_fees = findViewById(R.id.tv_cancellation_fees);
        tv_serving_no = findViewById(R.id.tv_serving_no);
        tv_people_in_q = findViewById(R.id.tv_people_in_q);
        tv_hour_saved = findViewById(R.id.tv_hour_saved);
        ImageView iv_profile = findViewById(R.id.iv_profile);
        tv_rating_review = findViewById(R.id.tv_rating_review);
        btn_joinQueue = findViewById(R.id.btn_joinQueue);
        btn_joinQueue.setOnClickListener((View v) -> {
            if (null != jsonQueue)
                joinQueue(false);
        });
        tv_rating = findViewById(R.id.tv_rating);
        tv_add = findViewById(R.id.tv_add);
        tv_name = findViewById(R.id.tv_name);
        add_person = findViewById(R.id.add_person);
        tv_add.setPaintFlags(tv_add.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        sp_name_list = findViewById(R.id.sp_name_list);

        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.screen_join));
        tv_add.setOnClickListener((View v) -> {
            if (UserUtils.isLogin()) {
                Intent loginIntent = new Intent(KioskJoinActivity.this, UserProfileActivity.class);
                startActivity(loginIntent);
            } else {
                new CustomToast().showToast(KioskJoinActivity.this, "Please login to add dependents");
            }
        });

        Intent bundle = getIntent();
        if (null != bundle) {
            codeQR = bundle.getStringExtra(IBConstant.KEY_CODE_QR);
            String imageUrl = bundle.getStringExtra(IBConstant.KEY_IMAGE_URL);
            boolean isDoctor = bundle.getBooleanExtra(IBConstant.KEY_IS_DO, false);
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get().load(imageUrl).
                        placeholder(getResources().getDrawable(R.drawable.profile_theme)).
                        error(getResources().getDrawable(R.drawable.profile_theme)).into(iv_profile);
            } else {
                Picasso.get().load(R.drawable.profile_theme).into(iv_profile);
            }
            if (isDoctor) {
                iv_profile.setVisibility(View.VISIBLE);
            } else {
                iv_profile.setVisibility(View.GONE);
            }

            if (LaunchActivity.getLaunchActivity().isOnline()) {
                setProgressMessage(getString(R.string.loading_queue_details));
                showProgress();
                if (UserUtils.isLogin()) {
                    QueueApiAuthenticCall queueApiAuthenticCall = new QueueApiAuthenticCall();
                    queueApiAuthenticCall.setQueuePresenter(this);
                    queueApiAuthenticCall.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
                } else {
                    QueueApiUnAuthenticCall queueApiUnAuthenticCall = new QueueApiUnAuthenticCall();
                    queueApiUnAuthenticCall.setQueuePresenter(this);
                    queueApiUnAuthenticCall.getQueueState(UserUtils.getDeviceId(), codeQR);
                }
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }
    }

    @Override
    public void queueError() {
        Log.d(TAG, "Queue=Error");
        dismissProgress();
    }

    @Override
    public void queueResponse(JsonQueue jsonQueueTemp) {
        if (null != jsonQueueTemp) {
            Log.d(TAG, "Queue=" + jsonQueueTemp.toString());
            this.jsonQueue = jsonQueueTemp;
            tv_queue_name.setText(jsonQueue.getDisplayName());
            tv_serving_no.setText(String.valueOf(jsonQueue.getServingNumber()));
            tv_people_in_q.setText(String.valueOf(jsonQueue.getPeopleInQueue()));
            if (jsonQueue.getDelayedInMinutes() > 0) {
                int hours = jsonQueue.getDelayedInMinutes() / 60;
                int minutes = jsonQueue.getDelayedInMinutes() % 60;
                String red = "<b>Delayed by " + hours + " Hrs " + minutes + " minutes.</b>";
                tv_delay_in_time.setText(Html.fromHtml(red));
                tv_delay_in_time.setVisibility(View.VISIBLE);
            } else {
                tv_delay_in_time.setVisibility(View.GONE);
            }
            String time = new AppUtils().formatTodayStoreTiming(this, jsonQueue.getStartHour(), jsonQueue.getEndHour());
            tv_hour_saved.setText(time);
            tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tv_rating.setText(String.valueOf(AppUtils.round(jsonQueue.getRating())));
            if (tv_rating.getText().toString().equals("0.0")) {
                tv_rating.setVisibility(View.INVISIBLE);
            } else {
                tv_rating.setVisibility(View.VISIBLE);
            }
            AppUtils.setReviewCountText(jsonQueue.getReviewCount(), tv_rating_review);
            codeQR = jsonQueue.getCodeQR();
            /* Check weather join is possible or not today due to some reason */
            JoinQueueState joinQueueState = JoinQueueUtil.canJoinQueue(jsonQueue, this);
            if (joinQueueState.isJoinNotPossible()) {
                isJoinNotPossible = joinQueueState.isJoinNotPossible();
                joinErrorMsg = joinQueueState.getJoinErrorMsg();
            }
            switch (jsonQueue.getBusinessType()) {
                case DO:
                case HS:
                    String feeString = "<b>" + AppUtils.getCurrencySymbol(jsonQueue.getCountryShortName()) + jsonQueue.getProductPrice() / 100 + "</b>  Consultation fee";
                    tv_consult_fees.setText(Html.fromHtml(feeString));
                    String cancelFeeString = "<b>" + AppUtils.getCurrencySymbol(jsonQueue.getCountryShortName()) + jsonQueue.getCancellationPrice() / 100 + "</b>  Cancellation fee";
                    tv_cancellation_fees.setText(Html.fromHtml(cancelFeeString));
                    tv_consult_fees.setVisibility(View.VISIBLE);
                    tv_cancellation_fees.setVisibility(View.VISIBLE);
                    tv_add.setVisibility(View.VISIBLE);
                    add_person.setVisibility(View.VISIBLE);
                    sp_name_list.setVisibility(View.VISIBLE);
                    iv_left_bg.setVisibility(View.VISIBLE);
                    iv_right_bg.setVisibility(View.VISIBLE);
                    break;
                default:
                    tv_consult_fees.setVisibility(View.GONE);
                    tv_cancellation_fees.setVisibility(View.GONE);
                    tv_add.setVisibility(View.GONE);
                    add_person.setVisibility(View.GONE);
                    sp_name_list.setSelection(1);// Q join @ Name of primary
                    sp_name_list.setVisibility(View.GONE);
                    iv_left_bg.setVisibility(View.GONE);
                    iv_right_bg.setVisibility(View.GONE);
            }
            joinQueue(true);
        }
        dismissProgress();
    }

    @Override
    public void queueResponse(BizStoreElasticList bizStoreElasticList) {
        dismissProgress();
    }

    private void joinQueue(boolean validateView) {
        showHideView(true);
        setColor(true);
        sp_name_list.setBackground(ContextCompat.getDrawable(this, R.drawable.sp_background));
        if (isJoinNotPossible) {
            new CustomToast().showToast(this, joinErrorMsg);
            showHideView(false);
            setColor(false);
            if (joinErrorMsg.startsWith(getString(R.string.please_login_to_join))) {
                // login required
                if (validateView) {
                    btn_joinQueue.setText(getString(R.string.login_to_join));
                } else {
                    Intent loginIntent = new Intent(KioskJoinActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            } else {
                btn_joinQueue.setText(getString(R.string.join));
            }
        } else {
            if (jsonQueue.isAllowLoggedInUser()) {//Only login user to be allowed for join
                if (UserUtils.isLogin()) {
                    btn_joinQueue.setText(getString(R.string.join));
                    if (validateView) {
                        //setColor(false);  skip due to view validation
                    } else {
                        if (sp_name_list.getSelectedItemPosition() == 0) {
                            new CustomToast().showToast(this, getString(R.string.error_patient_name_missing));
                            sp_name_list.setBackground(ContextCompat.getDrawable(this, R.drawable.sp_background_red));
                        } else {
                            callJoinAPI();
                        }
                    }
                } else {
                    btn_joinQueue.setText(getString(R.string.login_to_join));
                    // please login to avail this feature
                    if (validateView) {
                        setColor(false);
                    } else {
                        // Navigate to login screen
                        Intent loginIntent = new Intent(KioskJoinActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    }
                    new CustomToast().showToast(KioskJoinActivity.this, "Please login to avail this feature");
                }
            } else {
                // any user can join
                btn_joinQueue.setText(getString(R.string.join));
                callJoinAPI();
            }

        }
    }

    private void callJoinAPI() {
        if (!NoQueueBaseActivity.isEmailVerified()) {
            new CustomToast().showToast(this, "To pay, email is mandatory. In your profile add and verify email");
        } else {
            KioskApiCalls kioskApiCalls = new KioskApiCalls();
            kioskApiCalls.setTokenPresenter(this);
            JsonProfile jp = NoQueueBaseActivity.getUserProfile();
            String queueUserId = ((JsonProfile) sp_name_list.getSelectedItem()).getQueueUserId();
            tv_name.setText(((JsonProfile) sp_name_list.getSelectedItem()).getName());
            String qUserId;
            String guardianId = null;
            List<JsonProfile> profileList = new ArrayList<>();
            if (UserUtils.isLogin()) {
                profileList = NoQueueBaseActivity.getAllProfileList();
            }
            JsonProfile jsonProfile = AppUtils.getJsonProfileQueueUserID(queueUserId, profileList);
            if (jp.getQueueUserId().equalsIgnoreCase(queueUserId)) {
                qUserId = jp.getQueueUserId();
            } else {
                qUserId = jsonProfile.getQueueUserId();
                guardianId = jp.getQueueUserId();
            }
            JoinQueue joinQueue = new JoinQueue().setCodeQR(codeQR).setQueueUserId(qUserId).setGuardianQid(guardianId);
            kioskApiCalls.joinQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), joinQueue);

            if (AppUtils.isRelease()) {
                Bundle params = new Bundle();
                params.putString("Queue_Name", jsonQueue.getDisplayName());
                LaunchActivity.getLaunchActivity().getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_JOIN_KIOSK_SCREEN, params);
            }
        }
    }

    /*
     *If user navigate to AfterJoinActivity screen from here &
     * he press back from that screen Join screen should removed from activity stack
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.requestCodeAfterJoinQActivity) {
            if (resultCode == RESULT_OK) {
                boolean isClose = data.getExtras().getBoolean(Constants.ACTIVITY_TO_CLOSE, false);
                if (isClose) {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetDisconnectTimer();
        // Added to re-initialised the value if user is logged in again and comeback to join screen
        if (null != jsonQueue) {
            /* Check weather join is possible or not today due to some reason */
            JoinQueueState joinQueueState = JoinQueueUtil.canJoinQueue(jsonQueue, KioskJoinActivity.this);
            if (joinQueueState.isJoinNotPossible()) {
                isJoinNotPossible = joinQueueState.isJoinNotPossible();
                joinErrorMsg = joinQueueState.getJoinErrorMsg();
            } else {
                isJoinNotPossible = false;
                joinErrorMsg = "";
            }
            joinQueue(true);
        }

        if (UserUtils.isLogin()) {
            List<JsonProfile> profileList = NoQueueBaseActivity.getUserProfile().getDependents();
            profileList.add(0, NoQueueBaseActivity.getUserProfile());
            profileList.add(0, new JsonProfile().setName(getString(R.string.select_patient)));
            DependentAdapter adapter = new DependentAdapter(this, profileList);
            sp_name_list.setAdapter(adapter);
            if (profileList.size() == 2) {
                sp_name_list.setSelection(1);
            }
        }
    }

    private void setColor(boolean isEnable) {
        btn_joinQueue.setBackground(ContextCompat.getDrawable(this, isEnable ? R.drawable.btn_bg_enable : R.drawable.btn_bg_inactive));
        btn_joinQueue.setTextColor(ContextCompat.getColor(this, isEnable ? R.color.white : R.color.btn_color));
    }

    private void showHideView(boolean isEnable) {
        add_person.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        sp_name_list.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        tv_add.setVisibility(isEnable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void tokenPresenterResponse(JsonToken token) {
        Log.d(TAG, token.toString());
        tv_left.setText(getString(R.string.serving_now));
        tv_right.setText(getString(R.string.your_token));
        tv_serving_no.setText(String.valueOf(token.getServingNumber()));
        tv_people_in_q.setText(token.getDisplayToken());
        tv_name.setVisibility(View.VISIBLE);
        sp_name_list.setVisibility(View.GONE);
        tv_add.setVisibility(View.GONE);
        btn_joinQueue.setVisibility(View.GONE);
        new CustomToast().showToast(this, "Token generated successfully. Your token no is: " + token.getDisplayToken());
        logoutFromKiosk();
        dismissProgress();
    }

    @Override
    public void paidTokenPresenterResponse(JsonToken token) {
        // do nothing
    }

    @Override
    public void unPaidTokenPresenterResponse(JsonToken token) {
        // do nothing
    }

    private void logoutFromKiosk() {
        tv_timer.setVisibility(View.VISIBLE);
        tv_timer.setText(String.format(getString(R.string.logout_warning), time));
        waitTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                tv_timer.setText(String.format(getString(R.string.logout_warning), time));
                time--;
            }

            public void onFinish() {
                tv_timer.setText(getString(R.string.try_again));
            }
        }.start();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                iv_home.performClick();
                if (waitTimer != null) {
                    waitTimer.cancel();
                    waitTimer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Constants.SCREEN_TIME_OUT);
    }

    private Handler disconnectHandler = new Handler(msg -> true);

    private Runnable disconnectCallback = () -> {
        // Perform any required operation on disconnect
        iv_home.performClick();
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, Constants.DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
}
