package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiAuthenticCall;
import com.noqapp.android.client.model.QueueApiUnAuthenticCall;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.wrapper.JoinQueueState;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.FabricEvents;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.JoinQueueUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.squareup.picasso.Picasso;

public class BeforeJoinOrderQueueActivity extends BaseActivity implements QueuePresenter {
    private final String TAG = BeforeJoinOrderQueueActivity.class.getSimpleName();
    private TextView tv_store_name;
    private TextView tv_queue_name;
    private TextView tv_address, tv_store_address;
    private TextView tv_mobile;
    private TextView tv_serving_no;
    private TextView tv_people_in_q;
    private TextView tv_hour_saved;
    private TextView tv_rating_review;
    private TextView tv_rating;
    private TextView tv_delay_in_time;
    private String codeQR;
    private JsonQueue jsonQueue;
    private boolean isJoinNotPossible = false;
    private String joinErrorMsg = "";
    private Button btn_pay_and_joinQueue, btn_joinQueue;
    private BizStoreElastic bizStoreElastic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_join_order_q);
        tv_delay_in_time = findViewById(R.id.tv_delay_in_time);
        tv_store_name = findViewById(R.id.tv_store_name);
        tv_queue_name = findViewById(R.id.tv_queue_name);
        tv_address = findViewById(R.id.tv_address);
        tv_store_address = findViewById(R.id.tv_store_address);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_serving_no = findViewById(R.id.tv_serving_no);
        tv_people_in_q = findViewById(R.id.tv_people_in_q);
        tv_hour_saved = findViewById(R.id.tv_hour_saved);
        ImageView iv_category_banner = findViewById(R.id.iv_category_banner);
        TextView tv_skip_msg = findViewById(R.id.tv_skip_msg);
        tv_rating_review = findViewById(R.id.tv_rating_review);
        btn_pay_and_joinQueue = findViewById(R.id.btn_pay_and_joinQueue);
        btn_joinQueue = findViewById(R.id.btn_joinQueue);
        btn_joinQueue.setOnClickListener((View v) -> {
            if (null != jsonQueue)
                joinQueue(false);
        });
        btn_pay_and_joinQueue.setOnClickListener((View v) -> {
            if (null != jsonQueue)
                joinQueue(false);
        });
        tv_rating = findViewById(R.id.tv_rating);
        Button btn_no = findViewById(R.id.btn_no);
        btn_no.setOnClickListener((View v) -> {
            finish();
        });

        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.screen_join));
        tv_mobile.setOnClickListener((View v) -> {
            AppUtils.makeCall(BeforeJoinOrderQueueActivity.this, tv_mobile.getText().toString());
        });

        tv_address.setOnClickListener((View v) -> {
            AppUtils.openAddressInMap(BeforeJoinOrderQueueActivity.this, tv_address.getText().toString());
        });

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            codeQR = bundle.getString(IBConstant.KEY_CODE_QR);
            bizStoreElastic = (BizStoreElastic) bundle.getSerializable("BizStoreElastic");
            boolean isCategoryData = bundle.getBoolean(IBConstant.KEY_IS_CATEGORY, false);
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
            if (bundle.getBoolean(IBConstant.KEY_IS_REJOIN, false)) {
                btn_joinQueue.setText(getString(R.string.yes));
                tv_skip_msg.setVisibility(View.VISIBLE);
                btn_no.setVisibility(View.VISIBLE);
            }

            if (isCategoryData) {
                queueResponse(jsonQueue);
            } else {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    setProgressMessage("Loading queue details...");
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
            tv_store_name.setText(jsonQueue.getBusinessName());
            tv_queue_name.setText(jsonQueue.getDisplayName());
            tv_address.setText(jsonQueue.getStoreAddress());
            tv_store_address.setText(AppUtils.getStoreAddress(jsonQueue.getTown(), jsonQueue.getArea()));
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getStorePhone()));
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
                btn_joinQueue.setVisibility(View.VISIBLE);
                btn_pay_and_joinQueue.setVisibility(View.GONE);
            }
        }
        dismissProgress();
    }

    @Override
    public void queueResponse(BizStoreElasticList bizStoreElasticList) {
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
                        new CustomToast().showToast(BeforeJoinOrderQueueActivity.this, "please login to avail this feature");
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
        if (jsonQueue.isEnabledPayment() && !NoQueueBaseActivity.isEmailVerified()) {
            new CustomToast().showToast(this, "To pay, email is mandatory. In your profile add and verify email");
        } else {
            Intent in = new Intent(this, JoinActivity.class);
            in.putExtra(IBConstant.KEY_CODE_QR, jsonQueue.getCodeQR());
            in.putExtra(IBConstant.KEY_FROM_LIST, false);
            in.putExtra(IBConstant.KEY_IS_PAYMENT_ENABLE, jsonQueue.isEnabledPayment());
            in.putExtra(IBConstant.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
            in.putExtra(Constants.ACTIVITY_TO_CLOSE, true);
            in.putExtra("qUserId", null == NoQueueBaseActivity.getUserProfile().getQueueUserId()
                    ? "" : NoQueueBaseActivity.getUserProfile().getQueueUserId());
            in.putExtra("imageUrl", getIntent().getStringExtra(IBConstant.KEY_IMAGE_URL));
            startActivityForResult(in, Constants.requestCodeAfterJoinQActivity);

            if (AppUtils.isRelease()) {
                Bundle params = new Bundle();
                params.putString("Queue_Name", jsonQueue.getDisplayName());
                LaunchActivity.getLaunchActivity().getFireBaseAnalytics().logEvent(FabricEvents.EVENT_JOIN_SCREEN, params);
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
        btn_joinQueue.setBackground(ContextCompat.getDrawable(this, isEnable ? R.drawable.btn_bg_enable : R.drawable.btn_bg_inactive));
        btn_pay_and_joinQueue.setBackground(ContextCompat.getDrawable(this, isEnable ? R.drawable.btn_bg_enable : R.drawable.btn_bg_inactive));
        btn_joinQueue.setTextColor(ContextCompat.getColor(this, isEnable ? R.color.white : R.color.btn_color));
        btn_pay_and_joinQueue.setTextColor(ContextCompat.getColor(this, isEnable ? R.color.white : R.color.btn_color));
    }
}
