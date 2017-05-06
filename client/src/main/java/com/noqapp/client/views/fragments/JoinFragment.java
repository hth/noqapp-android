package com.noqapp.client.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.noqapp.client.R;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.QueuePresenter;
import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.utils.AppUtilities;
import com.noqapp.client.utils.Formatter;
import com.noqapp.client.views.activities.LaunchActivity;

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


    @BindView(R.id.btn_joinqueue)
    protected Button btn_joinqueue;

    private String codeQR;
    private JsonQueue jsonQueue;
    private String frtag;
    public JoinFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            codeQR = bundle.getString(KEY_CODEQR);
            LaunchActivity.getLaunchActivity().progressDialog.show();
            QueueModel.queuePresenter = JoinFragment.this;
            QueueModel.getQueueState(LaunchActivity.DID, codeQR);
            if (bundle.getBoolean(KEY_FROM_LIST, false)) {
                frtag = LaunchActivity.tabList;
            } else {
                frtag = LaunchActivity.tabHome;
            }
        }
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Join");
    }

    @Override
    public void queueError() {
        Log.d("Queue=", "Error");
    }

    @Override
    public void queueResponse(JsonQueue jsonQueue) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        Log.d("Queue=", jsonQueue.toString());
        this.jsonQueue = jsonQueue;
        tv_store_name.setText(jsonQueue.getBusinessName());
        tv_queue_name.setText(jsonQueue.getDisplayName());
        tv_address.setText(Formatter.getFormattedAddress(jsonQueue.getStoreAddress()));
        tv_mobile.setText(jsonQueue.getStorePhone());
        tv_total_value.setText(String.valueOf(jsonQueue.getServingNumber()));
        tv_current_value.setText(String.valueOf(jsonQueue.getLastNumber()));
        codeQR = jsonQueue.getCodeQR();
    }

    @OnClick(R.id.btn_joinqueue)
    public void joinQueue() {
        Bundle b = new Bundle();
        b.putString(KEY_CODEQR, jsonQueue.getCodeQR());
        b.putString(KEY_DISPLAYNAME, jsonQueue.getBusinessName());
        b.putString(KEY_STOREPHONE, jsonQueue.getStorePhone());
        b.putString(KEY_QUEUENAME, jsonQueue.getDisplayName());
        b.putString(KEY_ADDRESS, jsonQueue.getStoreAddress());
        b.putString(KEY_TOPIC, jsonQueue.getTopic());
        b.putBoolean(KEY_FROM_LIST, false);
        b.putString(KEY_SERVING_NO, String.valueOf(jsonQueue.getServingNumber()));
        AfterJoinFragment ajf = new AfterJoinFragment();
        ajf.setArguments(b);
        replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, ajf, TAG, frtag);
    }



}
