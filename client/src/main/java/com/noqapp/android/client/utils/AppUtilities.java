package com.noqapp.android.client.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueue;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AppUtilities {
    private static final String TAG = AppUtilities.class.getSimpleName();

    private static final SimpleDateFormat SDF_DOB_FROM_UI = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private static final SimpleDateFormat SDF_DOB = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static Map<String, Locale> localeMap;

    public static String convertDOBToValidFormat(String dob) {
        try {
            Date date = SDF_DOB_FROM_UI.parse(dob);
            return SDF_DOB.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing DOB={}" + e.getLocalizedMessage(), e);
            return "";
        }
    }

    public static String iso3CountryCodeToIso2CountryCode(String iso3CountryCode) {
        if (null == localeMap) {
            String[] countries = Locale.getISOCountries();
            localeMap = new HashMap<>(countries.length);
            for (String country : countries) {
                Locale locale = new Locale("", country);
                localeMap.put(locale.getISO3Country().toUpperCase(), locale);
            }
        }
        return localeMap.get(iso3CountryCode).getCountry();
    }

    public static String iso2CountryCodeToIso3CountryCode(String iso2CountryCode) {
        Locale locale = new Locale("", iso2CountryCode);
        return locale.getISO3Country();
    }

    public static void makeCall(Activity context, String phoneNumber) {
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

    public static void openAddressInMap(Activity context, String address) {
        try {
            String map = "http://maps.google.co.in/maps?q=" + address;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG, "Failed opening address reason=" + ex.getLocalizedMessage());
            Toast.makeText(context, "Please install a maps application", Toast.LENGTH_LONG).show();
        }
    }

    public static void exportDatabase(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + "noqueue.db" + "";
                String backupDBPath = System.currentTimeMillis() + "-noQueue.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }

    public void hideKeyBoard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    static void setRatingStarColor(Drawable drawable, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable, color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public static void setRatingBarColor(LayerDrawable stars, Context context) {

        // Filled stars
        setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(context, R.color.rating_select));
        // Half filled stars
        setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(context, R.color.rating_unselect));
        // Empty stars
        setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(context, R.color.rating_unselect));
    }

    static void openPlayStore(Context context) {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static int getTimeIn24HourFormat() {
        // To make sure minute in time 11:06 AM is not represented as 116 but as 1106.
        LocalDateTime localDateTime = LocalDateTime.now();
        int time = Integer.parseInt(String.valueOf(localDateTime.getHourOfDay()) + String.format(Locale.US, "%02d", localDateTime.getMinuteOfHour()));
        Log.i(TAG, "System Time " + time);
        return time;
    }

    //TODO(chandra) this logic goes in merchant too
    public static void sortJsonQueues(final int systemHourMinutes, List<JsonQueue> jsonQueues) {
        List<JsonQueue> closedTodayQueues = new ArrayList<>();
        List<JsonQueue> afterNowClosedQueues = new ArrayList<>();
        for (JsonQueue jsonQueue : jsonQueues) {
            if (jsonQueue.isDayClosed()) {
                closedTodayQueues.add(jsonQueue);
            } else if (jsonQueue.getEndHour() < systemHourMinutes) {
                afterNowClosedQueues.add(jsonQueue);
            }
        }

        jsonQueues.removeAll(afterNowClosedQueues);
        jsonQueues.removeAll(closedTodayQueues);
        Comparator<JsonQueue> openNow = new Comparator<JsonQueue>() {
            @Override
            public int compare(JsonQueue o1, JsonQueue o2) {
                return systemHourMinutes - o1.getStartHour() < systemHourMinutes - o2.getStartHour() ? 1 : -1;
            }
        };

        Collections.sort(jsonQueues, openNow);
        jsonQueues.addAll(afterNowClosedQueues);
        jsonQueues.addAll(closedTodayQueues);

        for (JsonQueue jsonQueue : jsonQueues) {
            System.out.println(jsonQueue.getDisplayName());
        }
    }

    public static double round (float value) {
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round(value * scale) / scale;
    }
}

