package com.noqapp.android.merchant.network;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ShowAlertInformation;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;

import org.jsoup.Jsoup;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Created by chandra on 11/4/17.
 */
public class VersionCheckAsync extends AsyncTask<String, String, String> {
    private final static String TAG = VersionCheckAsync.class.getSimpleName();

    private Context context;
    private String newVersion;

    public VersionCheckAsync(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div.hAyfc:nth-child(5) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                    .first()
                    .ownText();
        } catch (Exception e) {
            Log.e(TAG, "Random error during version check reason=" + e.getLocalizedMessage(), e);
            Answers.getInstance().logCustom(new CustomEvent("Version check failed:Merchant "+context.getPackageName())
                    .putCustomAttribute("Jsoup received version", newVersion));
        }

        return newVersion;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            String currentVersion = Constants.appVersion();
            if (Integer.parseInt(currentVersion.replace(".", "")) < Integer.parseInt(newVersion.replace(".", ""))) {
                ShowAlertInformation.showThemePlayStoreDialog(
                        context,
                        context.getString(R.string.playstore_update_title),
                        context.getString(R.string.playstore_update_msg),
                        true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Compare version check reason=" + e.getLocalizedMessage(), e);
            Answers.getInstance().logCustom(new CustomEvent("Version check failed:Merchant "+context.getPackageName())
                    .putCustomAttribute("Jsoup received version", newVersion));
        }
    }
}