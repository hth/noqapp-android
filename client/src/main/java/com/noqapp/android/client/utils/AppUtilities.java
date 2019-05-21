package com.noqapp.android.client.utils;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;
import com.noqapp.android.common.beans.JsonAdvertisement;
import com.noqapp.android.common.beans.JsonHour;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;

import com.google.android.gms.maps.model.LatLng;

import com.squareup.picasso.Picasso;

import org.joda.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class AppUtilities extends CommonHelper {
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


    private static void setRatingStarColor(Drawable drawable, @ColorInt int color) {
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
        StoreHourElastic storeHourElastic = getStoreHourElastic(bizStoreElastic.getStoreHourElasticList());
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
        StoreHourElastic storeHourElastic = getStoreHourElastic(bizStoreElastic.getStoreHourElasticList());
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
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
        String time = df.format(Calendar.getInstance().getTime());
        int timeData = Integer.parseInt(time.replace(":", ""));
        if (jsonTokenAndQueue.getStartHour() <= timeData && timeData <= jsonTokenAndQueue.getEndHour()) {
            additionalText = "Open";
        } else {
            additionalText = "Closed";
        }
        return additionalText;
    }

    private static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        return dayOfWeek;
    }

    public static JsonHour getJsonHour(List<JsonHour> jsonHourList) {
        if (null != jsonHourList && jsonHourList.size() > 0) {
            int todayDay = getDayOfWeek();
            for (int i = 0; i < jsonHourList.size(); i++) {
                if (jsonHourList.get(i).getDayOfWeek() == todayDay) {
                    return jsonHourList.get(i);
                }
            }
        }
        return null;
    }

    public static StoreHourElastic getStoreHourElastic(List<StoreHourElastic> jsonHourList) {
        if (null != jsonHourList && jsonHourList.size() > 0) {
            int todayDay = getDayOfWeek();
            for (int i = 0; i < jsonHourList.size(); i++) {
                if (jsonHourList.get(i).getDayOfWeek() == todayDay) {
                    return jsonHourList.get(i);
                }
            }
        }
        return null;
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
                //TODO @Chandra get proper lat long
            }
            Address location = address.get(0);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return latLng;
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            String sb = Constants.PLACES_API_BASE + Constants.TYPE_AUTOCOMPLETE + Constants.OUT_JSON +
                    "?key=" + Constants.GOOGLE_PLACE_API_KEY +
                    "&components=country:" + Constants.COUNTRY_CODE +
                    "&types=(regions)" +
                    "&input=" + URLEncoder.encode(input, "utf8");
            URL url = new URL(sb);

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
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return null;
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
            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }

    public static String getNameFromQueueUserID(String queueUserID, List<JsonProfile> list) {
        String name = "";
        if (!TextUtils.isEmpty(queueUserID)) {
            if (null != list && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (queueUserID.equalsIgnoreCase(list.get(i).getQueueUserId())) {
                        name = list.get(i).getName();
                        break;
                    }
                }
            }
        }
        return name;
    }

    public static JsonProfile getJsonProfileQueueUserID(String queueUserID, List<JsonProfile> list) {
        JsonProfile name = null;
        if (!TextUtils.isEmpty(queueUserID)) {
            if (null != list && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (queueUserID.equalsIgnoreCase(list.get(i).getQueueUserId())) {
                        name = list.get(i);
                        break;
                    }
                }
            }
        }
        return name;
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
                Log.e(TAG, "Un-supported bucketType=" + bucket_type);
                throw new UnsupportedOperationException("Reached unsupported condition");
        }
        return location;
    }

    public static void exportDatabase(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/" + context.getPackageName() + "/databases/" + "noqueue.db" + "";
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
            e.printStackTrace();
        }
    }

    public String formatWeeklyTimings(Context context, List<JsonHour> jsonHoursList) {
        String output = "";
        if (null != jsonHoursList && jsonHoursList.size() > 0) {
            HashMap<String, String> temp = new LinkedHashMap<>();
            for (int i = 0; i < jsonHoursList.size(); i++) {
                JsonHour jsonHour = jsonHoursList.get(i);
                String key;
                if (jsonHour.isDayClosed()) {
                    key = "Closed";
                } else {
                    key = Formatter.duration(jsonHour.getStartHour(), jsonHour.getEndHour());
                    if (1 == jsonHour.getStartHour() && 2359 == jsonHour.getEndHour()) {
                        key = context.getString(R.string.whole_day);
                    }
                    if (jsonHour.getStartHour() == 0 && jsonHour.getEndHour() == 0) {
                        key = "Closed";
                    }
                }
                if (null == temp.get(key)) {
                    temp.put(key, getDayName(jsonHour.getDayOfWeek()));
                } else {
                    String value = temp.get(key) + "-" + getDayName(jsonHour.getDayOfWeek());
                    temp.put(key, value);
                }
            }

            for (Map.Entry<String, String> entry : temp.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                //Log.e("mapValue: ", "Key: " + key + " , value: " + value);
                output += "<font color=\"black\"><b>" + value + "</b></font>" + ": " + key + "<br>";
            }
        }
        return output;
    }

    private String getDayName(int dayOfWeek) {
        String dayName = null;
        switch (dayOfWeek) {
            case 1:
                dayName = "Mon";
                break;
            case 2:
                dayName = "Tue";
                break;
            case 3:
                dayName = "Wed";
                break;
            case 4:
                dayName = "Thu";
                break;
            case 5:
                dayName = "Fri";
                break;
            case 6:
                dayName = "Sat";
                break;
            case 7:
                dayName = "Sun";
                break;
        }
        return dayName;
    }

    public static void authenticationProcessing(Context context) {
        NoQueueBaseActivity.clearPreferences();
        ShowAlertInformation.showAuthenticErrorDialog(context);
    }

    public String formatTodayStoreTiming(Context context, StoreHourElastic storeHourElastic) {
        if (storeHourElastic.isDayClosed())
            return "Closed";
        else
            return formatTodayStoreTiming(context, storeHourElastic.getStartHour(), storeHourElastic.getEndHour());
    }

    public String formatTodayStoreTiming(Context context, int startHour, int endHour) {
        String key = Formatter.duration(startHour, endHour);
        if (1 == startHour && 2359 == endHour) {
            key = context.getString(R.string.whole_day);
            return key;
        } else if (startHour == 0 && endHour == 0) {
            return "Closed";
        } else {
            return key;
        }
    }

    public static void shareTheApp(Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.launcher);
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Uri bitmapUri = null;
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
                file.getParentFile().mkdirs();
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                if (Build.VERSION.SDK_INT < 24) {
                    bitmapUri = Uri.fromFile(file);
                } else {
                    bitmapUri = Uri.parse(file.getPath()); // Work-around for new SDKs, doesn't work on older ones.
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmapUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I am inviting you to join our app. A simple and secure app developed by us. Check out my app at: https://play.google.com/store/apps/details?id=" + context.getPackageName());
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setType("image/*");
                context.startActivity(Intent.createChooser(shareIntent, "Share my app"));
            }
        }
    }

    public static boolean isRelease() {
        return BuildConfig.BUILD_TYPE.equalsIgnoreCase(Constants.RELEASE);
    }

    public static void loadProfilePic(ImageView iv_profile, String imageUrl, Context context) {
        Picasso.get().load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get()
                        .load(AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, imageUrl))
                        .placeholder(ImageUtils.getProfilePlaceholder(context))
                        .error(ImageUtils.getProfileErrorPlaceholder(context))
                        .into(iv_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int generateRandomColor() {
        String[] colors = new String[]{
                "#90C978", "#AFD5AA", "#83C6DD", "#5DB1D1", "#8DA290", "#BEC7B4", "#769ECB", "#9DBAD5",
                "#C8D6B9", "#8FC1A9", "#7CAA98", "#58949C", "#DF9881", "#D4B59D", "#CE9C6F", "#D3EEFF",
                "#836853", "#988270", "#4F9EC4", "#3A506B", "#606E79", "#804040", "#AF6E4D", "#567192"};

        int rnd = new Random().nextInt(colors.length);
        return Color.parseColor(colors[rnd]);
    }
}