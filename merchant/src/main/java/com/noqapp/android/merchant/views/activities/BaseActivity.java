package com.noqapp.android.merchant.views.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;

public class BaseActivity extends AppCompatActivity implements ResponseErrorPresenter {

    private Dialog dialog;
    private TextView tv_loading_msg;
    protected ImageView iv_home;
    protected ImageView actionbarBack;
    protected TextView tv_toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initProgress();
    }

    private void initProgress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.lay_progress, null, false);
        tv_loading_msg = view.findViewById(R.id.tv_loading_msg);
        builder.setView(view);
        dialog = builder.create();
    }

    protected void setProgressCancel(boolean isCancelled) {
        if (null != dialog && dialog.isShowing()) {
            dialog.setCanceledOnTouchOutside(isCancelled);
            dialog.setCancelable(isCancelled);
        }
    }

    protected void dismissProgress() {
        if (null != dialog && dialog.isShowing())
            dialog.dismiss();
    }

    protected void showProgress() {
        if (null != dialog)
            dialog.show();
    }

    protected void setProgressMessage(String msg) {
        tv_loading_msg.setText(msg);
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
