package com.noqapp.android.merchant.utils;

import com.noqapp.android.merchant.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowCustomDialog {
    private Context context;
    private boolean showNegativeBtn = false;
    private boolean isGravityLeft = false;
    private int icon;
    private DialogClickListener dialogClickListener;

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setGravityLeft(boolean gravityLeft) {
        isGravityLeft = gravityLeft;
    }

    public interface DialogClickListener {
        void btnPositiveClick();

        void btnNegativeClick();
    }

    public ShowCustomDialog setDialogClickListener(DialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
        return this;
    }

    public ShowCustomDialog(Context context) {
        this.context = context;
    }

    public ShowCustomDialog(Context context, boolean showNegativeBtn) {
        this.context = context;
        this.showNegativeBtn = showNegativeBtn;
    }

    public void displayDialog(String title, String msg) {
        displayDialog(title, msg, "", "");
    }

    public void displayDialog(String title, String msg, String btnPositiveText, String btnNegativeText) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        TextView tv_title = dialog.findViewById(R.id.tv_title);
        TextView tv_msg = dialog.findViewById(R.id.tv_msg);
        tv_title.setText(title);
        tv_msg.setText(msg);
        if (isGravityLeft) {
            tv_msg.setGravity(Gravity.LEFT);
        }

        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);
        btnNegative.setVisibility(showNegativeBtn ? View.VISIBLE: View.GONE);

        if (!TextUtils.isEmpty(btnPositiveText)) {
            btnPositive.setText(btnPositiveText);
        }
        if (!TextUtils.isEmpty(btnNegativeText)) {
            btnNegative.setText(btnNegativeText);
        }
        btnPositive.setOnClickListener(v -> {
            dialog.dismiss();
            if (null != dialogClickListener)
                dialogClickListener.btnPositiveClick();
        });
        btnNegative.setOnClickListener(v -> {
            dialog.dismiss();
            if (null != dialogClickListener)
                dialogClickListener.btnNegativeClick();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}
