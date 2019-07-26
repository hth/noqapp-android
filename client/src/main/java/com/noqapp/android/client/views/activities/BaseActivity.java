package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.common.utils.CustomProgressBar;


public abstract class BaseActivity extends AppCompatActivity implements ResponseErrorPresenter {

    private CustomProgressBar customProgressBar;
    protected ImageView iv_home;
    protected ImageView actionbarBack;
    protected TextView tv_toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customProgressBar = new CustomProgressBar(this);
    }

    protected void dismissProgress() {
        if (null != customProgressBar)
            customProgressBar.dismissProgress();
    }

    protected void showProgress() {
        if (null != customProgressBar)
            customProgressBar.showProgress();
    }

    protected void setProgressCancel(boolean isCancelled) {
        if (null != customProgressBar)
            customProgressBar.setProgressCancel(isCancelled);
    }

    protected void setProgressMessage(String msg) {
        if (null != customProgressBar)
            customProgressBar.setProgressMessage(msg);
    }

    protected void initActionsViews(boolean isHomeVisible) {
        iv_home = findViewById(R.id.iv_home);
        actionbarBack = findViewById(R.id.actionbarBack);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        iv_home.setVisibility(isHomeVisible ? View.VISIBLE : View.INVISIBLE);
        actionbarBack.setOnClickListener((View v) -> {
            finish();
        });
        iv_home.setOnClickListener((View v) -> {
            Intent goToA = new Intent(BaseActivity.this, LaunchActivity.class);
            goToA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goToA);
        });
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        if (errorCode == Constants.INVALID_BAR_CODE) {
            ShowAlertInformation.showBarcodeErrorDialog(this);
        } else {
            new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgress();
    }
}
