package com.noqapp.android.client.views.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.api.TokenQueueApiImpl;
import com.noqapp.android.client.model.open.TokenQueueImpl;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.presenter.beans.wrapper.JoinQueueState;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.JoinQueueUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.squareup.picasso.Picasso;

public class BeforeJoinOrderQueueActivity extends BaseActivity implements QueuePresenter, SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = BeforeJoinOrderQueueActivity.class.getSimpleName();
    private static final int MAX_AVAILABLE_TOKEN_DISPLAY = 99;
    private static final String TITLE_TOOLBAR_POSTFIX = " Queue";

    private TextView tv_queue_name;
    private TextView tv_store_timing, tv_lunch_time;
    private FrameLayout fl_token_available;
    private TextView tv_token_available, tv_token_available_text;
    private TextView tv_people_in_q, tv_people_in_q_text;
    private TextView tv_rating_review;
    private TextView tv_rating;
    private TextView tv_delay_in_time;
    private TextView tv_daily_token_limit;
    private TextView tv_daily_token_limit_msg;
    private TextView tv_revisit_restriction;
    private TextView tv_identification_code;
    private TextView tv_mobile;
    private TextView tv_address;
    private TextView tv_estimated_time;
    private TextView tv_currently_serving;
    private TextView tv_live_status;
    private LinearLayout ll_announcement;
    private TextView tv_announcement_label;
    private TextView tv_announcement_text;
    private String codeQR;
    private JsonQueue jsonQueue;
    private boolean isJoinNotPossible = false;
    private String joinErrorMsg = "";
    private Button btn_pay_and_joinQueue, btn_joinQueue;
    private BizStoreElastic bizStoreElastic;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isCategoryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_join_order_q);
        swipeRefreshLayout = findViewById(R.id.refresh);
        tv_delay_in_time = findViewById(R.id.tv_delay_in_time);
        tv_queue_name = findViewById(R.id.tv_queue_name);
        tv_address = findViewById(R.id.tv_address);
        tv_estimated_time = findViewById(R.id.tv_estimated_time);
        tv_currently_serving = findViewById(R.id.tv_currently_serving);
        tv_live_status = findViewById(R.id.tv_live_status);
        ll_announcement = findViewById(R.id.ll_announcement);
        tv_announcement_label = findViewById(R.id.tv_announcement_label);
        tv_announcement_text = findViewById(R.id.tv_announcement_text);
        tv_mobile = findViewById(R.id.tv_mobile);
        fl_token_available = findViewById(R.id.fl_token_available);
        tv_token_available = findViewById(R.id.tv_token_available);
        tv_token_available_text = findViewById(R.id.tv_token_available_text);
        tv_people_in_q = findViewById(R.id.tv_people_in_q);
        tv_people_in_q_text = findViewById(R.id.tv_people_in_q_text);
        tv_store_timing = findViewById(R.id.tv_store_timing);
        tv_lunch_time = findViewById(R.id.tv_lunch_time);
        tv_daily_token_limit = findViewById(R.id.tv_daily_token_limit);
        tv_daily_token_limit_msg = findViewById(R.id.tv_daily_token_limit_msg);
        tv_revisit_restriction = findViewById(R.id.tv_revisit_restriction);
        tv_identification_code = findViewById(R.id.tv_identification_code);
        ImageView iv_category_banner = findViewById(R.id.iv_category_banner);
        tv_rating_review = findViewById(R.id.tv_rating_review);
        btn_pay_and_joinQueue = findViewById(R.id.btn_pay_and_joinQueue);
        btn_joinQueue = findViewById(R.id.btn_joinQueue);
        btn_joinQueue.setOnClickListener((View v) -> {
            if (null != jsonQueue) {
                joinQueue(false);
            }
        });
        btn_pay_and_joinQueue.setOnClickListener((View v) -> {
            if (null != jsonQueue) {
                joinQueue(false);
            }
        });
        tv_rating = findViewById(R.id.tv_rating);

        initActionsViews(true);
        tv_mobile.setOnClickListener((View v) -> AppUtils.makeCall(BeforeJoinOrderQueueActivity.this, tv_mobile.getText().toString()));
        tv_address.setOnClickListener((View v) -> AppUtils.openAddressInMap(BeforeJoinOrderQueueActivity.this, tv_address.getText().toString()));
        swipeRefreshLayout.setOnRefreshListener(this);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            codeQR = bundle.getString(IBConstant.KEY_CODE_QR);
            enableFavourite(codeQR);
            bizStoreElastic = (BizStoreElastic) bundle.getSerializable("BizStoreElastic");
            isCategoryData = bundle.getBoolean(IBConstant.KEY_IS_CATEGORY, false);
            String imageUrl = bizStoreElastic.getDisplayImage();
            JsonQueue jsonQueue = (JsonQueue) bundle.getSerializable(IBConstant.KEY_DATA_OBJECT);
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get()
                    .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, imageUrl))
                    .placeholder(ImageUtils.getThumbPlaceholder(this))
                    .error(ImageUtils.getThumbErrorPlaceholder(this))
                    .into(iv_category_banner);
            } else {
                Picasso.get().load(ImageUtils.getThumbPlaceholder()).into(iv_category_banner);
            }

            if (isCategoryData) {
                queueResponse(jsonQueue);
            } else {
                if (isOnline()) {
                    setProgressMessage("Loading queue details...");
                    showProgress();
                    if (UserUtils.isLogin()) {
                        TokenQueueApiImpl tokenQueueApiImpl = new TokenQueueApiImpl();
                        tokenQueueApiImpl.setQueuePresenter(this);
                        tokenQueueApiImpl.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
                    } else {
                        TokenQueueImpl tokenQueueImpl = new TokenQueueImpl();
                        tokenQueueImpl.setQueuePresenter(this);
                        tokenQueueImpl.getQueueState(UserUtils.getDeviceId(), codeQR);
                    }
                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                }
            }
        }
    }

    @Override
    public void queueError() {
        Log.d(TAG, "Queue=Error");
        dismissProgress();
        swipeRefreshLayout.setRefreshing(false);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void queueResponse(JsonQueue jsonQueueTemp) {
        swipeRefreshLayout.setRefreshing(false);
        if (null != jsonQueueTemp) {
            Log.d(TAG, "Queue=" + jsonQueueTemp.toString());
            this.jsonQueue = jsonQueueTemp;
            tv_toolbar_title.setText(jsonQueue.getBusinessName() + TITLE_TOOLBAR_POSTFIX);
            tv_queue_name.setText(jsonQueue.getDisplayName());
            tv_address.setText(jsonQueue.getStoreAddress());
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getStorePhone()));
            if (jsonQueue.getAvailableTokenCount() != 0) {
                fl_token_available.setVisibility(View.VISIBLE);
                int tokenAlreadyIssued = jsonQueue.getServingNumber() + jsonQueue.getPeopleInQueue();
                int tokenAvailableForDay = Math.max(jsonQueue.getAvailableTokenCount() - tokenAlreadyIssued, 0);
                tv_token_available.setText(String.valueOf(tokenAvailableForDay));
                tv_token_available_text.setText(getResources().getQuantityString(R.plurals.token_available, tokenAvailableForDay));
            }
            tv_people_in_q.setText(String.valueOf(jsonQueue.getPeopleInQueue()));
            tv_people_in_q_text.setText(getResources().getQuantityString(R.plurals.people_in_queue, jsonQueue.getPeopleInQueue()));

            if (jsonQueue.getAvailableTokenCount() != 0) {
                tv_daily_token_limit.setText(String.format(getResources().getString(R.string.daily_token_limit), String.valueOf(jsonQueue.getAvailableTokenCount())));
                tv_daily_token_limit.setVisibility(View.VISIBLE);
            }
            if (jsonQueue.getLimitServiceByDays() != 0) {
                tv_revisit_restriction.setText(String.format(getResources().getString(R.string.revisit_restriction), jsonQueue.getLimitServiceByDays() + " days"));
                tv_revisit_restriction.setVisibility(View.VISIBLE);
            }
            if (jsonQueue.getPriorityAccess().getDescription().equalsIgnoreCase("ON")) {
                tv_identification_code.setVisibility(View.VISIBLE);
            }

            //TODO: This information need to come from bizStoreElastic along with formatted announcement text
            switch (bizStoreElastic.getBusinessType()) {
                case CD:
                case CDQ:
                    tv_announcement_text.setText(R.string.announcement_message_csd);
                    tv_estimated_time.setText(getResources().getString(R.string.expected_service_around, jsonQueue.getTimeSlotMessage()));
                    break;
                default:
                    tv_announcement_text.setText(R.string.announcement_message_covid);
                    tv_announcement_text.setVisibility(View.GONE);
                    tv_announcement_label.setVisibility(View.GONE);
                    ll_announcement.setVisibility(View.GONE);
                    tv_estimated_time.setText(getResources().getString(R.string.expected_service_around, jsonQueue.getTimeSlotMessage()));
            }
            if (0 == jsonQueue.getServingNumber()) {
                tv_currently_serving.setText(getResources().getString(R.string.serving_not_started, "Not Started"));
            } else {
                tv_currently_serving.setText(getResources().getString(R.string.serving_now_in_queue, jsonQueue.getDisplayServingNumber()));
            }
            tv_live_status.setText(Html.fromHtml("&#8857 live"));
            tv_live_status.startAnimation(addAnimation());

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
            if (null != bizStoreElastic && bizStoreElastic.getStoreHourElasticList().size() > 0) {
                StoreHourElastic storeHourElastic = AppUtils.getStoreHourElastic(bizStoreElastic.getStoreHourElasticList());
                String lunchTime = new AppUtils().formatTodayStoreLunchTiming(this, storeHourElastic.getLunchTimeStart(), storeHourElastic.getLunchTimeEnd());
                if (!TextUtils.isEmpty(lunchTime)) {
                    tv_lunch_time.setText(lunchTime);
                    tv_lunch_time.setVisibility(View.VISIBLE);
                }
            } else {
                String lunchTime = new AppUtils().formatTodayStoreLunchTiming(this, jsonQueue.getLunchTimeStart(), jsonQueue.getLunchTimeEnd());
                if (!TextUtils.isEmpty(lunchTime)) {
                    tv_lunch_time.setText(lunchTime);
                    tv_lunch_time.setVisibility(View.VISIBLE);
                }
            }
            tv_store_timing.setText(time);
            tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tv_rating_review.setOnClickListener((View v) -> {
                if (null != jsonQueue && jsonQueue.getReviewCount() > 0) {
                    Intent in = new Intent(BeforeJoinOrderQueueActivity.this, AllReviewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(IBConstant.KEY_CODE_QR, jsonQueue.getCodeQR());
                    bundle.putString(IBConstant.KEY_STORE_NAME, jsonQueue.getDisplayName());
                    bundle.putString(IBConstant.KEY_STORE_ADDRESS, AppUtils.getStoreAddress(jsonQueue.getTown(), jsonQueue.getArea()));
                    in.putExtras(bundle);
                    startActivity(in);
                }
            });
            tv_rating.setText(String.valueOf(AppUtils.round(jsonQueue.getRating())));
            if (tv_rating.getText().toString().equals("0.0")) {
                tv_rating.setVisibility(View.INVISIBLE);
            } else {
                tv_rating.setVisibility(View.VISIBLE);
            }
            AppUtils.setReviewCountText(jsonQueue.getReviewCount(), tv_rating_review);
            codeQR = jsonQueue.getCodeQR();
            /* Check weather join is possible or not today due to some reason */
            JoinQueueState joinQueueState = JoinQueueUtil.canJoinQueue(jsonQueue, BeforeJoinOrderQueueActivity.this);
            if (joinQueueState.isJoinNotPossible()) {
                isJoinNotPossible = joinQueueState.isJoinNotPossible();
                joinErrorMsg = joinQueueState.getJoinErrorMsg();
            }
            joinQueue(true);
            if (jsonQueue.isEnabledPayment()) {
                btn_joinQueue.setVisibility(View.GONE);
                btn_pay_and_joinQueue.setVisibility(View.VISIBLE);
            } else {
                // Check if user is already in queue for this store
                SharedPreferences prefs = this.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE);
                if (prefs.contains(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, jsonQueue.getCodeQR()))) {
                    btn_joinQueue.setText(getResources().getString(R.string.view_token_status));
                }
                btn_joinQueue.setVisibility(View.VISIBLE);
                btn_pay_and_joinQueue.setVisibility(View.GONE);
            }
        }
        dismissProgress();
    }

    private Animation addAnimation() {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        return anim;
    }

    @Override
    public void queueResponse(BizStoreElasticList bizStoreElasticList) {
        swipeRefreshLayout.setRefreshing(false);
        dismissProgress();
    }

    private void joinQueue(boolean validateView) {
        setColor(true);
        if (isJoinNotPossible) {
            new CustomToast().showToast(this, joinErrorMsg);
            setColor(false);
            if (joinErrorMsg.startsWith("Please login to join")) {
                // login required
                if (validateView) {
                    btn_joinQueue.setText(getString(R.string.login_to_join));
                    btn_pay_and_joinQueue.setText(getString(R.string.login_to_join));
                } else {
                    Intent loginIntent = new Intent(BeforeJoinOrderQueueActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            } else {
                btn_joinQueue.setText(getString(R.string.join));
                btn_pay_and_joinQueue.setText(getString(R.string.pay_and_join));
            }
        } else {
            if (jsonQueue.isRemoteJoinAvailable()) {
                if (jsonQueue.isAllowLoggedInUser()) {//Only login user to be allowed for join
                    if (UserUtils.isLogin()) {
                        btn_joinQueue.setText(getString(R.string.join));
                        btn_pay_and_joinQueue.setText(getString(R.string.pay_and_join));
                        if (validateView) {
                            //setColor(false);  skip due to view validation
                        } else {
                            callJoinScreen();
                        }
                    } else {
                        btn_joinQueue.setText(getString(R.string.login_to_join));
                        btn_pay_and_joinQueue.setText(getString(R.string.login_to_join));
                        // please login to avail this feature
                        if (validateView) {
                            setColor(false);
                        } else {
                            // Navigate to login screen
                            Intent loginIntent = new Intent(BeforeJoinOrderQueueActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                        }
                        new CustomToast().showToast(BeforeJoinOrderQueueActivity.this, "Please login to avail this feature");
                    }
                } else {
                    // any user can join
                    btn_joinQueue.setText(getString(R.string.join));
                    btn_pay_and_joinQueue.setText(getString(R.string.pay_and_join));
                    callJoinScreen();
                }
            } else {
                btn_joinQueue.setText(getString(R.string.join));
                btn_pay_and_joinQueue.setText(getString(R.string.pay_and_join));
                if (validateView) {
                    setColor(false);
                }
                ShowAlertInformation.showThemeDialog(this, getString(R.string.error_join), getString(R.string.error_remote_join_not_available), true);
            }
        }
    }

    private void callJoinScreen() {
        if (jsonQueue.isEnabledPayment() && !NoQueueClientApplication.isEmailVerified()) {
            new CustomToast().showToast(this, "To pay, email is mandatory. In your profile add and verify email");
        } else if (!AppUtils.isValidStoreDistanceForUser(jsonQueue)) {
            ShowCustomDialog showCustomDialog = new ShowCustomDialog(this, true);
            showCustomDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    Intent in = new Intent(BeforeJoinOrderQueueActivity.this, JoinActivity.class);
                    in.putExtra(IBConstant.KEY_CODE_QR, jsonQueue.getCodeQR());
                    in.putExtra(IBConstant.KEY_FROM_LIST, false);
                    in.putExtra(IBConstant.KEY_IS_PAYMENT_ENABLE, jsonQueue.isEnabledPayment());
                    in.putExtra(IBConstant.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
                    in.putExtra(Constants.ACTIVITY_TO_CLOSE, true);
                    in.putExtra("qUserId",
                        null == NoQueueClientApplication.getUserProfile().getQueueUserId()
                            ? ""
                            : NoQueueClientApplication.getUserProfile().getQueueUserId());
                    in.putExtra("imageUrl", getIntent().getStringExtra(IBConstant.KEY_IMAGE_URL));
                    startActivityForResult(in, Constants.requestCodeAfterJoinQActivity);

                    if (AppUtils.isRelease()) {
                        Bundle params = new Bundle();
                        params.putString("Queue_Name", jsonQueue.getDisplayName());
                        NoQueueClientApplication.getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_JOIN_SCREEN, params);
                    }
                }

                @Override
                public void btnNegativeClick() {

                }
            });
            showCustomDialog.displayDialog(getString(R.string.alert), getString(R.string.business_too_far_from_location));
        } else {
            Intent in = new Intent(this, JoinActivity.class);
            in.putExtra(IBConstant.KEY_CODE_QR, jsonQueue.getCodeQR());
            in.putExtra(IBConstant.KEY_FROM_LIST, false);
            in.putExtra(IBConstant.KEY_IS_PAYMENT_ENABLE, jsonQueue.isEnabledPayment());
            in.putExtra(IBConstant.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
            in.putExtra(Constants.ACTIVITY_TO_CLOSE, true);
            in.putExtra("qUserId",
                null == NoQueueClientApplication.getUserProfile().getQueueUserId()
                    ? ""
                    : NoQueueClientApplication.getUserProfile().getQueueUserId());
            in.putExtra("imageUrl", getIntent().getStringExtra(IBConstant.KEY_IMAGE_URL));
            startActivityForResult(in, Constants.requestCodeAfterJoinQActivity);

            if (AppUtils.isRelease()) {
                Bundle params = new Bundle();
                params.putString("Queue_Name", jsonQueue.getDisplayName());
                NoQueueClientApplication.getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_JOIN_SCREEN, params);
            }
        }
    }

    /*
     * If user navigate to AfterJoinActivity screen from here &
     * he press back from that screen Join screen should removed from activity stack
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.requestCodeAfterJoinQActivity) {
            if (resultCode == RESULT_OK) {
                boolean toclose = data.getExtras().getBoolean(Constants.ACTIVITY_TO_CLOSE, false);
                if (toclose) {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Added to re-initialised the value if user is logged in again and comeback to join screen
        if (null != jsonQueue) {
            /* Check weather join is possible or not today due to some reason */
            JoinQueueState joinQueueState = JoinQueueUtil.canJoinQueue(jsonQueue, BeforeJoinOrderQueueActivity.this);
            if (joinQueueState.isJoinNotPossible()) {
                isJoinNotPossible = joinQueueState.isJoinNotPossible();
                joinErrorMsg = joinQueueState.getJoinErrorMsg();
            } else {
                isJoinNotPossible = false;
                joinErrorMsg = "";
            }
            joinQueue(true);
        }
    }

    private void setColor(boolean isEnable) {
        btn_joinQueue.setBackground(ContextCompat.getDrawable(this, isEnable
            ? R.drawable.orange_gradient
            : R.drawable.btn_bg_inactive));
        btn_pay_and_joinQueue.setBackground(ContextCompat.getDrawable(this, isEnable
            ? R.drawable.orange_gradient
            : R.drawable.btn_bg_inactive));
        btn_joinQueue.setTextColor(ContextCompat.getColor(this, isEnable
            ? R.color.white
            : R.color.btn_color));
        btn_pay_and_joinQueue.setTextColor(ContextCompat.getColor(this, isEnable
            ? R.color.white
            : R.color.btn_color));
    }

    @Override
    public void onRefresh() {
        if (isCategoryData) {
            swipeRefreshLayout.setRefreshing(false);
        } else {
            if (isOnline()) {
                setProgressMessage("Loading queue details...");
                swipeRefreshLayout.setRefreshing(true);
                showProgress();
                if (UserUtils.isLogin()) {
                    TokenQueueApiImpl tokenQueueApiImpl = new TokenQueueApiImpl();
                    tokenQueueApiImpl.setQueuePresenter(this);
                    tokenQueueApiImpl.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);

                } else {
                    TokenQueueImpl tokenQueueImpl = new TokenQueueImpl();
                    tokenQueueImpl.setQueuePresenter(this);
                    tokenQueueImpl.getQueueState(UserUtils.getDeviceId(), codeQR);
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
                ShowAlertInformation.showNetworkDialog(this);
            }
        }
    }
}
