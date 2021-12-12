package com.noqapp.android.client.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonQueueHistorical;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.views.activities.AllDayTimingActivity;
import com.noqapp.android.client.views.activities.AppInitialize;
import com.noqapp.android.client.views.version_2.HomeActivity;
import com.noqapp.android.common.beans.JsonHour;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.GeoIP;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import kotlin.jvm.functions.Function0;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.noqapp.android.common.model.types.UserLevelEnum.Q_SUPERVISOR;
import static com.noqapp.android.common.model.types.UserLevelEnum.S_MANAGER;

public class AppUtils extends CommonHelper {
    private static final String TAG = AppUtils.class.getSimpleName();

    public static void makeCall(Activity context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                context.startActivity(callIntent);
            } catch (ActivityNotFoundException ex) {
                Log.w(TAG, "Failed calling reason=" + ex.getLocalizedMessage());
                new CustomToast().showToast(context, "Please install calling application");
            }
        }
    }

    public static void openAddressInMap(Activity context, String address) {
        try {
            String map = "http://maps.google.co.in/maps?q=" + Uri.encode(address);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG, "Failed opening address reason=" + ex.getLocalizedMessage());
            new CustomToast().showToast(context, "Please install map application");
        }
    }

    public static void openNavigationInMap(Activity context, String address) {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG, "Failed opening address reason=" + ex.getLocalizedMessage());
            new CustomToast().showToast(context, "Please install map application");
        }
    }

    private static void setRatingStarColor(Drawable drawable, @ColorInt int color) {
        DrawableCompat.setTint(drawable, color);
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
        int time = Integer.parseInt(localDateTime.getHourOfDay() + String.format(Locale.US, "%02d", localDateTime.getMinuteOfHour()));
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
        Comparator<JsonQueue> openNow = (o1, o2) -> systemHourMinutes - o1.getStartHour() < systemHourMinutes - o2.getStartHour() ? 1 : -1;

        Collections.sort(jsonQueues, openNow);
        jsonQueues.addAll(afterNowClosedQueues);
        jsonQueues.addAll(closedTodayQueues);
    }

    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        if (Constants.DISTANCE_UNIT.equals("km")) {
            return round(dist / 1000);// distance in km
        } else {
            double kilometers = dist / 1000;
            double value = kilometers / 1.6;
            return Math.round(value * 100D) / 100D;// distance in miles
        }
    }

    public static String getSelectedLanguage(Context context) {
        SharedPreferences languagePref = PreferenceManager.getDefaultSharedPreferences(context);
        String language = languagePref.getString("pref_language", "");
        if (language.equals("")) {
            language = "en_US";
        }
        return language;
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

    public static JsonHour getJsonHour(List<JsonHour> jsonHourList) {
        if (null != jsonHourList && jsonHourList.size() > 0) {
            int todayDay = getDayOfWeek(Calendar.getInstance());
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
            int todayDay = getDayOfWeek(Calendar.getInstance());
            for (int i = 0; i < jsonHourList.size(); i++) {
                if (jsonHourList.get(i).getDayOfWeek() == todayDay) {
                    return jsonHourList.get(i);
                }
            }
        }
        return null;
    }

    public static GeoIP getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        GeoIP geoIP = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (null == address) {
                return null;
            }
            Address location = address.get(0);
            geoIP = new GeoIP(location.getSubLocality(), location.getSubAdminArea(), strAddress, location.getLatitude(), location.getLongitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return geoIP;
    }

    public static List<String> autoCompleteWithOkHttp(String input) {
        List<String> resultList;
        OkHttpClient client = new OkHttpClient();
        String url;
        try {
            url = Constants.PLACES_API_BASE + Constants.TYPE_AUTOCOMPLETE + Constants.OUT_JSON +
                    "?key=" + Constants.GOOGLE_PLACE_API_KEY +
                    "&components=country:" + Constants.DEFAULT_COUNTRY_CODE +
                    "&types=(regions)" +
                    "&input=" + URLEncoder.encode(input, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            /* Create a JSON object hierarchy from the results. */
            String data = response.body().string();
            JSONObject jsonObj = new JSONObject(data);
            JSONArray predictions = jsonObj.getJSONArray("predictions");
            /* Extract the Place descriptions from the results. */
            resultList = new ArrayList<>(predictions.length());
            for (int i = 0; i < predictions.length(); i++) {
                resultList.add(predictions.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Failed loading places ", e);
            e.printStackTrace();
            return null;
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
            case BuildConfig.PRODUCT_BUCKET:
                location = BuildConfig.AWSS3 + BuildConfig.PRODUCT_BUCKET + url;
                break;
            case BuildConfig.MARKETPLACE_BUCKET:
                location = BuildConfig.AWSS3 + BuildConfig.MARKETPLACE_BUCKET + url;
                break;
            default:
                Log.e(TAG, "Un-supported bucketType=" + bucket_type);
                throw new UnsupportedOperationException("Reached unsupported condition");
        }

        Log.d(TAG, "Image location " + location);
        return location;
    }


    public String formatWeeklyTimings(Context context, List<JsonHour> jsonHoursList) {
        String output = "";
        if (null != jsonHoursList && jsonHoursList.size() > 0) {
            HashMap<String, String> temp = new LinkedHashMap<>();
            for (int i = 0; i < jsonHoursList.size(); i++) {
                JsonHour jsonHour = jsonHoursList.get(i);
                String key;
                if (jsonHour.isDayClosed()) {
                    key = context.getString(R.string.closed);
                } else {
                    key = Formatter.duration(jsonHour.getStartHour(), jsonHour.getEndHour());
                    if (1 == jsonHour.getStartHour() && 2359 == jsonHour.getEndHour()) {
                        key = context.getString(R.string.whole_day);
                    }
                    if (jsonHour.getStartHour() == 0 && jsonHour.getEndHour() == 0) {
                        key = context.getString(R.string.closed);
                    }
                }
                if (null == temp.get(key)) {
                    temp.put(key, getDayName(context, jsonHour.getDayOfWeek()));
                } else {
                    String value = temp.get(key) + "-" + getDayName(context, jsonHour.getDayOfWeek());
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

    public static String getDayName(Context context, int dayOfWeek) {
        String dayName = null;
        switch (dayOfWeek) {
            case 1:
                dayName = context.getString(R.string.txt_monday);
                break;
            case 2:
                dayName = context.getString(R.string.txt_tuesday);
                break;
            case 3:
                dayName = context.getString(R.string.txt_wednesday);
                break;
            case 4:
                dayName = context.getString(R.string.txt_thursday);
                break;
            case 5:
                dayName = context.getString(R.string.txt_friday);
                break;
            case 6:
                dayName = context.getString(R.string.txt_saturday);
                break;
            case 7:
                dayName = context.getString(R.string.txt_sunday);
                break;
        }
        return dayName;
    }

    public static void authenticationProcessing(Activity activity, Function0 loginAgainAuthentication) {
        AppInitialize.clearPreferences();
        ShowAlertInformation.showAuthenticErrorDialog(activity, loginAgainAuthentication);
    }

    public String formatTodayStoreTiming(Context context, boolean isDayClosed, int startHour, int endHour) {
        if (isDayClosed)
            return "Closed";
        else
            return formatTodayStoreTiming(context, startHour, endHour);
    }

    public String formatTodayStoreTiming(Context context, int startHour, int endHour) {
        String key = Formatter.duration(startHour, endHour);
        if (1 == startHour && 2359 == endHour) {
            key = context.getString(R.string.whole_day);
            return key;
        } else if (startHour == 0 && endHour == 0) {
            return context.getString(R.string.closed);
        } else {
            return key;
        }
    }

    public String formatTodayStoreLunchTiming(Context context, int lunchTimeStart, int lunchTimeEnd) {
        if (lunchTimeStart == 0 || lunchTimeEnd == 0) {
            return "";
        } else {
            return context.getString(R.string.lunch_time) + ": " + Formatter.duration(lunchTimeStart, lunchTimeEnd);
        }
    }

    public String storeLunchTiming(int lunchTimeStart, int lunchTimeEnd) {
        if (lunchTimeStart == 0 || lunchTimeEnd == 0) {
            return "";
        } else {
            return Formatter.duration(lunchTimeStart, lunchTimeEnd);
        }
    }

    public boolean checkStoreClosedWithTime(StoreHourElastic storeHourElastic) {
        return (storeHourElastic.getStartHour() == 0 && storeHourElastic.getEndHour() == 0);
    }

    public boolean checkStoreClosedWithAppointmentTime(StoreHourElastic storeHourElastic) {
        return (storeHourElastic.getAppointmentStartHour() == 0 && storeHourElastic.getAppointmentEndHour() == 0);
    }

    public static void shareTheApp(Context context) {
        String shareMessage = "Hi, I am using a new and wonderful app, called NoQueue. " +
                "It helps keep the social distancing, avoid crowd and saves my time. Most importantly, it is real time. " +
                "Get the status update on your phone quickly and immediately. I am sending you an invite so you too " +
                "enjoy the experience and avoid standing in queues.\n\n" +
                "Download it here: https://play.google.com/store/apps/details?id=" + context.getPackageName();

        // @TODO revert the below changes when storage permission enabled in manifest (fails on Samsung)
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.launcher);
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Uri bitmapUri = null;
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "NoQueue.png");
                if (file.getParentFile().mkdirs() || file.getParentFile().exists()) {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.close();
                    if (Build.VERSION.SDK_INT < 24) {
                        bitmapUri = Uri.fromFile(file);
                    } else {
                        bitmapUri = Uri.parse(file.getPath()); // Work-around for new SDKs, doesn't work on older ones.
                    }
                }
            } catch (IOException e) {
                FirebaseCrashlytics.getInstance().log("Failed to share app " + e.getLocalizedMessage());
                FirebaseCrashlytics.getInstance().recordException(e);
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            if (null != bitmapUri) {
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            } else {
                shareIntent.setType("text/plain");
            }
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "NoQueue");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "Choose one to share the app"));
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
                        .load(AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, imageUrl))
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


    public static boolean isStoreOpenToday(JsonStore jsonStore) {
        List<JsonHour> jsonHourList = jsonStore.getJsonHours();
        JsonHour jsonHour = AppUtils.getJsonHour(jsonHourList);
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
        String time = df.format(Calendar.getInstance().getTime());
        int timeData = Integer.parseInt(time.replace(":", ""));
        return jsonHour.getTokenAvailableFrom() <= timeData && timeData <= jsonHour.getTokenNotAvailableFrom();
    }

    public static boolean showKioskMode(BizStoreElastic bizStoreElastic) {
        JsonProfile jsonProfile = AppInitialize.getUserProfile();
        if (null != jsonProfile && null != jsonProfile.getBizNameId() && StringUtils.equals(jsonProfile.getBizNameId(), bizStoreElastic.getBizNameId())) {
            if (bizStoreElastic.getBusinessType() == BusinessTypeEnum.DO) {
                return Q_SUPERVISOR == jsonProfile.getUserLevel();
            } else {
                /* Only manager has the capacity to turn on kiosk mode. */
                if (jsonProfile.getCodeQRAndBizStoreIds().containsKey(bizStoreElastic.getCodeQR())) {
                    return S_MANAGER == jsonProfile.getUserLevel();
                }
            }
        }
        return false;
    }

    public static void setReviewCountText(int reviewCount, TextView tv_rating_review) {
        if (reviewCount == 0) {
            tv_rating_review.setText("No Review");
            tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        } else if (reviewCount == 1) {
            tv_rating_review.setText("1 Review");
            tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() | (Paint.UNDERLINE_TEXT_FLAG));
        } else {
            tv_rating_review.setText(reviewCount + " Reviews");
            tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() | (Paint.UNDERLINE_TEXT_FLAG));
        }
    }

    public static BizStoreElastic getStoreElastic(JsonQueueHistorical jsonQueueHistorical) {
        BizStoreElastic bizStoreElastic = new BizStoreElastic();
        bizStoreElastic.setCodeQR(jsonQueueHistorical.getCodeQR());
        bizStoreElastic.setBusinessName(jsonQueueHistorical.getBusinessName());
        return bizStoreElastic;
    }

    public static boolean isValidStoreDistanceForUser(JsonQueue jsonQueue) {
        if (!TextUtils.isEmpty(jsonQueue.getGeoHash())) {
            float lat_s = (float) GeoHashUtils.decodeLatitude(jsonQueue.getGeoHash());
            float long_s = (float) GeoHashUtils.decodeLongitude(jsonQueue.getGeoHash());
            float lat_d = (float) AppInitialize.location.getLatitude();
            float long_d = (float) AppInitialize.location.getLongitude();
            float distance = (float) calculateDistance(lat_s, long_s, lat_d, long_d);
            switch (jsonQueue.getBusinessType()) {
                case DO:
                case HS:
                    if (distance > Constants.VALID_DOCTOR_STORE_DISTANCE_FOR_TOKEN) {
                        return false;
                    }
                    break;
                case BK:
                case CD:
                case CDQ:
                    if (distance > Constants.VALID_CDQ_AND_CD_STORE_DISTANCE_FOR_TOKEN) {
                        return false;
                    }
                    break;
                default:
                    if (distance > Constants.VALID_STORE_DISTANCE_FOR_TOKEN) {
                        return false;
                    }
            }
        }
        return true;
    }

    public static void showAllDaysTiming(Context context, TextView textView, String codeQR) {
        textView.setTextColor(ContextCompat.getColor(context, R.color.btn_color));
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setOnClickListener((View v) -> {
            Intent in = new Intent(context, AllDayTimingActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(IBConstant.KEY_CODE_QR, codeQR);
            in.putExtras(bundle);
            context.startActivity(in);
        });
    }

    public static void saveFavouriteCodeQRs(List<BizStoreElastic> data) {
        List<String> codeQRs = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            codeQRs.add(data.get(i).getCodeQR());
        }
        AppInitialize.saveFavouriteList(codeQRs);
    }

    public static String getLocationAsString(String area, String town) {
        if (StringUtils.isNotBlank(area) && StringUtils.isNotBlank(town)) {
            return area + ", " + town;
        } else if (StringUtils.isNotBlank(area)) {
            return area;
        }
        return StringUtils.isNotBlank(town) ? town : "";
    }

    public static String halfTextBold(String boldText, String normalText) {
        SpannableString str = new SpannableString(boldText + normalText);
        str.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                boldText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return str.toString();
    }
}

