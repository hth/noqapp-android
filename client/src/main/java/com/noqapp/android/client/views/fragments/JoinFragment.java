package com.noqapp.android.client.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiModel;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Formatter;
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

    @BindView(R.id.btn_joinQueue)
    protected Button btn_joinQueue;

    private String codeQR;
    private String countryShortName;
    private JsonQueue jsonQueue;
    private String frtag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join, container, false);
        ButterKnife.bind(this, view);

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
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                LaunchActivity.getLaunchActivity().progressDialog.show();

                if (UserUtils.isLogin()) {
                    QueueApiModel.queuePresenter = this;
                    QueueApiModel.remoteScanQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
                } else {
                    QueueModel.queuePresenter = this;
                    QueueModel.getQueueState(UserUtils.getDeviceId(), codeQR);
                }
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
            if (bundle.getBoolean(KEY_FROM_LIST, false)) {
                frtag = LaunchActivity.tabList;
                if (bundle.getBoolean(KEY_IS_HISTORY, false)) {
                    btn_joinQueue.setText(getString(R.string.remotejoin));
                }

            } else {
                frtag = LaunchActivity.tabHome;
            }
            if (bundle.getBoolean(KEY_IS_REJOIN, false)) {
                btn_joinQueue.setText(getString(R.string.re_join));
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
        codeQR = jsonQueue.getCodeQR();
        countryShortName = jsonQueue.getCountryShortName();
        /* Update the remote join count */
        NoQueueBaseActivity.setRemoteJoinCount(jsonQueue.getRemoteJoin());
        /* Auto join after scan if autojoin status is true in me screen */
        if (!getArguments().getBoolean(KEY_FROM_LIST, false) && NoQueueBaseActivity.getAutoJoinStatus()) {
            joinQueue();
        }
    }

    @OnClick(R.id.btn_joinQueue)
    public void joinQueue() {
        if (getArguments().getBoolean(KEY_IS_HISTORY, false)) {


            String phone = NoQueueBaseActivity.getPhoneNo();
            // if (!phone.equals("")) {
//                if(jsonQueue.getRemoteJoin()==0){
//                    Toast.makeText(getActivity(),getString(R.string.error_remote_join_available),Toast.LENGTH_LONG).show();
//                }else{
            Bundle b = new Bundle();
            b.putString(KEY_CODE_QR, jsonQueue.getCodeQR());
            b.putBoolean(KEY_FROM_LIST, false);
            b.putSerializable(KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
            AfterJoinFragment afterJoinFragment = new AfterJoinFragment();
            afterJoinFragment.setArguments(b);
            replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, afterJoinFragment, TAG, frtag);

            // QueueModel.remoteJoinQueue
            //}
//            } else {
//                Toast.makeText(getActivity(),getString(R.string.error_login),Toast.LENGTH_LONG).show();
//            }
        } else {
            Bundle b = new Bundle();
            b.putString(KEY_CODE_QR, jsonQueue.getCodeQR());
            b.putBoolean(KEY_FROM_LIST, false);
            b.putSerializable(KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
            AfterJoinFragment afterJoinFragment = new AfterJoinFragment();
            afterJoinFragment.setArguments(b);
            replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, afterJoinFragment, TAG, frtag);
        }
    }
}
