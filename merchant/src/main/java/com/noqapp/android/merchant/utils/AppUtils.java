package com.noqapp.android.merchant.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.noqapp.android.common.beans.JsonNameDatePair;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import java.util.List;
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
            apkVersionModel = new ApkVersionModel(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        } else if (split.length == 3) {
            apkVersionModel = new ApkVersionModel(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }
        return apkVersionModel;
    }

    public boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void makeCall(Activity context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                context.startActivity(callIntent);
            } catch (ActivityNotFoundException ex) {
                Log.w(TAG, "Failed calling reason=" + ex.getLocalizedMessage());
                Toast.makeText(context, "Please install a calling application", Toast.LENGTH_LONG).show();
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

    public static void authenticationProcessing() {
        LaunchActivity.getLaunchActivity().clearLoginData(true);
    }

    public String getCompleteEducation(List<JsonNameDatePair> education) {
        if (null == education || education.size() == 0)
            return "";
        else {
            String edu = "";
            for (int i = 0; i < education.size(); i++) {
                edu += education.get(i).getName() + ", ";
            }
            if (edu.endsWith(", ")) {
                edu = edu.substring(0, edu.length() - 2);
            }
            return edu;
        }
    }

    public String hidePhoneNumberWithX(String phoneNo) {
        if (null != phoneNo && phoneNo.length() >= 10) {
            return phoneNo.substring(0, 4) + "XXXXX" + phoneNo.substring(phoneNo.length() - 3, phoneNo.length());
        } else {
            return "";
        }
    }

    public static String getImageUrls(String bucket_type, String url) {
        String location;
        switch (bucket_type) {
            case BuildConfig.PROFILE_BUCKET:
                location = BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + url;
                break;
            case BuildConfig.SERVICE_BUCKET:
                location = BuildConfig.AWSS3 + BuildConfig.SERVICE_BUCKET + url;
                break;
            default:
                Log.e("App Utils", "Un-supported bucketType=" + bucket_type);
                throw new UnsupportedOperationException("Reached unsupported condition");
        }
        return location;
    }

    public static boolean isRelease() {
        return BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constants.RELEASE);
    }
}
