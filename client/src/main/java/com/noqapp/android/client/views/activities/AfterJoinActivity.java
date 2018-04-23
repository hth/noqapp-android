package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiModel;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.utils.GetTimeAgoUtils;
import com.noqapp.android.client.utils.PhoneFormatterUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AfterJoinActivity extends BaseActivity implements TokenPresenter, ResponsePresenter, ActivityCommunicator {
    private static final String TAG = AfterJoinActivity.class.getSimpleName();

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    public JsonToken jsonToken;

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

    @BindView(R.id.tv_how_long)
    protected TextView tv_how_long;

    @BindView(R.id.btn_cancel_queue)
    protected Button btn_cancel_queue;

    @BindView(R.id.tv_after)
    protected TextView tv_after;

    @BindView(R.id.tv_hour_saved)
    protected TextView tv_hour_saved;

    @BindView(R.id.tv_estimated_time)
    protected TextView tv_estimated_time;

    @BindView(R.id.ll_change_bg)
    protected LinearLayout ll_change_bg;

    private JsonTokenAndQueue jsonTokenAndQueue;
    private String codeQR;
    private String displayName;
    private String storePhone;
    private String queueName;
    private String address;
    private String topic;
    private boolean isResumeFirst = true;
    private String gotoPerson = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_join);
        ButterKnife.bind(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnResultBack();
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_qdetails));
        LaunchActivity.getLaunchActivity().activityCommunicator = this;
        Intent bundle = getIntent();
        if (null != bundle) {
            jsonTokenAndQueue = (JsonTokenAndQueue) bundle.getSerializableExtra(NoQueueBaseActivity.KEY_JSON_TOKEN_QUEUE);
            Log.d("AfterJoin bundle", jsonTokenAndQueue.toString());
            codeQR = bundle.getStringExtra(NoQueueBaseActivity.KEY_CODE_QR);
            displayName = jsonTokenAndQueue.getBusinessName();
            storePhone = jsonTokenAndQueue.getStorePhone();
            queueName = jsonTokenAndQueue.getDisplayName();
            address = jsonTokenAndQueue.getStoreAddress();
            topic = jsonTokenAndQueue.getTopic();
            tv_store_name.setText(displayName);
            tv_queue_name.setText(queueName);
            tv_address.setText(Formatter.getFormattedAddress(address));

            String time = getString(R.string.store_hour) + " " + Formatter.convertMilitaryTo12HourFormat(jsonTokenAndQueue.getStartHour()) +
                    " - " + Formatter.convertMilitaryTo12HourFormat(jsonTokenAndQueue.getEndHour());
            if (jsonTokenAndQueue.getDelayedInMinutes() > 0) {
                String red = "<font color='#e92270'><b>Late " + jsonTokenAndQueue.getDelayedInMinutes() + " minutes.</b></font>";
                time = time + " " + red;
            }
            tv_hour_saved.setText(Html.fromHtml(time));
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonTokenAndQueue.getCountryShortName(), storePhone));
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
            gotoPerson = ReviewDB.getValue(ReviewDB.KEY_GOTO, codeQR);
            if (bundle.getBooleanExtra(NoQueueBaseActivity.KEY_FROM_LIST, false)) {
                tv_total_value.setText(String.valueOf(jsonTokenAndQueue.getServingNumber()));
                tv_current_value.setText(String.valueOf(jsonTokenAndQueue.getToken()));
                tv_how_long.setText(String.valueOf(jsonTokenAndQueue.afterHowLong()));
                setBackGround(jsonTokenAndQueue.afterHowLong());
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
        tv_total_value.setText(String.valueOf(token.getServingNumber()));
        tv_current_value.setText(String.valueOf(token.getToken()));
        tv_how_long.setText(String.valueOf(token.afterHowLong()));
        setBackGround(token.afterHowLong());
        NoQueueMessagingService.subscribeTopics(topic);
        jsonTokenAndQueue.setServingNumber(token.getServingNumber());
        jsonTokenAndQueue.setToken(token.getToken());
        updateEstimatedTime();
        //save data to DB
        TokenAndQueueDB.saveJoinQueueObject(jsonTokenAndQueue);
        /* Update the remote join count */
        NoQueueBaseActivity.setRemoteJoinCount(NoQueueBaseActivity.getRemoteJoinCount() - 1);
        dismissProgress();
    }

    @Override
    public void responsePresenterResponse(JsonResponse response) {
        // To cancel
        if (null != response) {
            if (response.getResponse() == 1) {
                Toast.makeText(this, getString(R.string.cancel_queue), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.fail_to_cancel), Toast.LENGTH_LONG).show();
            }
        } else {
            //Show error
        }
        NoQueueMessagingService.unSubscribeTopics(topic);
        TokenAndQueueDB.deleteTokenQueue(codeQR);
        onBackPressed();
        dismissProgress();
    }

    @Override
    public void responsePresenterError() {
        Log.d("", "responsePresenterError");
        dismissProgress();
    }

    @Override
    public void tokenPresenterError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        dismissProgress();
        if (errorCode == Constants.INVALID_CREDENTIAL) {
            NoQueueBaseActivity.clearPreferences();
            ShowAlertInformation.showAuthenticErrorDialog(this);
        }
        if (errorCode == Constants.INVALID_BAR_CODE) {
            ShowAlertInformation.showBarcodeErrorDialog(this);
        }
    }

    @OnClick(R.id.btn_cancel_queue)
    public void cancelQueue() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            if (UserUtils.isLogin()) {
                QueueApiModel.responsePresenter = this;
                QueueApiModel.abortQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
            } else {
                QueueModel.responsePresenter = this;
                QueueModel.abortQueue(UserUtils.getDeviceId(), codeQR);
            }
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    private void callQueue() {
        if (codeQR != null) {
            Log.d("CodeQR=", codeQR);
            if (UserUtils.isLogin()) {
                boolean callingFromHistory = getIntent().getBooleanExtra(NoQueueBaseActivity.KEY_IS_HISTORY, false);
                if (!callingFromHistory && getIntent().getBooleanExtra(NoQueueBaseActivity.KEY_IS_AUTOJOIN_ELIGIBLE, false)) {
                    QueueApiModel.tokenPresenter = this;
                    QueueApiModel.joinQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
                } else if (callingFromHistory) {
                    if (getIntent().getBooleanExtra(NoQueueBaseActivity.KEY_IS_AUTOJOIN_ELIGIBLE, false)) {
                        QueueApiModel.tokenPresenter = this;
                        QueueApiModel.remoteJoinQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
                    } else {
                        //Failure
                        Toast.makeText(this, getString(R.string.error_remote_join_available), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                QueueModel.tokenPresenter = this;
                QueueModel.joinQueue(UserUtils.getDeviceId(), codeQR);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Added to update the screen if app is in background & notification received */
        if (!isResumeFirst) {
            JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(codeQR);
            if (null != jtk) {
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
        tv_estimated_time.setTextColor(Color.WHITE);
        tv_after.setText("Soon is your turn! You are:");
        //tv_after.setVisibility(View.VISIBLE);
        switch (pos) {
            case 0:
                ll_change_bg.setBackgroundResource(R.drawable.turn_1);
                tv_after.setText("It's your turn!!!");
                tv_how_long.setText(gotoPerson);
                // tv_after.setVisibility(View.GONE);
                break;
            case 1:
                ll_change_bg.setBackgroundResource(R.drawable.turn_1);
                tv_after.setText("Next is your turn! You are:");
                break;
            case 2:
                ll_change_bg.setBackgroundResource(R.drawable.turn_2);
                break;
            case 3:
                ll_change_bg.setBackgroundResource(R.drawable.turn_3);
                break;
            case 4:
                ll_change_bg.setBackgroundResource(R.drawable.turn_4);
                break;
            case 5:
                ll_change_bg.setBackgroundResource(R.drawable.turn_5);
                break;
            default:
                tv_after.setText("You are:");
                tv_after.setTextColor(ContextCompat.getColor(this, R.color.colorActionbar));
                tv_how_long.setTextColor(ContextCompat.getColor(this, R.color.colorActionbar));
                ll_change_bg.setBackgroundResource(R.drawable.square_bg_drawable);
                tv_estimated_time.setTextColor(ContextCompat.getColor(this, R.color.colorActionbar));
                break;

        }
    }

    public void setObject(JsonTokenAndQueue jq, String go_to) {
        gotoPerson = go_to;
        // jsonTokenAndQueue = jq; removed to avoided the override of the data
        jsonTokenAndQueue.setServingNumber(jq.getServingNumber());
        jsonTokenAndQueue.setToken(jq.getToken());
        tv_total_value.setText(String.valueOf(jsonTokenAndQueue.getServingNumber()));
        tv_current_value.setText(String.valueOf(jsonTokenAndQueue.getToken()));
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LaunchActivity.getLaunchActivity().activityCommunicator = null;
    }

    @Override
    public void updateUI(String qrCode, JsonTokenAndQueue jq, String go_to) {
        if (codeQR.equals(qrCode)) {
            //updating the serving status
            setObject(jq, go_to);
        }
    }

    @Override
    public void requestProcessed(String qrCode) {
        if (codeQR.equals(qrCode)) {
            //remove the screen from stack
            returnResultBack();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        returnResultBack();
        super.onBackPressed();

    }

    private void returnResultBack() {
        if (getIntent().getBooleanExtra(Constants.FROM_JOIN_SCREEN, false)) {
            Intent intent = new Intent();
            intent.putExtra(Constants.ACTIVITY_TO_CLOSE, true);
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, intent);
            } else {
                getParent().setResult(Activity.RESULT_OK, intent);
            }
        }
    }


}
