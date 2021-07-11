package com.noqapp.android.client.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.noqapp.android.client.R;

import kotlin.jvm.functions.Function0;

public class ShowAlertInformation {

    public static void showNetworkDialog(Context context) {
        showThemeDialog(context, context.getString(R.string.networkerror), context.getString(R.string.offline));
    }

    public static void showAuthenticErrorDialog(Activity context, Function0 loginAgain) {
        ShowAlertInformation.showThemeDialogWithListener(context, context.getString(R.string.authentication_fail_title), context.getString(R.string.authentication_fail_msg), false,loginAgain);
    }

    public static void showErrorDialog(Context context, String error) {
        if (error != null) {
            ShowAlertInformation.showThemeDialog(context, error, context.getString(R.string.authentication_fail_msg));
        } else {
            ShowAlertInformation.showThemeDialog(context, "Oops! Something went wrong...", context.getString(R.string.authentication_fail_msg));
        }
    }

    public static void showThemeDialog(Context context, String title, String message, boolean isGravityLeft) {
        ShowCustomDialog showDialog = new ShowCustomDialog(context);
        showDialog.setGravityLeft(isGravityLeft);
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

    public static void showThemeDialogWithListener(Context context, String title, String message, boolean isGravityLeft, Function0 positiveButtonCallback) {
        ShowCustomDialog showDialog = new ShowCustomDialog(context);
        showDialog.setGravityLeft(isGravityLeft);
        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
            @Override
            public void btnPositiveClick() {
                positiveButtonCallback.invoke();
            }

            @Override
            public void btnNegativeClick() {
                //Do nothing
            }
        });
        showDialog.displayDialog(title, message);
    }

    public static void showThemeDialog(Context context, String title, String message) {
        showThemeDialog(context, title, message, false);
    }

    public static void showBarcodeErrorDialog(final Context context) {
        ShowCustomDialog showDialog = new ShowCustomDialog(context);
        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
            @Override
            public void btnPositiveClick() {
                ((Activity) context).finish();
            }

            @Override
            public void btnNegativeClick() {
                //Do nothing
            }
        });
        showDialog.displayDialog(context.getString(R.string.barcode_error), context.getString(R.string.barcode_error_msg));
    }

    public static void showAlertWithDismissCapability(final Context context, String title, String message) {
        ShowCustomDialog showDialog = new ShowCustomDialog(context);
        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
            @Override
            public void btnPositiveClick() {
                ((Activity) context).finish();
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
        btn_no.setOnClickListener((View v) -> {
            mAlertDialog.dismiss();
        });
        btn_yes.setOnClickListener((View v) -> {
            AppUtils.openPlayStore(context);
        });
        try {
            mAlertDialog.show();
        } catch (Exception e) {
            // WindowManager$BadTokenException will be caught and the app would not display
            // the 'Force Close' message
        }
    }

    private static void showThemeDialogWithIcon(Activity context, String title, String message, boolean isGravityLeft, int icon) {
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
        mAlertDialog.setCanceledOnTouchOutside(false);
        Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
        Button btn_no = customDialogView.findViewById(R.id.btn_no);
        btn_no.setOnClickListener((View v) -> mAlertDialog.dismiss());
        btn_yes.setOnClickListener((View v) -> mAlertDialog.dismiss());
        mAlertDialog.show();
    }

    public static void showThemeDialog(Activity context, String title, String message, int icon) {
        showThemeDialogWithIcon(context, title, message, false, icon);
    }

    public static void showInfoDisplayDialog(Context context, String title, String message) {
        ShowCustomDialog showDialog = new ShowCustomDialog(context, false);
        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
            @Override
            public void btnPositiveClick() {

            }

            @Override
            public void btnNegativeClick() {
                //Do nothing
            }
        });
        showDialog.displayDialog(title, message);
    }

    public void showSnakeBar(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
