package com.noqapp.android.client.views.fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiModel;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.wrapper.JoinQueueState;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.utils.JoinQueueUtil;
import com.noqapp.android.client.utils.PhoneFormatterUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JoinFragment extends NoQueueBaseFragment implements QueuePresenter {
    private final String TAG = JoinFragment.class.getSimpleName();

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
    private String frtag;
    private boolean isJoinNotPossible = false;
    private String joinErrorMsg = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join, container, false);
        ButterKnife.bind(this, view);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtilities.setRatingBarColor(stars, getActivity());
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

        Bundle bundle = getArguments();
        if (null != bundle) {
            codeQR = bundle.getString(KEY_CODE_QR);
            boolean callingFromHistory = getArguments().getBoolean(KEY_IS_HISTORY, false);
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                LaunchActivity.getLaunchActivity().progressDialog.show();
                if (UserUtils.isLogin()) {
                    QueueApiModel.queuePresenter = this;
                    if (callingFromHistory) {
                        QueueApiModel.remoteScanQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
                    } else {
                        QueueApiModel.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
                    }
                } else {
                    QueueModel.queuePresenter = this;
                    QueueModel.getQueueState(UserUtils.getDeviceId(), codeQR);
                }
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
            if (bundle.getBoolean(KEY_FROM_LIST, false)) {
                frtag = LaunchActivity.tabList;
                if (callingFromHistory) {
                    btn_joinQueue.setText(getString(R.string.remotejoin));
                }

            } else {
                frtag = LaunchActivity.tabHome;
            }
            if (bundle.getBoolean(KEY_IS_REJOIN, false)) {
                btn_joinQueue.setText(getString(R.string.yes));
                tv_skip_msg.setVisibility(View.VISIBLE);
                btn_no.setVisibility(View.VISIBLE);
                frtag = LaunchActivity.getLaunchActivity().getCurrentSelectedTabTag();
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Join");
        LaunchActivity.getLaunchActivity().enableDisableBack(true);
    }

    @Override
    public void queueError() {
        Log.d(TAG, "Queue=Error");
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

    @Override
    public void queueResponse(JsonQueue jsonQueue) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        Log.d(TAG, "Queue=" + jsonQueue.toString());
        this.jsonQueue = jsonQueue;
        tv_store_name.setText(jsonQueue.getBusinessName());
        tv_queue_name.setText(jsonQueue.getDisplayName());
        tv_address.setText(Formatter.getFormattedAddress(jsonQueue.getStoreAddress()));
        tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getStorePhone()));
        tv_total_value.setText(String.valueOf(jsonQueue.getServingNumber()));
        tv_current_value.setText(String.valueOf(jsonQueue.getPeopleInQueue()));
        tv_hour_saved.setText(getString(R.string.store_hour) + " " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour()) + " - " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getEndHour()));
        ratingBar.setRating(jsonQueue.getRating());
        // tv_rating.setText(String.valueOf(Math.round(jsonQueue.getRating())));
        tv_rating_review.setText(String.valueOf(jsonQueue.getRatingCount())
                + " Reviews");

        codeQR = jsonQueue.getCodeQR();
        countryShortName = jsonQueue.getCountryShortName();
        /* Check weather join is possible or not today due to some reason */
        JoinQueueState joinQueueState = JoinQueueUtil.canJoinQueue(jsonQueue, getContext());
        if (joinQueueState.isJoinNotPossible()) {
            isJoinNotPossible = joinQueueState.isJoinNotPossible();
            joinErrorMsg = joinQueueState.getJoinErrorMsg();
        }
        /* Update the remote join count */
        NoQueueBaseActivity.setRemoteJoinCount(jsonQueue.getRemoteJoinCount());
        if (isJoinNotPossible) {
            Toast.makeText(getActivity(), joinErrorMsg, Toast.LENGTH_LONG).show();
        } else {
            /* Auto join after scan if auto-join status is true in me screen && it is not coming from skip notification as well as history queue. */
            if (getArguments().getBoolean(KEY_IS_AUTOJOIN_ELIGIBLE, true) && NoQueueBaseActivity.getAutoJoinStatus()) {
                joinQueue();
            }
        }
    }

    @OnClick(R.id.btn_joinQueue)
    public void joinQueue() {
        if (isJoinNotPossible) {
            Toast.makeText(getActivity(), joinErrorMsg, Toast.LENGTH_LONG).show();
        } else {
            if (getArguments().getBoolean(KEY_IS_HISTORY, false)) {
                String errorMsg = "";
                boolean isValid = true;
                if (!UserUtils.isLogin()) {
                    errorMsg = getString(R.string.error_login)+"\n";
                    isValid = false;
                }
                if (!jsonQueue.isRemoteJoinAvailable()) {
                    errorMsg = getString(R.string.error_remote_join_not_available)+"\n";
                    isValid = false;
                }
                if (jsonQueue.getRemoteJoinCount() == 0) {
                    errorMsg = getString(R.string.error_remote_join_available);
                    isValid = false;
                }
                if(isValid) {
                    Bundle b = new Bundle();
                    b.putString(KEY_CODE_QR, jsonQueue.getCodeQR());
                    b.putBoolean(KEY_FROM_LIST, false);
                    b.putSerializable(KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
                    b.putBoolean(KEY_IS_AUTOJOIN_ELIGIBLE, true);
                    b.putBoolean(KEY_IS_HISTORY, getArguments().getBoolean(KEY_IS_HISTORY, false));
                    AfterJoinFragment afterJoinFragment = new AfterJoinFragment();
                    afterJoinFragment.setArguments(b);
                    replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, afterJoinFragment, TAG, frtag);
                }else{
                    ShowAlertInformation.showThemeDialog(getActivity(),getString(R.string.error_join),errorMsg);
                }
            } else {
                //TODO(chandra) make sure jsonQueue is not null. Prevent action on join button.
                Bundle b = new Bundle();
                b.putString(KEY_CODE_QR, jsonQueue.getCodeQR());
                b.putBoolean(KEY_FROM_LIST, false);
                b.putBoolean(KEY_IS_AUTOJOIN_ELIGIBLE, getArguments().getBoolean(KEY_IS_AUTOJOIN_ELIGIBLE, true));
                b.putBoolean(KEY_IS_HISTORY, getArguments().getBoolean(KEY_IS_HISTORY, false));
                b.putSerializable(KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
                AfterJoinFragment afterJoinFragment = new AfterJoinFragment();
                afterJoinFragment.setArguments(b);
                replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, afterJoinFragment, TAG, frtag);
            }
        }
    }

    @OnClick(R.id.btn_no)
    public void click() {
        LaunchActivity.getLaunchActivity().onBackPressed();
    }


}
