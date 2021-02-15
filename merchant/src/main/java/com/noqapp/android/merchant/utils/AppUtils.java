package com.noqapp.android.merchant.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.noqapp.android.common.beans.JsonHour;
import com.noqapp.android.common.beans.JsonNameDatePair;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * User: hitender
 * Date: 4/24/17 11:13 PM
 */

public class AppUtils extends CommonHelper {

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


    public static void makeCall(Activity context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                context.startActivity(callIntent);
            } catch (ActivityNotFoundException ex) {
                Log.w(AppUtils.class.getSimpleName(), "Failed calling reason=" + ex.getLocalizedMessage());
                new CustomToast().showToast(context, "Please install a calling application");
            }
        }
    }

    public static void changeLanguage(String language) {
        if (!language.equals("")) {
            if (language.equals("en")) {
                LaunchActivity.language = "en_US";
                LaunchActivity.locale = Locale.ENGLISH;
                LaunchActivity.languagepref.edit().putString("pref_language", "en").apply();
            } else {
                LaunchActivity.language = "hi";
                LaunchActivity.locale = new Locale("hi");
                LaunchActivity.languagepref.edit().putString("pref_language", "hi").apply();
            }
        } else {
            LaunchActivity.language = "en_US";
            LaunchActivity.locale = Locale.ENGLISH;
            LaunchActivity.languagepref.edit().putString("pref_language", "en").apply();
        }

    }

    public static void authenticationProcessing() {
        LaunchActivity.getLaunchActivity().clearLoginData(true);
    }

    public static String getCompleteEducation(List<JsonNameDatePair> education) {
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

    public static String hidePhoneNumberWithX(String phoneNo) {
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
            case BuildConfig.ACCREDITATION_BUCKET:
                location = BuildConfig.AWSS3 + BuildConfig.ACCREDITATION_BUCKET + url;
                break;
            case BuildConfig.ADVERTISEMENT_BUCKET:
                location = BuildConfig.AWSS3 + BuildConfig.ADVERTISEMENT_BUCKET + url;
                break;
            default:
                Log.e(AppUtils.class.getSimpleName(), "Un-supported bucketType=" + bucket_type);
                throw new UnsupportedOperationException("Reached unsupported condition");
        }
        return location;
    }

    public static boolean isRelease() {
        return BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constants.RELEASE);
    }

    public static void setRatingBarColor(LayerDrawable stars, Context context) {
        // Filled stars
        setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(context, R.color.rating_select));
        // Half filled stars
        setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(context, R.color.rating_unselect));
        // Empty stars
        setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(context, R.color.rating_unselect));
    }

    private static void setRatingStarColor(Drawable drawable, @ColorInt int color) {
        DrawableCompat.setTint(drawable, color);
    }

    public boolean checkStoreClosedWithTime(JsonHour jsonHour) {
        if ((jsonHour.getStartHour() == 0 && jsonHour.getEndHour() == 0)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkStoreClosedWithAppointmentTime(JsonHour jsonHour) {
        if ((jsonHour.getAppointmentStartHour() == 0 && jsonHour.getAppointmentEndHour() == 0)) {
            return true;
        } else {
            return false;
        }
    }


    public static ArrayList<String> getMonths() {
        ArrayList<String> monthList = new ArrayList<>();
        monthList.add("Select month");
        monthList.add("January");
        monthList.add("February");
        monthList.add("March");
        monthList.add("April");
        monthList.add("May");
        monthList.add("June");
        monthList.add("July");
        monthList.add("August");
        monthList.add("September");
        monthList.add("October");
        monthList.add("November");
        monthList.add("December");
        return monthList;
    }

    public static ArrayList<String> getYearsTillNow() {
        ArrayList<String> yearList = new ArrayList<>();
        yearList.add("Select year");
        int startYear = 2018;
        int endYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = startYear; i <= endYear; i++) {
            yearList.add(String.valueOf(i));
        }
        Log.e("YearList : ", yearList.toString());
        return yearList;
    }

    public static String createAndParseDate(String month, String year) {
        try {
            DateFormat fmt = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            Date date = fmt.parse(month + " 01, " + year);
            return CommonHelper.SDF_YYYY_MM_DD.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
