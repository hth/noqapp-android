package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.common.utils.CustomProgressBar;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;

public class BaseActivity extends AppCompatActivity implements ResponseErrorPresenter {

    protected ImageView iv_home;
    protected ImageView actionbarBack;
    protected TextView tv_toolbar_title;
    private CustomProgressBar customProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customProgressBar = new CustomProgressBar(this);
    }

    protected void dismissProgress() {
        customProgressBar.dismissProgress();
    }

    protected void showProgress() {
        customProgressBar.showProgress();
    }

    protected void setProgressCancel(boolean isCancelled) {
        customProgressBar.setProgressCancel(isCancelled);
    }

    protected void setProgressMessage(String msg) {
        customProgressBar.setProgressMessage(msg);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }
}
