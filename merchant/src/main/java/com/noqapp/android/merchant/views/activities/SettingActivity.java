package com.noqapp.android.merchant.views.activities;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;

import org.apache.commons.lang3.text.WordUtils;

public class SettingActivity extends AppCompatActivity {


    public ProgressDialog progressDialog;
    public Toolbar toolbar;
    protected TextView tv_toolbar_title;
    private ImageView iv_logout;
    private ImageView actionbarBack;
    private TextView tv_name;
    private TextView tv_title;
    private ToggleButton toggleDayClosed,togglePreventJoin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
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
        if(null != title){
            tv_title.setText(title);
        }
//        LoginModel.loginPresenter = this;
//        MerchantProfileModel.merchantPresenter = this;

        toggleDayClosed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                // NoQueueBaseActivity.setAutoJoinStatus(isChecked);
            }
        });

        togglePreventJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                //  NoQueueBaseActivity.setAutoJoinStatus(isChecked);
            }
        });
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_logout.setVisibility(View.INVISIBLE);
        setActionBarTitle(getString(R.string.screen_settings));


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
}
