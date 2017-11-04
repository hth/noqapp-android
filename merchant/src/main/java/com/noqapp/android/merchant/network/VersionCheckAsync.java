package com.noqapp.android.merchant.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ShowAlertInformation;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by chandra on 11/4/17.
 */

public class VersionCheckAsync extends AsyncTask<String, String, String> {

    private  Context context;
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
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newVersion;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            String currentVersion = Constants.appVersion();
            if(Integer.parseInt(currentVersion)<Integer.parseInt(newVersion.replace(".","")))
                ShowAlertInformation.showThemePlayStoreDialog(context,context.getString(R.string.playstore_update_title),context.getString(R.string.playstore_update_msg),true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}