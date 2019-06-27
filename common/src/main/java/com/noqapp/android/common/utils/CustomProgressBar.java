package com.noqapp.android.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.noqapp.android.common.R;

public class CustomProgressBar {

    private Context context;
    private Dialog dialog;
    private TextView tv_loading_msg;

    public CustomProgressBar(Context context) {
        this.context = context;
        initProgress();
    }

    private void initProgress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.lay_progress, null, false);
        tv_loading_msg = view.findViewById(R.id.tv_loading_msg);
        builder.setView(view);
        dialog = builder.create();
    }


    public void dismissProgress() {
        if (null != dialog && dialog.isShowing())
            dialog.dismiss();
    }

    public void showProgress() {
        if (null != dialog)
            dialog.show();
    }

    public void setProgressCancel(boolean isCancelled) {
        if (null != dialog && dialog.isShowing()) {
            dialog.setCanceledOnTouchOutside(isCancelled);
            dialog.setCancelable(isCancelled);
        }
    }

    public void setProgressMessage(String msg) {
        tv_loading_msg.setText(msg);
    }
}
