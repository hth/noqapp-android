package com.noqapp.android.client.utils;

import com.noqapp.android.client.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class RateTheAppManager {

    private final int DAYS_UNTIL_PROMPT = 3;//Min number of days
    private final int LAUNCHES_UNTIL_PROMPT = 5;//Min number of launches

    public void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("ratetheapp", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.apply();
    }

    public void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rate_app_prompt, null);
        TextView tv_rate_app = dialogView.findViewById(R.id.tv_rate_app);
        tv_rate_app.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName())));
                dialog.dismiss();
            }
        });
        TextView tv_remind_later = dialogView.findViewById(R.id.tv_remind_later);
        tv_remind_later.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView tv_no_thanks = dialogView.findViewById(R.id.tv_no_thanks);
        tv_no_thanks.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }
}
