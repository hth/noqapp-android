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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.BusinessTypeEnum;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.views.activities.LaunchActivity;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    public static double round(float value) {
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round(value * scale) / scale;
    }


    public static void setStoreDrawable(Context context, ImageView iv, BusinessTypeEnum bussinessType, TextView tv_store_rating) {
        switch (bussinessType) {
            case DO:
                iv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hospital));
                iv.setColorFilter(context.getResources().getColor(R.color.bussiness_hospital));
                tv_store_rating.setBackground(ContextCompat.getDrawable(context, R.drawable.round_corner_hospital));
                break;
            case BK:
                iv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bank));
                iv.setColorFilter(context.getResources().getColor(R.color.bussiness_bank));
                tv_store_rating.setBackground(ContextCompat.getDrawable(context, R.drawable.round_corner_bank));
                break;
            default:
                iv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.store));
                iv.setColorFilter(context.getResources().getColor(R.color.bussiness_store));
                tv_store_rating.setBackground(ContextCompat.getDrawable(context, R.drawable.round_corner_store));
        }
    }

    /**
     * Calculate distance between two points in latitude and longitude. Uses Haversine
     * method as its base.
     * <p>
     * lat1, lng1 Start point lat2, lng2
     *
     * @returns Distance in KMeters
     */
    public static String calculateDistanceInKm(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return String.valueOf(round(dist / 1000)) + " km";// distance in km
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
                LaunchActivity.locale = new Locale("hi");;
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

    public static String getAdditionalCardText(BizStoreElastic bizStoreElastic) {
        StoreHourElastic storeHourElastic = bizStoreElastic.getStoreHourElasticList().get(getTodayDay());
        String additionalText;
        if (storeHourElastic.isDayClosed()) {
            //Fetch for tomorrow when closed
            additionalText = bizStoreElastic.getDisplayName() + " is closed today.";
        } else if (getTimeIn24HourFormat() >= storeHourElastic.getStartHour() && getTimeIn24HourFormat() < storeHourElastic.getEndHour()) {
            //Based on location let them know in how much time they will reach or suggest the next queue.
            additionalText = bizStoreElastic.getDisplayName()
                    + " is open & can service you now. Click to join the queue.";
        } else {
            if (getTimeIn24HourFormat() >= storeHourElastic.getTokenAvailableFrom()) {
                additionalText = bizStoreElastic.getDisplayName()
                        + " opens at "
                        + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour())
                        + ". Join queue now to save time.";
            } else {
                additionalText = bizStoreElastic.getDisplayName()
                        + " can service you at "
                        + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour())
                        + ". You can join this queue at "
                        + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom());
            }
        }
        return additionalText;
    }
    public static String getStoreOpenStatus(BizStoreElastic bizStoreElastic) {

        StoreHourElastic storeHourElastic = bizStoreElastic.getStoreHourElasticList().get(getTodayDay());
        String additionalText;
        if (storeHourElastic.isDayClosed()) {
            additionalText = "Closed";
        } else {
            additionalText =  "Open";
        }
        return additionalText;
    }
    public static String getStoreOpenStatus(JsonTokenAndQueue jsonTokenAndQueue) {

        String additionalText;
        DateFormat df = new SimpleDateFormat("HH:mm");
        String time = df.format(Calendar.getInstance().getTime());
        int timedata = Integer.valueOf(time.replace(":",""));
        if(jsonTokenAndQueue.getStartHour()<= timedata&&
                timedata <=jsonTokenAndQueue.getEndHour()){
            additionalText =  "Open";
        }else{
            additionalText = "Closed";
        }
        return additionalText;
    }

    public static int getTodayDay(){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date dateobj = new Date();
        System.out.println(df.format(dateobj));
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return  c.get(Calendar.DAY_OF_WEEK)-1;
    }
}

