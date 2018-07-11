package com.noqapp.android.merchant.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.common.utils.CommonHelper;

import java.util.Locale;

/**
 * User: hitender
 * Date: 4/24/17 11:13 PM
 */

public class AppUtils extends CommonHelper {
    private final String TAG = AppUtils.class.getSimpleName();

    public static ApkVersionModel parseVersion(String version) {
        if (null == version || !version.contains(".")) {
            return null;
        }

        String[] split = version.split("\\.");
        ApkVersionModel apkVersionModel = null;
        if (split.length == 4) {
            apkVersionModel = new ApkVersionModel(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer.valueOf(split[3]));
        } else if (split.length == 3) {
            apkVersionModel = new ApkVersionModel(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]));
        }
        return apkVersionModel;
    }

    public boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    //TODO(chandra) conditioned to call when permission available
    public void makeCall(Activity context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            int checkPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        context,
                        new String[]{Manifest.permission.CALL_PHONE},
                        111);
            } else {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    context.startActivity(callIntent);
                } catch (ActivityNotFoundException ex) {
                    Log.w(TAG, "Failed calling reason=" + ex.getLocalizedMessage());
                    Toast.makeText(context, "Please install a calling application", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public static void changeLanguage(String language) {

        if (!language.equals("")) {
            if (language.equals("en")) {
                LaunchActivity.language = "en_US";
                LaunchActivity.locale = Locale.ENGLISH;
                LaunchActivity.languagepref.edit()
                        .putString("pref_language", "en").apply();
            } else {
                LaunchActivity.language = "hi";
                LaunchActivity.locale = new Locale("hi");
                ;
                LaunchActivity.languagepref.edit()
                        .putString("pref_language", "hi").apply();
            }
        } else {
            LaunchActivity.language = "en_US";
            LaunchActivity.locale = Locale.ENGLISH;
            LaunchActivity.languagepref.edit()
                    .putString("pref_language", "en").apply();
        }

    }
}
