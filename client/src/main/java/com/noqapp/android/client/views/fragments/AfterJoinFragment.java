package com.noqapp.android.client.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.noqapp.android.client.utils.PhoneFormatterUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AfterJoinFragment extends NoQueueBaseFragment implements TokenPresenter, ResponsePresenter {

    private static final String TAG = AfterJoinFragment.class.getSimpleName();
    public JsonToken mJsonToken;

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

    @BindView(R.id.ll_change_bg)
    protected LinearLayout ll_change_bg;

    private JsonTokenAndQueue jsonQueue;
    private String codeQR;
    private String displayName;
    private String storePhone;
    private String queueName;
    private String address;
    private String topic;
    private boolean isResumeFirst = true;
    private String gotoPerson = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_after_join, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (null != bundle) {
            jsonQueue = (JsonTokenAndQueue) bundle.getSerializable(KEY_JSON_TOKEN_QUEUE);
            Log.d("AfterJoin bundle", jsonQueue.toString());
            codeQR = bundle.getString(KEY_CODE_QR);
            displayName = jsonQueue.getBusinessName();
            storePhone = jsonQueue.getStorePhone();
            queueName = jsonQueue.getDisplayName();
            address = jsonQueue.getStoreAddress();
            topic = jsonQueue.getTopic();
            tv_store_name.setText(displayName);
            tv_queue_name.setText(queueName);
            tv_address.setText(Formatter.getFormattedAddress(address));
            tv_hour_saved.setText(getString(R.string.store_hour) + " " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour()) + " - " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getEndHour()));
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), storePhone));
            tv_mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtilities.makeCall(LaunchActivity.getLaunchActivity(), tv_mobile.getText().toString());
                }
            });
            tv_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtilities.openAddressInMap(LaunchActivity.getLaunchActivity(), tv_address.getText().toString());
                }
            });
            gotoPerson = ReviewDB.getValue(ReviewDB.KEY_GOTO, codeQR);
            if (bundle.getBoolean(KEY_FROM_LIST, false)) {
                tv_total_value.setText(String.valueOf(jsonQueue.getServingNumber()));
                tv_current_value.setText(String.valueOf(jsonQueue.getToken()));
                tv_how_long.setText(String.valueOf(jsonQueue.afterHowLong()));
                setBackGround(jsonQueue.afterHowLong());
            } else {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    if (isResumeFirst) {
                        LaunchActivity.getLaunchActivity().progressDialog.show();
                        callQueue();
                    }
                } else {
                    ShowAlertInformation.showNetworkDialog(getActivity());
                }
            }
        }
        return view;
    }

    @Override
    public void tokenPresenterResponse(JsonToken token) {
        Log.d(TAG, token.toString());
        this.mJsonToken = token;
        tv_total_value.setText(String.valueOf(token.getServingNumber()));
        tv_current_value.setText(String.valueOf(token.getToken()));
        tv_how_long.setText(String.valueOf(token.afterHowLong()));
        setBackGround(token.afterHowLong());
        NoQueueMessagingService.subscribeTopics(topic);
        jsonQueue.setServingNumber(token.getServingNumber());
        jsonQueue.setToken(token.getToken());
        //save data to DB
        TokenAndQueueDB.saveJoinQueueObject(jsonQueue);
             /* Update the remote join count */
        NoQueueBaseActivity.setRemoteJoinCount(NoQueueBaseActivity.getRemoteJoinCount() - 1);
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void responsePresenterResponse(JsonResponse response) {
        // To cancel
        if (null != response) {
            if (response.getResponse() == 1) {
                Toast.makeText(getActivity(), getString(R.string.cancel_queue), Toast.LENGTH_LONG).show();
                NoQueueMessagingService.unSubscribeTopics(topic);
                TokenAndQueueDB.deleteTokenQueue(codeQR);
                navigateToList();
            } else {
                Toast.makeText(getActivity(), getString(R.string.fail_to_cancel), Toast.LENGTH_LONG).show();
            }
        } else {
            //Show error
        }
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void responsePresenterError() {
        Log.d(TAG, "responsePresenterError");
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void tokenPresenterError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        if (errorCode == Constants.INVALID_CREDENTIAL) {
            NoQueueBaseActivity.clearPreferences();
            ShowAlertInformation.showAuthenticErrorDialog(getActivity());
        }
        if (errorCode == Constants.INVALID_BAR_CODE) {
            ShowAlertInformation.showBarcodeErrorDialog(LaunchActivity.getLaunchActivity());
        }
    }

    @OnClick(R.id.btn_cancel_queue)
    public void cancelQueue() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            LaunchActivity.getLaunchActivity().progressDialog.show();
            if (UserUtils.isLogin()) {
                QueueApiModel.responsePresenter = this;
                QueueApiModel.abortQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
            } else {
                QueueModel.responsePresenter = this;
                QueueModel.abortQueue(UserUtils.getDeviceId(), codeQR);
            }
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    private void navigateToList() {
        LaunchActivity.getLaunchActivity().onBackPressed();
    }

    private void callQueue() {
        if (codeQR != null) {
            Log.d("CodeQR=", codeQR);
            if (UserUtils.isLogin()) {
                boolean callingFromHistory = getArguments().getBoolean(KEY_IS_HISTORY, false);
                if (!callingFromHistory && getArguments().getBoolean(KEY_IS_AUTOJOIN_ELIGIBLE, false)) {
                    QueueApiModel.tokenPresenter = this;
                    QueueApiModel.joinQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
                } else if (callingFromHistory) {
                    if (getArguments().getBoolean(KEY_IS_AUTOJOIN_ELIGIBLE, false)) {
                        QueueApiModel.tokenPresenter = this;
                        QueueApiModel.remoteJoinQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
                    } else {
                        //Failure
                        Toast.makeText(getActivity(), getString(R.string.error_remote_join_available), Toast.LENGTH_LONG).show();
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
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_qdetails));
        LaunchActivity.getLaunchActivity().enableDisableBack(true);
        /* Added to update the screen if app is in background & notification received */
        if (!isResumeFirst) {
            JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(codeQR);
            if (null != jtk)
                setObject(jtk, gotoPerson);
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
                tv_after.setTextColor(getResources().getColor(R.color.colorActionbar));
                tv_how_long.setTextColor(getResources().getColor(R.color.colorActionbar));
                ll_change_bg.setBackgroundResource(R.drawable.square_bg_drawable);
                break;
        }
    }

    public void setObject(JsonTokenAndQueue jq, String go_to) {
        gotoPerson = go_to;
        jsonQueue = jq;
        tv_total_value.setText(String.valueOf(jsonQueue.getServingNumber()));
        tv_current_value.setText(String.valueOf(jsonQueue.getToken()));
        tv_how_long.setText(String.valueOf(jsonQueue.afterHowLong()));
        setBackGround(jq.afterHowLong() > 0 ? jq.afterHowLong() : 0);
    }
}
