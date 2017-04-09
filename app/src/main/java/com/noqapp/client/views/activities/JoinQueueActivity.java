package com.noqapp.client.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.noqapp.client.R;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.TokenPresenter;
import com.noqapp.client.presenter.beans.JsonToken;

import butterknife.BindView;
import butterknife.ButterKnife;


public class JoinQueueActivity extends NoQueueBaseActivity implements TokenPresenter {
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

    public JsonToken mJsonToken;
    private String codeQR;
    private String displayName;
    private String storePhone;
    private String queueName;
    private String address;

    private String topic;
    public void callQueue() {
        if (codeQR != null) {
            Log.d("code qr ::", codeQR);
            QueueModel.tokenPresenter = this;
            QueueModel.joinQueue(LaunchActivity.DID, codeQR);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_queue);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        tv_toolbar_title.setText("Join");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        codeQR = getIntent().getExtras().getString(KEY_CODEQR);
        displayName = getIntent().getExtras().getString(KEY_DISPLAYNAME);
        storePhone = getIntent().getExtras().getString(KEY_STOREPHONE);
        queueName = getIntent().getExtras().getString(KEY_QUEUENAME);
        address= getIntent().getExtras().getString(KEY_ADDRESS);

        tv_store_name.setText(displayName);
        tv_queue_name.setText(queueName);
        tv_address.setText(address);
        tv_mobile.setText(storePhone);
        topic = getIntent().getExtras().getString(KEY_TOPIC);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        callQueue();
    }



    @Override
    public void onBackPressed() {

        if(mJsonToken!=null)
        {

            Intent intent = new Intent();
            intent.putExtra(KEY_CODEQR,mJsonToken.getToken());
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, intent);
            }else
            {
                getParent().setResult(Activity.RESULT_OK,intent);
            }
            //finish();
        }
        super.onBackPressed();
    }

    @Override
    public void queueResponse(JsonToken token) {
        Log.d(TAG, token.toString());
        this.mJsonToken = token;
        tv_total_value.setText(String.valueOf(String.valueOf(token.getToken())));
        tv_current_value.setText(String.valueOf(String.valueOf(token.getServingNumber())));
        tv_how_long.setText(String.valueOf(token.afterHowLong()));
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    @Override
    public void queueError() {
        Log.d(TAG, "Error");

    }
}
