package com.noqapp.android.merchant.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;

/**
 * User: hitender
 * Date: 4/24/17 11:13 PM
 */

public class AppUtils {

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

    static void openPlayStore(Context context) {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
