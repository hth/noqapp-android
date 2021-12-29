package com.noqapp.android.client.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowCustomDialog {
    private Context context;
    private boolean showNegativeBtn = false;
    private boolean isGravityLeft = false;
    private DialogClickListener dialogClickListener;

    public void setIcon(int icon) {
        this.icon = icon;
    }

    private int icon;

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
        displayDialog(title, msg, "", "", false, showNegativeBtn);
    }

    public void displayDialog(String title, String msg, boolean cancelable, boolean showNegativeBtn) {
        displayDialog(title, msg, "", "", cancelable, showNegativeBtn);
    }

    public void displayDialog(String title, String msg, String btnPositiveText, String btnNegativeText, boolean cancelable, boolean showNegativeBtn) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        TextView tv_title = dialog.findViewById(R.id.tv_title);
        TextView tv_msg = dialog.findViewById(R.id.tv_msg);
        tv_title.setText(title);

        setLinks(tv_msg, msg);

        if (isGravityLeft) {
            tv_msg.setGravity(Gravity.LEFT);
        }

        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);
        if (showNegativeBtn) {
            btnNegative.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(btnPositiveText)) {
            btnPositive.setText(btnPositiveText);
        }
        if (!TextUtils.isEmpty(btnNegativeText)) {
            btnNegative.setText(btnNegativeText);
        }
        btnPositive.setOnClickListener((View v) -> {
            dialog.dismiss();
            if (null != dialogClickListener) {
                dialogClickListener.btnPositiveClick();
            }
        });
        btnNegative.setOnClickListener((View v) -> {
            dialog.dismiss();
            if (null != dialogClickListener) {
                dialogClickListener.btnNegativeClick();
            }
        });
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        try {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            // WindowManager$BadTokenException will be caught and the app would not display
            // the 'Force Close' message
            Log.d("ShowCustomDialog", "Error while showing the dialog"+e.getMessage());
        }
    }

    private void setLinks(TextView tv, String text) {
        String[] linkPatterns = {
            "([Hh][tT][tT][pP][sS]?:\\/\\/[^ ,'\">\\]\\)]*[^\\. ,'\">\\]\\)])",
            "#[\\w]+",
            "@[\\w]+"
        };

        boolean foundLink = false;
        SpannableString f = new SpannableString(text);
        for (String str : linkPatterns) {
            Pattern pattern = Pattern.compile(str);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                foundLink = true;

                int x = matcher.start();
                int y = matcher.end();

                String spanText = text.substring(x, y);
                URLSpan span = new URLSpan(spanText);
                f.setSpan(span, x, y, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(f);
            }
        }

        if (!foundLink) {
            tv.setText(text);
        }

        tv.setLinkTextColor(Color.BLUE);
        tv.setLinksClickable(true);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setFocusable(false);
    }
}
