package com.noqapp.client.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.noqapp.client.R;
import com.noqapp.client.helper.PhoneFormatterUtil;
import com.noqapp.client.helper.ShowAlertInformation;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.model.database.NoQueueDB;
import com.noqapp.client.presenter.ResponsePresenter;
import com.noqapp.client.presenter.TokenPresenter;
import com.noqapp.client.presenter.beans.JsonResponse;
import com.noqapp.client.presenter.beans.JsonToken;
import com.noqapp.client.utils.AppUtilities;
import com.noqapp.client.utils.Formatter;
import com.noqapp.client.views.activities.LaunchActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AfterJoinFragment extends NoQueueBaseFragment implements TokenPresenter, ResponsePresenter {

    private static final String TAG = AfterJoinFragment.class.getSimpleName();

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

    public JsonToken mJsonToken;
    private String codeQR;
    private String displayName;
    private String storePhone;
    private String queueName;
    private String address;
    private String topic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_after_join, container, false);
        ButterKnife.bind(this,view);
        Bundle bundle = getArguments();
        if (null != bundle) {
            codeQR = bundle.getString(KEY_CODEQR);
            displayName = bundle.getString(KEY_DISPLAYNAME);
            storePhone = bundle.getString(KEY_STOREPHONE);
            queueName = bundle.getString(KEY_QUEUENAME);
            address = bundle.getString(KEY_ADDRESS);
            String countryshortname=bundle.getString(KEY_COUNTRY_SHORT_NAME,"US");
            tv_store_name.setText(displayName);
            tv_queue_name.setText(queueName);
            tv_address.setText(Formatter.getFormattedAddress(address));
            tv_mobile.setText(storePhone);
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(countryshortname,storePhone));
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
            topic = bundle.getString(KEY_TOPIC);
            if(bundle.getBoolean(KEY_FROM_LIST,false)) {
                tv_total_value.setText(bundle.getString(KEY_SERVING_NO));
                tv_current_value.setText(bundle.getString(KEY_TOKEN));
                tv_how_long.setText(bundle.getString(KEY_HOW_LONG));
            }else{
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    LaunchActivity.getLaunchActivity().progressDialog.show();
                    callQueue();
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
        tv_total_value.setText(String.valueOf(String.valueOf(token.getServingNumber())));
        tv_current_value.setText(String.valueOf(String.valueOf(token.getToken())));
        tv_how_long.setText(String.valueOf(token.afterHowLong()));
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void responsePresenterResponse(JsonResponse response) {
        // To cancel
        if (null != response) {
            if (response.getResponse() == 1) {
                Toast.makeText(getActivity(), "You successfully cancel the queue", Toast.LENGTH_LONG).show();
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                NoQueueDB queueDB = new NoQueueDB(getActivity());
                queueDB.deleteTokenQueue(codeQR);
                navigateToList();

            } else {
                Toast.makeText(getActivity(), "Failed to cancel the queue", Toast.LENGTH_LONG).show();
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

    @OnClick(R.id.btn_cancel_queue)
    public void cancelQueue() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            LaunchActivity.getLaunchActivity().progressDialog.show();
            QueueModel.responsePresenter = this;
            QueueModel.abortQueue(LaunchActivity.DID, codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }

    }

    private void navigateToList() {
        LaunchActivity.getLaunchActivity().onBackPressed();
    }

    private void callQueue() {
        if (codeQR != null) {
            Log.d("code qr ::", codeQR);
            QueueModel.tokenPresenter = this;
            QueueModel.joinQueue(LaunchActivity.DID, codeQR);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Details");
        LaunchActivity.getLaunchActivity().enableDisableBack(true);
    }
}
