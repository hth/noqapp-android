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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.api.TokenQueueApiImpl;
import com.noqapp.android.client.model.open.TokenQueueImpl;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.wrapper.JoinQueueState;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.JoinQueueUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.DependentAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BeforeJoinActivity extends BaseActivity implements QueuePresenter, SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = BeforeJoinActivity.class.getSimpleName();
    private static final int MAX_AVAILABLE_TOKEN_DISPLAY = 99;
    private static final String TITLE_TOOLBAR_POSTFIX = " Queue";

    private TextView tv_queue_name;
    private TextView tv_store_timing;
    private FrameLayout fl_token_available;
    private TextView tv_token_available, tv_token_available_text;
    private TextView tv_people_in_q, tv_people_in_q_text;
    private TextView tv_rating_review;
    private TextView tv_rating;
    private TextView tv_delay_in_time;
    private TextView tv_consult_fees, tv_cancellation_fees;
    private TextView tv_add, add_person;
    private TextView tv_estimated_time;
    private TextView tv_currently_serving;
    private TextView tv_live_status;
    private TextView tv_daily_token_limit;
    private TextView tv_daily_token_limit_msg;
    private TextView tv_revisit_restriction;
    private TextView tv_identification_code;
    private TextView tv_mobile;
    private TextView tv_address;
    private RelativeLayout tv_consult;
    private LinearLayout ll_select_family_member;
    private Spinner sp_name_list;
    private String codeQR;
    private JsonQueue jsonQueue;
    private boolean isJoinNotPossible = false;
    private String joinErrorMsg = "";
    private Button btn_pay_and_joinQueue, btn_joinQueue;
    private ImageView iv_token_bg, iv_token_available_bg;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isCategoryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_join);
        swipeRefreshLayout = findViewById(R.id.refresh);
        tv_delay_in_time = findViewById(R.id.tv_delay_in_time);
        tv_queue_name = findViewById(R.id.tv_queue_name);
        tv_address = findViewById(R.id.tv_address);
        tv_mobile = findViewById(R.id.tv_mobile);
        fl_token_available = findViewById(R.id.fl_token_available);
        iv_token_bg = findViewById(R.id.iv_token_bg);
        iv_token_available_bg = findViewById(R.id.iv_token_available_bg);
        tv_consult = findViewById(R.id.tv_consult);
        tv_consult_fees = findViewById(R.id.tv_consult_fees);
        tv_cancellation_fees = findViewById(R.id.tv_cancellation_fees);
        tv_token_available = findViewById(R.id.tv_token_available);
        tv_token_available_text = findViewById(R.id.tv_token_available_text);
        tv_people_in_q = findViewById(R.id.tv_people_in_q);
        tv_people_in_q_text = findViewById(R.id.tv_people_in_q_text);
        tv_store_timing = findViewById(R.id.tv_store_timing);
        tv_estimated_time = findViewById(R.id.tv_estimated_time);
        tv_currently_serving = findViewById(R.id.tv_currently_serving);
        tv_live_status = findViewById(R.id.tv_live_status);
        tv_daily_token_limit = findViewById(R.id.tv_daily_token_limit);
        tv_daily_token_limit_msg = findViewById(R.id.tv_daily_token_limit_msg);
        tv_revisit_restriction = findViewById(R.id.tv_revisit_restriction);
        tv_identification_code = findViewById(R.id.tv_identification_code);
        ImageView iv_profile = findViewById(R.id.iv_profile);
        TextView tv_skip_msg = findViewById(R.id.tv_skip_msg);
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
        ll_select_family_member = findViewById(R.id.ll_select_family_member);
        tv_add = findViewById(R.id.tv_add);
        add_person = findViewById(R.id.add_person);
        tv_add.setPaintFlags(tv_add.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Button btn_no = findViewById(R.id.btn_no);
        btn_no.setOnClickListener((View v) -> finish());
        sp_name_list = findViewById(R.id.sp_name_list);

        initActionsViews(true);
        tv_mobile.setOnClickListener((View v) -> AppUtils.makeCall(BeforeJoinActivity.this, tv_mobile.getText().toString()));
        tv_address.setOnClickListener((View v) -> AppUtils.openAddressInMap(BeforeJoinActivity.this, tv_address.getText().toString()));
        tv_add.setOnClickListener((View v) -> {
            if (UserUtils.isLogin()) {
                Intent loginIntent = new Intent(BeforeJoinActivity.this, UserProfileActivity.class);
                startActivity(loginIntent);
            } else {
                new CustomToast().showToast(BeforeJoinActivity.this, "Please login to add dependents");
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            codeQR = bundle.getString(IBConstant.KEY_CODE_QR);
            enableFavourite(codeQR);
            isCategoryData = bundle.getBoolean(IBConstant.KEY_IS_CATEGORY, false);
            String imageUrl = bundle.getString(IBConstant.KEY_IMAGE_URL);
            JsonQueue jsonQueue = (JsonQueue) bundle.getSerializable(IBConstant.KEY_DATA_OBJECT);
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(ContextCompat.getDrawable(this, R.drawable.profile_theme))
                    .error(ContextCompat.getDrawable(this, R.drawable.profile_theme)).into(iv_profile);
            } else {
                Picasso.get().load(R.drawable.profile_theme).into(iv_profile);
            }
            boolean isDoctor = bundle.getBoolean(IBConstant.KEY_IS_DO, false);
            if (isDoctor) {
                iv_profile.setVisibility(View.VISIBLE);
            } else {
                iv_profile.setVisibility(View.GONE);
            }
            if (bundle.getBoolean(IBConstant.KEY_IS_REJOIN, false)) {
                btn_joinQueue.setText(getString(R.string.yes));
                tv_skip_msg.setVisibility(View.VISIBLE);
                btn_no.setVisibility(View.VISIBLE);
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
            tv_toolbar_title.setText(jsonQueue.getDisplayName() + TITLE_TOOLBAR_POSTFIX);
            tv_queue_name.setText(jsonQueue.getDisplayName());
            tv_address.setText(jsonQueue.getStoreAddress());
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getStorePhone()));
            if (jsonQueue.getAvailableTokenCount() != 0) {
                fl_token_available.setVisibility(View.VISIBLE);
                int tokenAlreadyIssued = jsonQueue.getServingNumber() + jsonQueue.getPeopleInQueue();
                int tokenAvailableForDay = Math.max(jsonQueue.realAvailableToken() - tokenAlreadyIssued, 0);
                tv_token_available.setText(String.valueOf(tokenAvailableForDay));
                tv_token_available_text.setText(getResources().getQuantityString(R.plurals.token_available, tokenAvailableForDay));
            }

            tv_people_in_q.setText(String.valueOf(jsonQueue.getPeopleInQueue()));
            tv_people_in_q_text.setText(getResources().getQuantityString(R.plurals.people_in_queue, jsonQueue.getPeopleInQueue()));

            switch (jsonQueue.getBusinessType()) {
                case CD:
                case CDQ:
                    tv_estimated_time.setText(getResources().getString(R.string.expected_service_around, jsonQueue.getTimeSlotMessage()));
                    break;
                default:
                    tv_estimated_time.setText(getResources().getString(R.string.estimated_wait, jsonQueue.getTimeSlotMessage()));
            }
            if (0 == jsonQueue.getServingNumber()) {
                tv_currently_serving.setText(getResources().getString(R.string.serving_not_started, "Not Started"));
            } else {
                tv_currently_serving.setText(getResources().getString(R.string.serving_now_in_queue, jsonQueue.getDisplayServingNumber()));
            }
            tv_live_status.setText(Html.fromHtml("&#8857 live"));
            tv_live_status.startAnimation(addAnimation());

            if (0 != jsonQueue.getAvailableTokenCount()) {
                tv_daily_token_limit.setTextColor(ContextCompat.getColor(this, R.color.theme_color_red));
                tv_daily_token_limit.setText(String.format(getResources().getString(R.string.daily_token_limit), String.valueOf(jsonQueue.getAvailableTokenCount())));
                tv_daily_token_limit.setVisibility(View.VISIBLE);

                tv_daily_token_limit_msg.setTextColor(ContextCompat.getColor(this, R.color.theme_color_red));
                if (jsonQueue.getAvailableTokenAfterCancellation() > 1) {
                    tv_daily_token_limit_msg.setText("Cancelled tokens: " + jsonQueue.getAvailableTokenAfterCancellation());
                } else {
                    tv_daily_token_limit_msg.setText("Cancelled token: " + jsonQueue.getAvailableTokenAfterCancellation());
                }
                tv_daily_token_limit_msg.setPaintFlags(tv_daily_token_limit_msg.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                tv_daily_token_limit_msg.setOnClickListener((View v) -> {
                    new CustomToast().showToast(BeforeJoinActivity.this, "Tokens issued in lieu of cancellation");
                });
                tv_daily_token_limit_msg.setVisibility(View.VISIBLE);
            }

            if (0 != jsonQueue.getLimitServiceByDays()) {
                tv_revisit_restriction.setText(String.format(getResources().getString(R.string.revisit_restriction), jsonQueue.getLimitServiceByDays() + " days"));
                tv_revisit_restriction.setVisibility(View.VISIBLE);
            }
            if (jsonQueue.getPriorityAccess().getDescription().equalsIgnoreCase("ON")) {
                tv_identification_code.setVisibility(View.VISIBLE);
            }

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
            tv_store_timing.setText(time);
            AppUtils.showAllDaysTiming(this, tv_store_timing, jsonQueue.getCodeQR());
            tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tv_rating_review.setOnClickListener((View v) -> {
                if (null != jsonQueue && jsonQueue.getReviewCount() > 0) {
                    Intent in = new Intent(BeforeJoinActivity.this, AllReviewsActivity.class);
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
            JoinQueueState joinQueueState = JoinQueueUtil.canJoinQueue(jsonQueue, BeforeJoinActivity.this);
            if (joinQueueState.isJoinNotPossible()) {
                isJoinNotPossible = joinQueueState.isJoinNotPossible();
                joinErrorMsg = joinQueueState.getJoinErrorMsg();
            }
            switch (jsonQueue.getBusinessType()) {
                case DO:
                case PH:
                case HS:
                    String feeString = "<b>" + AppUtils.getCurrencySymbol(jsonQueue.getCountryShortName()) + String.valueOf(jsonQueue.getProductPrice() / 100) + "</b>  Consultation fee";
                    tv_consult_fees.setText(Html.fromHtml(feeString));
                    String cancelFeeString = "<b>" + AppUtils.getCurrencySymbol(jsonQueue.getCountryShortName()) + String.valueOf(jsonQueue.getCancellationPrice() / 100) + "</b>  Cancellation fee";
                    tv_cancellation_fees.setText(Html.fromHtml(cancelFeeString));
                    tv_consult.setVisibility(View.VISIBLE);
                    ll_select_family_member.setVisibility(View.VISIBLE);
                    iv_token_available_bg.setVisibility(View.VISIBLE);
                    iv_token_bg.setVisibility(View.VISIBLE);
                    break;
                default:
                    tv_consult.setVisibility(View.GONE);
                    ll_select_family_member.setVisibility(View.GONE);
                    iv_token_available_bg.setVisibility(View.GONE);
                    iv_token_bg.setVisibility(View.GONE);
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
        dismissProgress();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void joinQueue(boolean validateView) {
        showHideView(true);
        setColor(true);
        sp_name_list.setBackground(ContextCompat.getDrawable(this, R.drawable.sp_background));
        if (isJoinNotPossible) {
            new CustomToast().showToast(this, joinErrorMsg);
            showHideView(false);
            setColor(false);
            if (joinErrorMsg.startsWith("Please login to join")) {
                // login required
                if (validateView) {
                    btn_joinQueue.setText(getString(R.string.login_to_join));
                    btn_pay_and_joinQueue.setText(getString(R.string.login_to_join));
                } else {
                    Intent loginIntent = new Intent(BeforeJoinActivity.this, LoginActivity.class);
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
                        switch (jsonQueue.getBusinessType()) {
                            case HS:
                            case DO:
                                break;
                            default:
                                // Set the primary account holder selected if not related to Health-care service
                                // TODO(pth): Fix another way to set primary by default
                                sp_name_list.setSelection(1);
                        }
                        if (validateView) {
                            //setColor(false);  skip due to view validation
                        } else {
                            if (sp_name_list.getSelectedItemPosition() == 0) {
                                new CustomToast().showToast(this, getString(R.string.error_patient_name_missing));
                                sp_name_list.setBackground(ContextCompat.getDrawable(this, R.drawable.sp_background_red));
                            } else {
                                callAfterJoin();
                            }
                        }
                    } else {
                        btn_joinQueue.setText(getString(R.string.login_to_join));
                        btn_pay_and_joinQueue.setText(getString(R.string.login_to_join));
                        // please login to avail this feature
                        if (validateView) {
                            setColor(false);
                        } else {
                            // Navigate to login screen
                            Intent loginIntent = new Intent(BeforeJoinActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                        }
                        new CustomToast().showToast(BeforeJoinActivity.this, "Please login to avail this feature");
                    }
                } else {
                    // any user can join
                    btn_joinQueue.setText(getString(R.string.join));
                    btn_pay_and_joinQueue.setText(getString(R.string.pay_and_join));
                    switch (jsonQueue.getBusinessType()) {
                        case HS:
                        case DO:
                            break;
                        default:
                            // Set the primary account holder selected if not related to Health-care service
                            // TODO(pth): Fix another way to set primary by default
                            sp_name_list.setSelection(1);
                    }
                    callAfterJoin();
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

    private void callAfterJoin() {
        if (jsonQueue.isEnabledPayment() && !NoQueueClientApplication.isEmailVerified()) {
            new CustomToast().showToast(this, "To pay, email is mandatory. In your profile add and verify email");
        } else if (!AppUtils.isValidStoreDistanceForUser(jsonQueue)) {
            ShowCustomDialog showCustomDialog = new ShowCustomDialog(this, true);
            showCustomDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    Intent in = new Intent(BeforeJoinActivity.this, JoinActivity.class);
                    in.putExtra(IBConstant.KEY_CODE_QR, jsonQueue.getCodeQR());
                    in.putExtra(IBConstant.KEY_FROM_LIST, false);
                    in.putExtra(IBConstant.KEY_IS_PAYMENT_ENABLE, jsonQueue.isEnabledPayment());
                    in.putExtra(IBConstant.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
                    in.putExtra(Constants.ACTIVITY_TO_CLOSE, true);
                    in.putExtra("qUserId", ((JsonProfile) sp_name_list.getSelectedItem()).getQueueUserId());
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
            in.putExtra("qUserId", ((JsonProfile) sp_name_list.getSelectedItem()).getQueueUserId());
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
     *If user navigate to AfterJoinActivity screen from here &
     * he press back from that screen Join screen should removed from activity stack
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.requestCodeAfterJoinQActivity) {
            if (resultCode == RESULT_OK) {
                boolean toClose = data.getExtras().getBoolean(Constants.ACTIVITY_TO_CLOSE, false);
                if (toClose) {
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
            JoinQueueState joinQueueState = JoinQueueUtil.canJoinQueue(jsonQueue, BeforeJoinActivity.this);
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
            List<JsonProfile> profileList = NoQueueClientApplication.getUserProfile().getDependents();
            profileList.add(0, NoQueueClientApplication.getUserProfile());
            profileList.add(0, new JsonProfile().setName("Select Patient"));
            DependentAdapter adapter = new DependentAdapter(this, profileList);
            sp_name_list.setAdapter(adapter);
            if (profileList.size() == 2) {
                sp_name_list.setSelection(1);
            }
        }
    }

    private void setColor(boolean isEnable) {
        btn_joinQueue.setBackground(ContextCompat.getDrawable(this, isEnable ? R.drawable.orange_gradient : R.drawable.btn_bg_inactive));
        btn_pay_and_joinQueue.setBackground(ContextCompat.getDrawable(this, isEnable ? R.drawable.orange_gradient : R.drawable.btn_bg_inactive));
        btn_joinQueue.setTextColor(ContextCompat.getColor(this, isEnable ? R.color.white : R.color.btn_color));
        btn_pay_and_joinQueue.setTextColor(ContextCompat.getColor(this, isEnable ? R.color.white : R.color.btn_color));
    }

    private void showHideView(boolean isEnable) {
        add_person.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        sp_name_list.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        tv_add.setVisibility(isEnable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRefresh() {
        if (isCategoryData) {
            swipeRefreshLayout.setRefreshing(false);
        } else {
            if (isOnline()) {
                swipeRefreshLayout.setRefreshing(true);
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
                swipeRefreshLayout.setRefreshing(false);
                ShowAlertInformation.showNetworkDialog(this);
            }
        }
    }
}
