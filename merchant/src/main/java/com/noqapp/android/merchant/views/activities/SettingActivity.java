package com.noqapp.android.merchant.views.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.QueueSettingModel;
import com.noqapp.android.merchant.presenter.beans.body.QueueSetting;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.interfaces.QueueSettingPresenter;

import org.apache.commons.lang3.text.WordUtils;

public class SettingActivity extends AppCompatActivity implements QueueSettingPresenter,View.OnClickListener{


    public ProgressDialog progressDialog;
    public Toolbar toolbar;
    protected TextView tv_toolbar_title;
    private ImageView iv_logout;
    private ImageView actionbarBack;
    private TextView tv_name;
    private TextView tv_title;
    private ToggleButton toggleDayClosed,togglePreventJoin;
    private String codeQR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
        QueueSettingModel.queueSettingPresenter = this;
        setContentView(R.layout.activity_setting);
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        iv_logout = (ImageView) findViewById(R.id.iv_logout);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        initProgress();
        tv_title = (TextView) findViewById(R.id.tv_title);
        toggleDayClosed = (ToggleButton) findViewById(R.id.toggleDayClosed);
        togglePreventJoin = (ToggleButton) findViewById(R.id.togglePreventJoin);
        String title =getIntent().getStringExtra("title");
        codeQR = getIntent().getStringExtra("codeQR");
        if(null != title){
            tv_title.setText(title);
        }
        toggleDayClosed.setOnClickListener(this);
        togglePreventJoin.setOnClickListener(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_logout.setVisibility(View.INVISIBLE);
        setActionBarTitle(getString(R.string.screen_settings));
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
           QueueSettingModel.getQueueState(UserUtils.getDeviceId(),UserUtils.getEmail(),UserUtils.getAuth(),codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
        }

    }

    public void setActionBarTitle(String title) {
        tv_toolbar_title.setText(title);
    }

    public void setUserName() {
        tv_name.setText(WordUtils.initials(LaunchActivity.getLaunchActivity().getUserName()));
    }
    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
    }

    public void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void setProgressTitle(String msg) {
        progressDialog.setMessage(msg);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void queueSettingResponse(QueueSetting queueSetting) {
        if(null != queueSetting){
            toggleDayClosed.setChecked(queueSetting.isDayClosed());
            togglePreventJoin.setChecked(queueSetting.isPreventJoining());
        }
        dismissProgress();

    }

    @Override
    public void queueSettingError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorcode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        if(errorcode == Constants.INVALID_CREDENTIAL){
           // LaunchActivity.getLaunchActivity().clearLoginData();
            Intent intent = new Intent();
            intent.putExtra(Constants.CLEAR_DATA, true);
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, intent);
            } else {
                getParent().setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            QueueSetting queueSetting = new QueueSetting();
            queueSetting.setCodeQR(codeQR);
            queueSetting.setDayClosed(toggleDayClosed.isChecked());
            queueSetting.setPreventJoining(togglePreventJoin.isChecked());
            QueueSettingModel.modify(UserUtils.getDeviceId(),UserUtils.getEmail(),UserUtils.getAuth(),queueSetting);
        } else {
            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
            if(v.getId() == R.id.toggleDayClosed){
                toggleDayClosed.setChecked(!toggleDayClosed.isChecked());
            }else{
                togglePreventJoin.setChecked(!togglePreventJoin.isChecked());
            }
        }
    }
}
