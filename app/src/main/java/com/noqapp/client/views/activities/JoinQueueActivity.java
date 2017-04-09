package com.noqapp.client.views.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.noqapp.client.R;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.TokenPresenter;
import com.noqapp.client.presenter.beans.JsonToken;

import butterknife.BindView;
import butterknife.ButterKnife;


public class JoinQueueActivity extends AppCompatActivity implements TokenPresenter {
    public static final String KEY_CODEQR = "codeqr";
    public static final String KEY_STOREPHONE = "storephone";
    public static final String KEY_DISPLAYNAME = "displayname";
    public static final String KEY_QUEUENAME = "queuename";
    public static final String KEY_ADDRESS = "address";
    private static final String TAG = JoinQueueActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_store_name)
    public TextView tv_store_name;
    @BindView(R.id.tv_queue_name)
    public TextView tv_queue_name;
    @BindView(R.id.tv_address)
    public TextView tv_address;
    @BindView(R.id.tv_mobile)
    public TextView tv_mobile;
    @BindView(R.id.tv_total_value)
    public TextView tv_total_value;
    @BindView(R.id.tv_current_value)
    public TextView tv_current_value;
    @BindView(R.id.tv_how_long)
    public TextView tv_how_long;
    private String codeQR;
    private String displayName;
    private String storePhone;
    private String queueName;
    private String address;

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

        //Ttv_title =(TextView)findViewById(R.id.tv_title);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setTitle("");
        ButterKnife.bind(this);

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

//        txtBussinessName.setText(displayName);
//        txtPhoneNo.setText(storePhone);
//        txtQueueName.setText(queueName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        callQueue();
    }

    @Override
    protected void onResume() {
        super.onResume();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Back Click ");
                finish();
                //LaunchActivity.tempViewpager.setCurrentItem(1);
                // LaunchActivity.tempViewpager.getAdapter().notifyDataSetChanged();

            }
        });
    }


    @Override
    public void queueResponse(JsonToken token) {
        Log.d(TAG, token.toString());
//        txtYourToken.setText(String.valueOf(token.getToken()));
//        txtServingNow.setText(String.valueOf(token.getServingNumber()));
//        txtyourAfter.setText(String.valueOf(token.afterHowLong()));

        tv_total_value.setText(String.valueOf(String.valueOf(token.getToken())));
        tv_current_value.setText(String.valueOf(String.valueOf(token.getServingNumber())));
        tv_how_long.setText(String.valueOf(token.afterHowLong()));

    }

    @Override
    public void queueError() {
        Log.d(TAG, "Error");

    }
}
