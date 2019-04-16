package com.noqapp.android.client.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;

public class ShowCustomDialog {
    private Context context;
    private DialogClickListener dialogClickListener;


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
        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);
        if (!TextUtils.isEmpty(btnPositiveText))
            btnPositive.setText(btnPositiveText);
        if (!TextUtils.isEmpty(btnNegativeText))
            btnNegative.setText(btnNegativeText);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (null != dialogClickListener)
                    dialogClickListener.btnPositiveClick();
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (null != dialogClickListener)
                    dialogClickListener.btnNegativeClick();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}
