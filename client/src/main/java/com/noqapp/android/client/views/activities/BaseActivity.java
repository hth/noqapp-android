package com.noqapp.android.client.views.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;


public abstract class BaseActivity extends AppCompatActivity implements ResponseErrorPresenter {

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
}
