package com.noqapp.android.common.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.noqapp.android.common.R;
import com.noqapp.android.common.customviews.CustomToast;

import java.util.Calendar;
import java.util.Date;

public class CustomCalendar {
    private Activity context;
    private DateSelectListener dateSelectListener;
    private boolean hideMsg = false;

    public interface DateSelectListener {
        void calendarDate(String date);
    }

    public CustomCalendar(Activity context) {
        this.context = context;
    }
    public CustomCalendar(Activity context, boolean hideMsg) {
        this.context = context;
        this.hideMsg = hideMsg;
    }

    public CustomCalendar setDateSelectListener(DateSelectListener dateSelectListener) {
        this.dateSelectListener = dateSelectListener;
        return this;
    }

    public void showDobCalendar() {
        View customView = context.getLayoutInflater().inflate(R.layout.custom_date_picker, null);
        final DatePicker dpDobDate = customView.findViewById(R.id.dpDobDate);
        TextView tv_date_msg = customView.findViewById(R.id.tv_date_msg);
        tv_date_msg.setVisibility(hideMsg?View.GONE:View.VISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);
        builder.setTitle("");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(dpDobDate.getYear(), dpDobDate.getMonth(), dpDobDate.getDayOfMonth());
                Date current = newDate.getTime();
                int date_diff = new Date().compareTo(current);
                if (date_diff < 0) {
                    new CustomToast().showToast(context, context.getString(R.string.error_invalid_date));
                    if (null != dateSelectListener)
                        dateSelectListener.calendarDate("");
                } else {
                    if (null != dateSelectListener)
                        dateSelectListener.calendarDate(CommonHelper.SDF_DOB_FROM_UI.format(newDate.getTime()));
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
        try {
            dpDobDate.getTouchables().get(0).performClick();
        }catch(Exception e){
            Log.e("date picker","view not created yet");
        }
    }
}
