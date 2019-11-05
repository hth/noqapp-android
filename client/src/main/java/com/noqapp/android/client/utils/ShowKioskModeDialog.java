package com.noqapp.android.client.utils;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.common.beans.JsonProfile;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

public class ShowKioskModeDialog {
    private Context context;
    private DialogClickListener dialogClickListener;

    public interface DialogClickListener {
        void btnPositiveClick(boolean isFeedBackScreen);

        void btnNegativeClick();
    }

    public ShowKioskModeDialog setDialogClickListener(DialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
        return this;
    }

    public ShowKioskModeDialog(Context context) {
        this.context = context;
    }

    public void displayDialog(String userLevel) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_kiosk_mode);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        TextView tv_info = dialog.findViewById(R.id.tv_info);
        tv_info.setText("Enabling kiosk as a " + userLevel);
        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);
        final LinearLayout ll_store_screen = dialog.findViewById(R.id.ll_store_screen);
        final LinearLayout ll_feedback_screen = dialog.findViewById(R.id.ll_feedback_screen);
        final RadioButton acrb_store_screen = dialog.findViewById(R.id.acrb_store_screen);
        final RadioButton acrb_feedback_screen = dialog.findViewById(R.id.acrb_feedback_screen);
        ll_store_screen.setOnClickListener((View v) -> {
            acrb_store_screen.setChecked(true);
            acrb_feedback_screen.setChecked(false);
        });
        ll_feedback_screen.setOnClickListener((View v) -> {
            acrb_feedback_screen.setChecked(true);
            acrb_store_screen.setChecked(false);
        });
        btnPositive.setOnClickListener((View v) -> {
            if (null != dialogClickListener) {
                dialogClickListener.btnPositiveClick(acrb_feedback_screen.isChecked());
                dialog.dismiss();
            }
        });
        btnNegative.setOnClickListener((View v) -> {
            if (null != dialogClickListener) {
                dialogClickListener.btnNegativeClick();
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public String getAssociatedCodeQR() {
        JsonProfile jsonProfile = LaunchActivity.getUserProfile();
        if (null == jsonProfile || null == jsonProfile.getBizStoreIds() || null == jsonProfile.getCodeQRs())
            return "";
        else {
            try {
                int pos = itemPos(jsonProfile.getBizNameId(), jsonProfile.getBizStoreIds());
                return jsonProfile.getCodeQRs().get(pos);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }

        }
    }

    private int itemPos(String value, List<String> temp) {
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).equals(value))
                return i;
        }
        return -1;
    }
}
