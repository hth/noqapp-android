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
    private static final String TAG = JoinQueueActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtBusinessName)
    TextView txtBussinessName;
    @BindView(R.id.txtBussinessPhoneNo)
    TextView txtPhoneNo;
    @BindView(R.id.txtQueueName)
    TextView txtQueueName;
    @BindView(R.id.txtyourToken)
    TextView txtYourToken;
    @BindView(R.id.txtservingNow)
    TextView txtServingNow;
    @BindView(R.id.txtyourAfter)
    TextView txtyourAfter;
    private String codeQR;
    private String displayName;
    private String storePhone;
    private String queueName;

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
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        codeQR = getIntent().getExtras().getString(KEY_CODEQR);
        displayName = getIntent().getExtras().getString(KEY_DISPLAYNAME);
        storePhone = getIntent().getExtras().getString(KEY_STOREPHONE);
        queueName = getIntent().getExtras().getString(KEY_QUEUENAME);

        txtBussinessName.setText(displayName);
        txtPhoneNo.setText(storePhone);
        txtQueueName.setText(queueName);
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
        txtYourToken.setText(String.valueOf(token.getToken()));
        txtServingNow.setText(String.valueOf(token.getServingNumber()));
        txtyourAfter.setText(String.valueOf(token.afterHowLong()));
    }

    @Override
    public void queueError() {
        Log.d(TAG, "Error");

    }
}
