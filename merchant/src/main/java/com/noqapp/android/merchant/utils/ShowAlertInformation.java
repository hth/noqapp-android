package com.noqapp.android.merchant.utils;

import com.noqapp.android.merchant.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowAlertInformation {
    public static void showNetworkDialog(Context context) {
        showThemeDialog(context, context.getString(R.string.networkerror), context.getString(R.string.offline));
    }

    public static void showThemeDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
        TextView tvtitle = (TextView) customDialogView.findViewById(R.id.tvtitle);
        TextView tv_msg = (TextView) customDialogView.findViewById(R.id.tv_msg);
        tvtitle.setText(title);
        tv_msg.setText(message);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        Button btn_yes = (Button) customDialogView.findViewById(R.id.btn_yes);
        Button btn_no = (Button) customDialogView.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    public static void showThemePlayStoreDialog(final Context context, String title, String message, boolean isNegativeEnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
        TextView tvtitle = (TextView) customDialogView.findViewById(R.id.tvtitle);
        TextView tv_msg = (TextView) customDialogView.findViewById(R.id.tv_msg);
        View separator = (View) customDialogView.findViewById(R.id.seperator);
        tvtitle.setText(title);
        tv_msg.setText(message);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        Button btn_yes = (Button) customDialogView.findViewById(R.id.btn_yes);
        btn_yes.setText(context.getString(R.string.btn_playstore));
        Button btn_no = (Button) customDialogView.findViewById(R.id.btn_no);
        if (isNegativeEnable) {
            btn_no.setVisibility(View.VISIBLE);
            separator.setVisibility(View.VISIBLE);
            mAlertDialog.setCanceledOnTouchOutside(true);
        }
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openPlayStore(context);
            }
        });
        mAlertDialog.show();
    }
}
