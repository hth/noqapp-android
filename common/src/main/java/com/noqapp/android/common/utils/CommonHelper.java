package com.noqapp.android.common.utils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CommonHelper {
    private static final String TAG = CommonHelper.class.getSimpleName();

    public static final String ISO8601_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final SimpleDateFormat SDF_DOB_FROM_UI = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    public static final SimpleDateFormat SDF_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static final SimpleDateFormat SDF_YYYY_MM_DD_HH_MM_A = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
    public static final SimpleDateFormat SDF_YYYY_MM_DD_KK_MM = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.getDefault());
    public static final SimpleDateFormat SDF_DD_MMM_YY_HH_MM_A = new SimpleDateFormat("dd MMM yy, hh:mm a", Locale.getDefault());
    public static final SimpleDateFormat SDF_ISO8601_FMT = new SimpleDateFormat(ISO8601_FMT, Locale.getDefault());
    public static String CURRENCY_SYMBOL = "currencySymbol";

    private static SimpleDateFormat MMM_YYYY = new SimpleDateFormat("MMM yyyy", Locale.getDefault());

    public static String convertDOBToValidFormat(String dob) {
        try {
            Date date = SDF_DOB_FROM_UI.parse(dob);
            return SDF_YYYY_MM_DD.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Failed parsing DOB=" + dob + " reason=" + e.getLocalizedMessage(), e);
            return "";
        }
    }

    public static void openPlayStore(Context context) {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void shareTheApp(Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + context.getPackageName());
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    /*
     * Method add to hide the dropdown while setting the
     * AutoCompleteTextView
     */
    public static void setAutoCompleteText(AutoCompleteTextView autoCompleteTextView, String text) {
        autoCompleteTextView.setFocusable(false);
        autoCompleteTextView.setFocusableInTouchMode(false);
        autoCompleteTextView.setText(text);
        autoCompleteTextView.setSelection(text.length()); // to make the cursor at end of the text
        autoCompleteTextView.setFocusable(true);
        autoCompleteTextView.setFocusableInTouchMode(true);
        autoCompleteTextView.dismissDropDown();
    }

    public static String getYearFromDate(String dateValue) {
        try {
            DateFormat sdf = new SimpleDateFormat("YYYY-MM-DD", Locale.getDefault());
            Date date = sdf.parse(dateValue);
            return MMM_YYYY.format(date);
        } catch (ParseException | IllegalArgumentException e) {
            Log.e(TAG, "Failed parsing dateValue=" + dateValue + " reason=" + e.getLocalizedMessage(), e);
            return "";
        }
    }

    public void hideKeyBoard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public String calculateAge(String dob) {
        String age = "";
        try {
            DateTime dateTime = new DateTime(CommonHelper.SDF_YYYY_MM_DD.parse(dob));
            DateTime now = DateTime.now();
            int years = Years.yearsBetween(dateTime, now).getYears();

            if (years <= 1) {
                int months = Months.monthsBetween(dateTime, now).getMonths();
                if (months <= 1) {
                    int days = Days.daysBetween(dateTime, now).getDays();
                    if (days == 0) {
                        age = "Today";
                    } else {
                        age = days + "+ days";
                    }
                } else {
                    age = months + "+ months";
                }
            } else {
                age = years + "+ years";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return age;
    }

    public static String getCurrencySymbol(String countryCode) {
        try {
            Locale localeTemp = new Locale("", countryCode);
            return Currency.getInstance(localeTemp).getSymbol(localeTemp);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatStringDate(SimpleDateFormat simpleDateFormat, String date) {
        String formattedDate = "";
        try {
            formattedDate = simpleDateFormat.format(new SimpleDateFormat(ISO8601_FMT, Locale.getDefault()).parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static Date stringToDate(String date) {
        try {
            return new SimpleDateFormat(CommonHelper.ISO8601_FMT, Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date();
    }

    public static String changeUTCDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(CommonHelper.ISO8601_FMT, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    public static String getStoreAddress(String town, String area) {
        String address = "";
        if (!TextUtils.isEmpty(town)) {
            address = town;
        }
        if (!TextUtils.isEmpty(area)) {
            address = area + ", " + address;
        }
        return address;
    }

    public static String displayPrice(String number) {
        return new BigDecimal(number).scaleByPowerOfTen(-2).toString();
    }

    public static String displayPrice(int number) {
        return new BigDecimal(number).scaleByPowerOfTen(-2).toString();
    }

    public static String transactionForDisplayOnly(String transactionId) {
        return transactionId.substring(transactionId.substring(0, transactionId.lastIndexOf("-")).lastIndexOf("-") + 1);
    }
}
