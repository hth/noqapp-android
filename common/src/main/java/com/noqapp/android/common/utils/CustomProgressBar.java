package com.noqapp.android.common.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
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
        try {
            if (null != dialog && dialog.isShowing())
                dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        }
    }

    public void showProgress() {
        try {
            if (null != dialog)
                if (((Activity) context).isFinishing()) {
                    //dismiss dialog
                    dialog.dismiss();
                    Log.e("YAY", "dismiss called");
                } else {
                    //show dialog
                    dialog.show();
                }
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        }

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
