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
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.noqapp.android.client.R;
import com.noqapp.common.model.types.BusinessTypeEnum;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.common.utils.CommonHelper;
import com.noqapp.common.utils.Formatter;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AppUtilities extends CommonHelper{
    private static final String TAG = AppUtilities.class.getSimpleName();
    private static Map<String, Locale> localeMap;

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

        return String.valueOf(calculateDistance(lat1, lng1, lat2, lng2)) + " km";// distance in km
    }

    public static double calculateDistance(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return round(dist / 1000);// distance in km
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

    public static String getAdditionalCardText(BizStoreElastic bizStoreElastic) {
        StoreHourElastic storeHourElastic = bizStoreElastic.getStoreHourElasticList().get(getDayOfWeek());
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

        StoreHourElastic storeHourElastic = bizStoreElastic.getStoreHourElasticList().get(getDayOfWeek());
        String additionalText;
        if (storeHourElastic.isDayClosed()) {
            additionalText = "Closed";
        } else {
            additionalText = "Open";
        }
        return additionalText;
    }

    public static String getStoreOpenStatus(JsonTokenAndQueue jsonTokenAndQueue) {

        String additionalText;
        DateFormat df = new SimpleDateFormat("HH:mm");
        String time = df.format(Calendar.getInstance().getTime());
        int timedata = Integer.valueOf(time.replace(":", ""));
        if (jsonTokenAndQueue.getStartHour() <= timedata &&
                timedata <= jsonTokenAndQueue.getEndHour()) {
            additionalText = "Open";
        } else {
            additionalText = "Closed";
        }
        return additionalText;
    }

    public static int getDayOfWeek() {
        Calendar mondayFirst = new GregorianCalendar();
        return mondayFirst.getFirstDayOfWeek() - 1;
    }


    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng latLng = null;
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return latLng;
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(Constants.PLACES_API_BASE + Constants.TYPE_AUTOCOMPLETE + Constants.OUT_JSON);
            sb.append("?key=" + Constants.API_KEY);
            sb.append("&components=country:" + Constants.COUNTRY_CODE);
            sb.append("&types=(regions)");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    /*
     * Method add to hide the dropdown while setting the
     * AutoCompleteTextView
     */
    public static void setAutoCompleteText(AutoCompleteTextView autoCompleteTextView, String text) {
        autoCompleteTextView.setFocusable(false);
        autoCompleteTextView.setFocusableInTouchMode(false);
        autoCompleteTextView.setText(text);
        autoCompleteTextView.setFocusable(true);
        autoCompleteTextView.setFocusableInTouchMode(true);
    }

    public static String getYearFromDate(String dateValue){
        try {
            DateFormat sdf = Formatter.formatRFC822;
            SimpleDateFormat month_date = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
            Date date = sdf.parse(dateValue);
            String month_year = month_date.format(date);
            return month_year;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}

