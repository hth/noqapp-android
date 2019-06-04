package com.noqapp.android.common.customviews;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.common.R;

public class CustomToast {

    public void showToast(Context context, String msg) {
        getToast(context,msg).show();
    }

    public Toast getToast(Context context, String msg) {
        View layout = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        TextView toastMessage = layout.findViewById(R.id.tv_toast_msg);
        toastMessage.setText(msg);
        toastMessage.setTextSize(18);
        toastMessage.setTextColor(Color.WHITE);
        toastMessage.setGravity(Gravity.CENTER);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
       return toast;
    }
}
