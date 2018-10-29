package com.noqapp.android.client.utils;

import com.noqapp.android.client.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowAlertInformation {

    public static void showNetworkDialog(Context context) {
        showThemeDialog(context, context.getString(R.string.networkerror), context.getString(R.string.offline));
    }

    public static void showAuthenticErrorDialog(Context context) {
        ShowAlertInformation.showThemeDialog(context, context.getString(R.string.authentication_fail_title), context.getString(R.string.authentication_fail_msg));
    }

    public static void showThemeDialog(Context context, String title, String message, boolean isGravityLeft) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
        tvtitle.setText(title);
        tv_msg.setText(message);
        if (isGravityLeft)
            tv_msg.setGravity(Gravity.LEFT);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
        Button btn_no = customDialogView.findViewById(R.id.btn_no);
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

    public static void showThemeDialog(Context context, String title, String message) {
        showThemeDialog(context, title, message, false);
    }

    public static void showBarcodeErrorDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
        tvtitle.setText(context.getString(R.string.barcode_error));
        tv_msg.setText(context.getString(R.string.barcode_error_msg));
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
        Button btn_no = customDialogView.findViewById(R.id.btn_no);
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
                ((Activity)context).finish();
            }
        });
        mAlertDialog.show();
    }

    public static void showThemePlayStoreDialog(final Context context, String title, String message, boolean isNegativeEnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
        View separator = customDialogView.findViewById(R.id.seperator);
        tvtitle.setText(title);
        tv_msg.setText(message);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setCancelable(false);
        Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
        btn_yes.setText(context.getString(R.string.btn_playstore));
        Button btn_no = customDialogView.findViewById(R.id.btn_no);
        if (isNegativeEnable) {
            btn_no.setVisibility(View.VISIBLE);
            separator.setVisibility(View.VISIBLE);
            mAlertDialog.setCanceledOnTouchOutside(true);
            mAlertDialog.setCancelable(true);
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
                AppUtilities.openPlayStore(context);
            }
        });
        try {
            mAlertDialog.show();
        } catch(Exception e){
            // WindowManager$BadTokenException will be caught and the app would not display
            // the 'Force Close' message
        }

    }

    private static void showThemeDialogWithIcon(Context context, String title, String message, boolean isGravityLeft, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        builder.setIcon(icon);
        View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
        ImageView iv_icon = customDialogView.findViewById(R.id.iv_icon);
        iv_icon.setBackground(ContextCompat.getDrawable(context,icon));
        tvtitle.setText(title);
        tv_msg.setText(message);
        if (isGravityLeft)
            tv_msg.setGravity(Gravity.LEFT);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
        Button btn_no = customDialogView.findViewById(R.id.btn_no);
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

    public static void showThemeDialog(Context context, String title, String message, int icon) {
        showThemeDialogWithIcon(context, title, message, false,  icon);
    }

    public static void showInfoDisplayDialog(Context context, String message) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCanceledOnTouchOutside(true);
        TextView tv_msg = dialog.findViewById(R.id.tv_msg);
        tv_msg.setText(message);
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public void showSnakeBar(View view, String msg) {
        Snackbar snackbar = Snackbar
                .make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
