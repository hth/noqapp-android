package com.noqapp.android.client.utils;

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
import com.noqapp.android.client.R;

public class RateTheAppManager {

    private final int DAYS_UNTIL_PROMPT = 0;//Min number of days
    private final int LAUNCHES_UNTIL_PROMPT = 5;//Min number of launches

    public void appLaunched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("ratetheapp", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launchCount = prefs.getLong("launchCount", 0) + 1;
        editor.putLong("launchCount", launchCount);

        // Get date of first launch
        Long dateFirstLaunch = prefs.getLong("dateFirstLaunch", 0);
        if (dateFirstLaunch == 0) {
            dateFirstLaunch = System.currentTimeMillis();
            editor.putLong("dateFirstLaunch", dateFirstLaunch);
        }
        // Log.e("Update", "launchCount "+launchCount+" dateFirstLaunch"+dateFirstLaunch);
        // Wait at least n days before opening
        if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= dateFirstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.apply();
    }

    private void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {

        if (AppUtils.isRelease()) {
            AnalyticsEvents.logContentEvent(AnalyticsEvents.EVENT_RATE_APP);
        }
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rate_app_prompt, null);
        TextView tv_rate_app = dialogView.findViewById(R.id.tv_rate_app);
        tv_rate_app.setOnClickListener((View v) -> {
            editor.putLong("launchCount", 0).apply();
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName())));
            dialog.dismiss();
        });
        TextView tv_remind_later = dialogView.findViewById(R.id.tv_remind_later);
        tv_remind_later.setOnClickListener((View v) -> {
            editor.putLong("launchCount", 0).apply();
            dialog.dismiss();
        });
        TextView tv_no_thanks = dialogView.findViewById(R.id.tv_no_thanks);
        tv_no_thanks.setOnClickListener((View v) -> {
            if (editor != null) {
                editor.putBoolean("dontshowagain", true);
                editor.commit();
            }
            dialog.dismiss();
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }
}
