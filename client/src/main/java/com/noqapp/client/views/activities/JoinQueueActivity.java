package com.noqapp.client.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.noqapp.client.R;
import com.noqapp.client.helper.ShowAlertInformation;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.model.database.NoQueueDB;
import com.noqapp.client.presenter.NoQueueDBPresenter;
import com.noqapp.client.presenter.ResponsePresenter;
import com.noqapp.client.presenter.TokenPresenter;
import com.noqapp.client.presenter.beans.JsonResponse;
import com.noqapp.client.presenter.beans.JsonToken;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.utils.AppUtilities;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class JoinQueueActivity extends NoQueueBaseActivity implements TokenPresenter,ResponsePresenter {
    public static final String KEY_CODEQR = "codeqr";
    public static final String KEY_STOREPHONE = "storephone";
    public static final String KEY_DISPLAYNAME = "displayname";
    public static final String KEY_QUEUENAME = "queuename";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_TOPIC = "topic";
    private static final String TAG = JoinQueueActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_queue);
        ButterKnife.bind(this);
        tv_toolbar_title.setText("Join");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        codeQR = getIntent().getExtras().getString(KEY_CODEQR);
        displayName = getIntent().getExtras().getString(KEY_DISPLAYNAME);
        storePhone = getIntent().getExtras().getString(KEY_STOREPHONE);
        queueName = getIntent().getExtras().getString(KEY_QUEUENAME);
        address = getIntent().getExtras().getString(KEY_ADDRESS);

        tv_store_name.setText(displayName);
        tv_queue_name.setText(queueName);
        tv_address.setText(address);
        tv_mobile.setText(storePhone);
        tv_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.makeCall(LaunchActivity.getLaunchActivity(),tv_mobile.getText().toString());
            }
        });
        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.openAddressInMap(LaunchActivity.getLaunchActivity(),tv_address.getText().toString());
            }
        });
        topic = getIntent().getExtras().getString(KEY_TOPIC);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToList();
            }
        });
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            LaunchActivity.getLaunchActivity().progressDialog.show();
            callQueue();
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }





    @Override
    public void onBackPressed() {
        navigateToList();
    }

    @Override
    public void queueResponse(JsonToken token) {
        Log.d(TAG, token.toString());
        this.mJsonToken = token;
        tv_total_value.setText(String.valueOf(String.valueOf(token.getServingNumber())));
        tv_current_value.setText(String.valueOf(String.valueOf(token.getToken())));
        tv_how_long.setText(String.valueOf(token.afterHowLong()));
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void queueResponse(JsonResponse response) {
        // To cancel
        if(null!=response){
            if(response.getResponse()==1){
                Toast.makeText(this,"You successfully cancel the queue",Toast.LENGTH_LONG).show();
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                NoQueueDB queueDB = new NoQueueDB(this);
                queueDB.deleteRecord(codeQR);
                navigateToList();

            }else{
                Toast.makeText(this,"Failed to cancel the queue",Toast.LENGTH_LONG).show();
            }
        }else{
            //Show error
        }
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void queueError() {
        Log.d(TAG, "Error");

    }

    @OnClick(R.id.btn_cancel_queue)
    public void cancelQueue() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            LaunchActivity.getLaunchActivity().progressDialog.show();
            QueueModel.responsePresenter=this;
            QueueModel.abortQueue(LaunchActivity.DID, codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }

    }

    private void navigateToList(){
        if (mJsonToken != null) {
            Intent intent = new Intent();
            intent.putExtra(KEY_CODEQR, mJsonToken.getToken());
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, intent);
            } else {
                getParent().setResult(Activity.RESULT_OK, intent);
            }
        }
        super.onBackPressed();
    }

    private void callQueue() {
        if (codeQR != null) {
            Log.d("code qr ::", codeQR);
            QueueModel.tokenPresenter = this;
            QueueModel.joinQueue(LaunchActivity.DID, codeQR);
        }
    }

}
