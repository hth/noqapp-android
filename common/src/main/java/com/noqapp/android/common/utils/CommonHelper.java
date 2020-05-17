package com.noqapp.android.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.category.GroceryEnum;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;
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
    public static final String CURRENCY_SYMBOL = "currencySymbol";
    private static SimpleDateFormat MMM_YYYY = new SimpleDateFormat("MMM yyyy", Locale.getDefault());

    public static String convertDOBToValidFormat(String dob) {
        try {
            Date date = SDF_DOB_FROM_UI.parse(dob);
            return SDF_YYYY_MM_DD.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Failed parsing DOB=" + dob + " reason=" + e.getLocalizedMessage(), e);
            FirebaseCrashlytics.getInstance().log("Failed parsing DOB " + dob);
            return "";
        }
    }

    public static void openPlayStore(Context context) {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        openPlayStore(context, appPackageName);
    }


    public static void openPlayStore(Context context, String appPackageName) {
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

    public static void hideKeyBoard(Activity activity) {
        if (null != activity) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String calculateAge(String dob) {
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
            FirebaseCrashlytics.getInstance().log("Failed calculate age " + dob);
            e.printStackTrace();
        }
        return age;
    }

    public static String getCurrencySymbol(String countryCode) {
        try {
            Locale localeTemp = new Locale("", countryCode);
            return Currency.getInstance(localeTemp).getSymbol(localeTemp);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log("Failed get currency from country code " + countryCode);
            e.printStackTrace();
            return "";
        }
    }

    public static String formatStringDate(SimpleDateFormat simpleDateFormat, String date) {
        String formattedDate = "";
        if (TextUtils.isEmpty(date)) {
            return formattedDate;
        } else {
            try {
                formattedDate = simpleDateFormat.format(new SimpleDateFormat(ISO8601_FMT, Locale.getDefault()).parse(date));
            } catch (ParseException e) {
                FirebaseCrashlytics.getInstance().log("Failed parse date " + date);
                e.printStackTrace();
            }
            return formattedDate;
        }
    }

    public static Date stringToDate(String date) {
        try {
            return new SimpleDateFormat(CommonHelper.ISO8601_FMT, Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            FirebaseCrashlytics.getInstance().log("Failed parse date " + date);
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
        if (StringUtils.isNotBlank(town)) {
            address = town;
        }
        if (StringUtils.isNotBlank(area) && StringUtils.isNotBlank(address)) {
            address = area + ", " + address;
        } else if (StringUtils.isNotBlank(area)) {
            address = area;
        }
        return address;
    }

    public static String displayPrice(String number) {
        return new BigDecimal(number).movePointLeft(2).toString();
    }

    public static String displayPrice(int number) {
        return displayPrice(String.valueOf(number));
    }

    public static String transactionForDisplayOnly(String transactionId) {
        return transactionId.substring(transactionId.substring(0, transactionId.lastIndexOf("-")).lastIndexOf("-") + 1).toUpperCase();
    }

    public static String getPriceWithUnits(JsonStoreProduct jsonStoreProduct) {
        try {
            if (null != jsonStoreProduct) {
                Log.d(TAG, jsonStoreProduct.toString());
                return jsonStoreProduct.getDisplayPrice()
                        + " / "
                        + jsonStoreProduct.getDisplayUnitValue()
                        + " "
                        + (jsonStoreProduct.getUnitOfMeasurement() == null ? "" : jsonStoreProduct.getUnitOfMeasurement().getDescription());
            } else {
                return "";
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log("Failed displaying price jsonStoreProduct " + jsonStoreProduct);
            Log.e(TAG, "jsonStoreProduct " + e.getLocalizedMessage(), e);
            return "";
        }
    }

    public static double round(float value) {
        int scale = (int) Math.pow(10, 2);
        return (double) Math.round(value * scale) / scale;
    }

    public static String earlierDayAsDateFormat(int days) {
        LocalDate localDate = LocalDate.now().minusDays(days);
        return localDate.toString();
    }

    public static String todayAsDateFormat() {
        Calendar cal = Calendar.getInstance();
        return dateFormatAsYYYY_MM_DD(cal);
    }

    public static String tomorrowAsDateFormat() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return dateFormatAsYYYY_MM_DD(cal);
    }

    public static String dateFormatAsYYYY_MM_DD(Calendar cal) {
        String output = SDF_YYYY_MM_DD.format(cal.getTime());
        Log.i("Supplied date ", output);
        return output;
    }

    public static int getDayOfWeek(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        return dayOfWeek;
    }

    public static String getTimeFourDigitWithColon(int time) {
        String str = String.valueOf(time);
        String input = String.format("%4s", str).replace(' ', '0');
        int index = 1;
        //  Log.e("Check string----- ", input + "----------- " + input.substring(0, index + 1) + ":" + input.substring(index + 1));
        return input.substring(0, index + 1) + ":" + input.substring(index + 1);
    }

    public static ArrayList<String> getTimeSlots(int slotMinute, String strFromTime, String strToTime, boolean isEqual) {
        ArrayList<String> timeSlot = new ArrayList<>();
        if (slotMinute == 0) {
            return timeSlot;
        } else {
            try {
                int fromHour, fromMinute, toHour, toMinute;
                fromHour = Integer.parseInt(strFromTime.split(":")[0]);
                fromMinute = Integer.parseInt(strFromTime.split(":")[1]);

                toHour = Integer.parseInt(strToTime.split(":")[0]);
                toMinute = Integer.parseInt(strToTime.split(":")[1]);

                long slot = slotMinute * 60 * 1000;
                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.HOUR_OF_DAY, fromHour);
                calendar2.set(Calendar.MINUTE, fromMinute);

                long currentTime = calendar2.getTimeInMillis();
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, toHour);
                calendar1.set(Calendar.MINUTE, toMinute);
                long endTime = calendar1.getTimeInMillis();
                if (isEqual) {
                    while (currentTime <= endTime) {
                        DateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        timeSlot.add(sdfTime.format(new Date(currentTime)));
                        currentTime = currentTime + slot;
                    }
                } else {
                    while (currentTime < endTime) {
                        DateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        timeSlot.add(sdfTime.format(new Date(currentTime)));
                        currentTime = currentTime + slot;
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log("Failed time slot " + slotMinute + ":" + strFromTime + ":" + strToTime);
                e.printStackTrace();
            }
            return timeSlot;
        }
    }

    public static int removeColon(String input) {
        try {
            return input.contains(":") ? Integer.parseInt(input.replace(":", "")) : Integer.parseInt(input);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log("Failed removeColon " + input);
            e.printStackTrace();
            return 0;
        }
    }

    public static String randomStringGenerator(int length) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public static boolean isDateBeforeToday(Context context, String selectedDay) {
        try {
            Date selectedDate = CommonHelper.SDF_DOB_FROM_UI.parse(selectedDay);
            int date_diff = new Date().compareTo(selectedDate);
            if (date_diff >= 0) {
                return true;
            } else {
                new CustomToast().showToast(context, "Future date not allowed");
                return false;
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log("Failed isDateBeforeToday " + selectedDay);
            e.printStackTrace();
        }
        return true;
    }

    public static int getDrawableFromString(String val, Context context) {
        return context.getResources().getIdentifier(val, "drawable", context.getPackageName());
    }


    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }
        return isInBackground;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    public static void showViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.GONE);
        }
    }


    public static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (isIPv4) {
                            return sAddr;
                        } else {
                            int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                            return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }

    public static android.location.Location getLocation(double lat, double lng){
        android.location.Location location = new android.location.Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
        return location;
    }

    public static JsonStoreCategory getSystemCategory(String categoryId) {
        String description = GroceryEnum.findByName(categoryId);
        return new JsonStoreCategory().setCategoryId(categoryId).setCategoryName(description);
    }

    public static List<JsonStoreCategory> populateWithAllCategories(BusinessTypeEnum businessType) {
        List<JsonStoreCategory> jsonStoreCategories = new ArrayList<>();
        switch (businessType) {
            case RS:
            case FT:
            case BA:
            case ST:
            case CF:
                return jsonStoreCategories;
            case GS:
                GroceryEnum[] values = GroceryEnum.values();
                for (GroceryEnum groceryEnum : values) {
                    jsonStoreCategories.add(new JsonStoreCategory().setCategoryId(groceryEnum.getName()).setCategoryName(groceryEnum.getDescription()));
                }
                return jsonStoreCategories;
            default:
                FirebaseCrashlytics.getInstance().log("Failed get categories for businessType " + businessType);
                Log.e(TAG, "Reached un-supported condition" + businessType);
                return jsonStoreCategories;
        }
    }

    public int getFirstVisibleGroup(ExpandableListView expandableListView) {
        int firstVis = expandableListView.getFirstVisiblePosition();
        long packedPosition = expandableListView.getExpandableListPosition(firstVis);
        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        return groupPosition;
    }

    /**
     * Method to make the first letter CAP of each word of given string
     * */
    public static String capitalizeEachWordFirstLetter(String input){
        return WordUtils.capitalizeFully(input);
    }
}