package com.noqapp.android.client.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.noqapp.android.client.views.pojos.KioskModeInfo;
import com.noqapp.android.client.views.pojos.LocationPref;
import com.noqapp.android.common.utils.FontsOverride;

import java.util.Locale;

/**
 * Created by chandra on 5/20/17.
 */

public class MyApplication extends MultiDexApplication {
    public static SharedPreferences preferences;
    public static final String PREKEY_IS_NOTIFICATION_SOUND_ENABLE = "isNotificationSoundEnable";
    public static final String PREKEY_IS_NOTIFICATION_RECEIVE_ENABLE = "isNotificationReceiveEnable";
    private static final String KEY_LOCATION_PREFERENCE= "locationPreference";
    public static String APP_PREF = "splashPref";
    public MyApplication() {
        super();
    }
    public static FirebaseAnalytics getFireBaseAnalytics() {
        return fireBaseAnalytics;
    }

    private static FirebaseAnalytics fireBaseAnalytics;
    /**
     * On application startup, override system default locale to which user set
     * in application preference.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.overrideFont(this, "DEFAULT", "fonts/roboto_regular.ttf");
        FontsOverride.overrideFont(this, "MONOSPACE", "fonts/roboto_regular.ttf");
        FontsOverride.overrideFont(this, "SERIF", "fonts/roboto_regular.ttf");
        FontsOverride.overrideFont(this, "SANS_SERIF", "fonts/roboto_regular.ttf");
        setLocale(this);
        preferences = getSharedPreferences( getPackageName() + "_preferences", MODE_PRIVATE);
        fireBaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private Locale getLocaleFromPref() {
        Locale locale = Locale.getDefault();
        String language = getPreferences().getString("pref_language", "");
        if (!language.equals("")) {
            locale = new Locale(language);
            Locale.setDefault(locale);
        }
        return locale;
    }

    public void setLocale(Context ctx) {
        Locale newLocale = getLocaleFromPref();
        Resources activityRes = ctx.getResources();
        Configuration activityConf = activityRes.getConfiguration();

        activityConf.setLocale(newLocale);
        activityRes.updateConfiguration(activityConf, activityRes.getDisplayMetrics());

        Resources applicationRes = getBaseContext().getResources();
        Configuration applicationConf = applicationRes.getConfiguration();
        applicationConf.setLocale(newLocale);
        applicationRes.updateConfiguration(applicationConf,
                applicationRes.getDisplayMetrics());
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
    }

    public static boolean isNotificationSoundEnable() {
        Log.e("Sound enable",String.valueOf(preferences.getBoolean(PREKEY_IS_NOTIFICATION_SOUND_ENABLE, true)));
        return preferences.getBoolean(PREKEY_IS_NOTIFICATION_SOUND_ENABLE, true);
    }

    public static void setNotificationSoundEnable(boolean check) {
        preferences.edit().putBoolean(PREKEY_IS_NOTIFICATION_SOUND_ENABLE, check).apply();
    }

    public static boolean isNotificationReceiveEnable() {
        return preferences.getBoolean(PREKEY_IS_NOTIFICATION_RECEIVE_ENABLE, true);
    }

    public static void setNotificationReceiveEnable(boolean check) {
        preferences.edit().putBoolean(PREKEY_IS_NOTIFICATION_RECEIVE_ENABLE, check).apply();
    }

    public static LocationPref getLocationPreference() {
        String json = preferences.getString(KEY_LOCATION_PREFERENCE, "");
        if (TextUtils.isEmpty(json)) {
            return new LocationPref();
        } else {
            return new Gson().fromJson(json, LocationPref.class);
        }
    }

    public static void setLocationPreference(LocationPref locationPreference) {
        SharedPreferences.Editor editor = preferences.edit();
        String json = new Gson().toJson(locationPreference);
        editor.putString(KEY_LOCATION_PREFERENCE, json);
        editor.apply();
    }
}
