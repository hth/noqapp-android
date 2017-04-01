package com.noqapp.client.views.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.noqapp.client.R;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.TokenPresenter;
import com.noqapp.client.presenter.beans.JsonToken;
import com.noqapp.client.views.fragments.ListQueueFragment;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;


public class JoinQueueActivity extends AppCompatActivity implements TokenPresenter {
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

    private static final String TAG = JoinQueueActivity.class.getSimpleName();
    public static final String KEY_CODEQR = "codeqr";
    public static final String KEY_STOREPHONE = "storephone";
    public static final String KEY_DISPLAYNAME = "displayname";
    public static final String KEY_QUEUENAME = "queuename";
    private String codeQR;
    private String displayName;
    private String storePhone;
    private String queueName;

    public void callQueue()
    {
        if (codeQR != null){
            Log.d("code qr ::",codeQR);
            QueueModel.tokenPresenter = this;
            QueueModel.joinQueue(LaunchActivity.DID,codeQR);}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_queue);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        codeQR = getIntent().getExtras().getString(KEY_CODEQR);
        displayName  = getIntent().getExtras().getString(KEY_DISPLAYNAME);
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
                Log.i(TAG,"Back Click ");
                finish();
                LaunchActivity.tempViewpager.setCurrentItem(1);
            }
        });
    }


    @Override
    public void queueResponse(JsonToken token) {
        Log.d(TAG,token.toString());
        int tokenNo  = token.getToken();
        int serviceNo = token.getServingNumber();
        int previousNo = tokenNo - serviceNo;
        txtYourToken.setText(String.valueOf(tokenNo));
        txtServingNow.setText(String.valueOf(serviceNo));
        txtyourAfter.setText(String.valueOf(previousNo));


    }

    @Override
    public void queueError() {
        Log.d(TAG,"Error");

    }
}
