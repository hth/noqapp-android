package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiModel;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.wrapper.JoinQueueState;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.utils.JoinQueueUtil;
import com.noqapp.android.client.utils.PhoneFormatterUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JoinActivity extends AppCompatActivity implements QueuePresenter {


    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.tv_store_name)
    protected TextView tv_store_name;

    @BindView(R.id.tv_queue_name)
    protected TextView tv_queue_name;

    @BindView(R.id.tv_address)
    protected TextView tv_address;

    @BindView(R.id.tv_mobile)
    protected TextView tv_mobile;

    @BindView(R.id.tv_total_value)
    protected TextView tv_total_value;

    @BindView(R.id.tv_current_value)
    protected TextView tv_current_value;

    @BindView(R.id.tv_hour_saved)
    protected TextView tv_hour_saved;

    @BindView(R.id.tv_skip_msg)
    protected TextView tv_skip_msg;

    @BindView(R.id.tv_rating_review)
    protected TextView tv_rating_review;

    @BindView(R.id.btn_joinQueue)
    protected Button btn_joinQueue;

    @BindView(R.id.ratingBar)
    protected RatingBar ratingBar;

    @BindView(R.id.tv_rating)
    protected TextView tv_rating;

    @BindView(R.id.btn_no)
    protected Button btn_no;

    private String codeQR;
    private String countryShortName;
    private JsonQueue jsonQueue;
    private boolean isJoinNotPossible = false;
    private String joinErrorMsg = "";
    private boolean isCategoryData = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        ButterKnife.bind(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_join));

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtilities.setRatingBarColor(stars, this);
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

        Intent bundle = getIntent();
        if (null != bundle) {
            codeQR = bundle.getStringExtra(NoQueueBaseActivity.KEY_CODE_QR);
            isCategoryData = bundle.getBooleanExtra("isCategoryData", true);
            JsonQueue jsonQueue = (JsonQueue) bundle.getExtras().getSerializable("object");

            if (bundle.getBooleanExtra(NoQueueBaseActivity.KEY_IS_REJOIN, false)) {
                btn_joinQueue.setText(getString(R.string.yes));
                tv_skip_msg.setVisibility(View.VISIBLE);
                btn_no.setVisibility(View.VISIBLE);
            }

            if (isCategoryData) {
                queueResponse(jsonQueue);
            } else {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    //LaunchActivity.getLaunchActivity().progressDialog.show();
                    if (UserUtils.isLogin()) {
                        QueueApiModel.queuePresenter = this;
                        QueueApiModel.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);

                    } else {
                        QueueModel.queuePresenter = this;
                        QueueModel.getQueueState(UserUtils.getDeviceId(), codeQR);
                    }
                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                }
            }

        }

    }


    @Override
    public void queueError() {
        Log.d("TAG", "Queue=Error");
        //LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        // LaunchActivity.getLaunchActivity().dismissProgress();
        if (errorCode == Constants.INVALID_CREDENTIAL) {
            NoQueueBaseActivity.clearPreferences();
            ShowAlertInformation.showAuthenticErrorDialog(this);
        }

        if (errorCode == Constants.INVALID_BAR_CODE) {
            ShowAlertInformation.showBarcodeErrorDialog(this);
        }
    }

    @Override
    public void queueResponse(JsonQueue jsonQueue) {

        Log.d("TAG", "Queue=" + jsonQueue.toString());
        this.jsonQueue = jsonQueue;
        tv_store_name.setText(jsonQueue.getBusinessName());
        tv_queue_name.setText(jsonQueue.getDisplayName());
        tv_address.setText(Formatter.getFormattedAddress(jsonQueue.getStoreAddress()));
        tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getStorePhone()));
        tv_total_value.setText(String.valueOf(jsonQueue.getServingNumber()));
        tv_current_value.setText(String.valueOf(jsonQueue.getPeopleInQueue()));
        String time = getString(R.string.store_hour) + " " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour()) +
                " - " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getEndHour());
        if (jsonQueue.getDelayedInMinutes() > 0) {
            String red = "<font color='#e92270'><b>Late " + jsonQueue.getDelayedInMinutes() + " minutes.</b></font>";
            time = time + " " + red;
        }
        tv_hour_saved.setText(Html.fromHtml(time));
        ratingBar.setRating(jsonQueue.getRating());
        // tv_rating.setText(String.valueOf(Math.round(jsonQueue.getRating())));
        String reviewText = String.valueOf(jsonQueue.getRatingCount() == 0 ? "No" : jsonQueue.getRatingCount()) + " Reviews";
        tv_rating_review.setText(reviewText);

        codeQR = jsonQueue.getCodeQR();
        countryShortName = jsonQueue.getCountryShortName();
        /* Check weather join is possible or not today due to some reason */
        JoinQueueState joinQueueState = JoinQueueUtil.canJoinQueue(jsonQueue, JoinActivity.this);
        if (joinQueueState.isJoinNotPossible()) {
            isJoinNotPossible = joinQueueState.isJoinNotPossible();
            joinErrorMsg = joinQueueState.getJoinErrorMsg();
        }
        /* Update the remote join count */
        NoQueueBaseActivity.setRemoteJoinCount(jsonQueue.getRemoteJoinCount());
        if (isJoinNotPossible) {
            Toast.makeText(this, joinErrorMsg, Toast.LENGTH_LONG).show();
        } else {
            /* Auto join after scan if auto-join status is true in me screen && it is not coming from skip notification as well as history queue. */
            if (getIntent().getBooleanExtra(NoQueueBaseActivity.KEY_IS_AUTOJOIN_ELIGIBLE, true) && NoQueueBaseActivity.getAutoJoinStatus()) {
                if (jsonQueue.isAllowLoggedInUser()) {//Only login user to be allowed for join

                    if (UserUtils.isLogin()) {
                        joinQueue();
                    } else {
                        // please login to avail this feature
                        Toast.makeText(JoinActivity.this, "please login to avail this feature", Toast.LENGTH_LONG).show();
                        // Navigate to login screen
                        Intent loginIntent = new Intent(JoinActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    }
                } else {
                    // any user can join
                    joinQueue();
                }
            }
        }
    }

    @Override
    public void queueResponse(BizStoreElasticList bizStoreElasticList) {

    }


    @OnClick(R.id.btn_joinQueue)
    public void joinQueue() {
        if (isJoinNotPossible) {
            Toast.makeText(this, joinErrorMsg, Toast.LENGTH_LONG).show();
            if (joinErrorMsg.startsWith("Please login to join")) {
                // login required
                Intent loginIntent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        } else {
            if (getIntent().getBooleanExtra(NoQueueBaseActivity.KEY_IS_HISTORY, false)) {
                String errorMsg = "";
                boolean isValid = true;
                if (!UserUtils.isLogin()) {
                    errorMsg += getString(R.string.bullet) + getString(R.string.error_login) + "\n";
                    isValid = false;
                    Intent loginIntent = new Intent(JoinActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
                if (!jsonQueue.isRemoteJoinAvailable()) {
                    errorMsg += getString(R.string.bullet) + getString(R.string.error_remote_join_not_available) + "\n";
                    isValid = false;
                }
                if (jsonQueue.getRemoteJoinCount() == 0) {
                    errorMsg += getString(R.string.bullet) + getString(R.string.error_remote_join_available);
                    //TODO(hth) Forced change to true when Remote Join fails.
                    isValid = true;
                }
                if (isValid) {
                    Intent in = new Intent(this, AfterJoinActivity.class);
                    in.putExtra(NoQueueBaseActivity.KEY_CODE_QR, jsonQueue.getCodeQR());
                    in.putExtra(NoQueueBaseActivity.KEY_FROM_LIST, false);
                    in.putExtra(NoQueueBaseActivity.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
                    in.putExtra(NoQueueBaseActivity.KEY_IS_AUTOJOIN_ELIGIBLE, true);
                    in.putExtra(NoQueueBaseActivity.KEY_IS_HISTORY, getIntent().getBooleanExtra(NoQueueBaseActivity.KEY_IS_HISTORY, false));
                    in.putExtra(Constants.FROM_JOIN_SCREEN, true);
                    startActivityForResult(in, Constants.requestCodeAfterJoinQActivity);
                } else {
                    ShowAlertInformation.showThemeDialog(this, getString(R.string.error_join), errorMsg, true);
                }
            } else {
                if (jsonQueue.isAllowLoggedInUser()) {//Only login user to be allowed for join

                    if (UserUtils.isLogin()) {
                        callAfterJoin();
                    } else {
                        // please login to avail this feature
                        Toast.makeText(JoinActivity.this, "please login to avail this feature", Toast.LENGTH_LONG).show();
                        // Navigate to login screen
                        Intent loginIntent = new Intent(JoinActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    }
                } else {
                    // any user can join
                    callAfterJoin();
                }


            }
        }
    }

    private void callAfterJoin() {

        //TODO(chandra) make sure jsonQueue is not null. Prevent action on join button.
        Intent in = new Intent(this, AfterJoinActivity.class);
        in.putExtra(NoQueueBaseActivity.KEY_CODE_QR, jsonQueue.getCodeQR());
        //TODO // previously KEY_FROM_LIST  was false need to verify
        in.putExtra(NoQueueBaseActivity.KEY_FROM_LIST, false);//getArguments().getBoolean(KEY_FROM_LIST, false));
        in.putExtra(NoQueueBaseActivity.KEY_IS_AUTOJOIN_ELIGIBLE, getIntent().getBooleanExtra(NoQueueBaseActivity.KEY_IS_AUTOJOIN_ELIGIBLE, true));
        in.putExtra(NoQueueBaseActivity.KEY_IS_HISTORY, getIntent().getBooleanExtra(NoQueueBaseActivity.KEY_IS_HISTORY, false));
        in.putExtra(NoQueueBaseActivity.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
        in.putExtra(Constants.FROM_JOIN_SCREEN, true);
        startActivityForResult(in, Constants.requestCodeAfterJoinQActivity);
    }

    @OnClick(R.id.btn_no)
    public void click() {
        finish();
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
        if(null != jsonQueue) {
            /* Check weather join is possible or not today due to some reason */
            JoinQueueState joinQueueState = JoinQueueUtil.canJoinQueue(jsonQueue, JoinActivity.this);
            if (joinQueueState.isJoinNotPossible()) {
                isJoinNotPossible = joinQueueState.isJoinNotPossible();
                joinErrorMsg = joinQueueState.getJoinErrorMsg();
            }else{
                isJoinNotPossible = false;
                joinErrorMsg = "";
            }
        }
    }
}
