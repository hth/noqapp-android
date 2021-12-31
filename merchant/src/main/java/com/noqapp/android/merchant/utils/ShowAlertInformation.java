package com.noqapp.android.merchant.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.noqapp.android.merchant.R;

public class ShowAlertInformation {

    public static void showNetworkDialog(Context context) {
        showThemeDialog(context, context.getString(R.string.networkerror), context.getString(R.string.offline));
    }

    public static void showThemeDialog(Context context, String title, String message) {
        ShowCustomDialog showDialog = new ShowCustomDialog(context);
        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
            @Override
            public void btnPositiveClick() {
                //Do nothing
            }

            @Override
            public void btnNegativeClick() {
                //Do nothing
            }
        });
        showDialog.displayDialog(title, message);
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
        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
        btn_no.setOnClickListener(v -> mAlertDialog.dismiss());
        btn_yes.setOnClickListener(v -> AppUtils.openPlayStore(context));
        try {
            mAlertDialog.show();
            resizeAlert(mAlertDialog, context);
        } catch (Exception e) {
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
        iv_icon.setBackground(ContextCompat.getDrawable(context, icon));
        tvtitle.setText(title);
        tv_msg.setText(message);
        if (isGravityLeft) {
            tv_msg.setGravity(Gravity.LEFT);
        }
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mAlertDialog.setCanceledOnTouchOutside(false);
        Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
        Button btn_no = customDialogView.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(v -> mAlertDialog.dismiss());
        btn_yes.setOnClickListener(v -> mAlertDialog.dismiss());
        mAlertDialog.show();
        resizeAlert(mAlertDialog, context);
    }

    public static void showThemeDialog(Context context, String title, String message, int icon) {
        showThemeDialogWithIcon(context, title, message, false, icon);
    }


    public static void resizeAlert(AlertDialog dialog, Context context) {
        try {
//            DisplayMetrics displayMetrics = new DisplayMetrics();
//            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//            int displayWidth = displayMetrics.widthPixels;
//            int displayHeight = displayMetrics.heightPixels;
//
//            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//            layoutParams.copyFrom(dialog.getWindow().getAttributes());
//            int dialogWindowWidth = (int) (displayWidth * 0.75f);
//            // Set alert dialog height equal to screen height 70%
//            int dialogWindowHeight = (int) (displayHeight * 0.5f);
//            layoutParams.width = dialogWindowWidth;
//            layoutParams.height = dialogWindowHeight;
//
//            // Apply the newly created layout parameters to the alert dialog window
//            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
