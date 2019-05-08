package com.noqapp.android.client.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.noqapp.android.common.utils.FontsOverride;

import java.util.Locale;

import androidx.multidex.MultiDexApplication;

/**
 * Created by chandra on 5/20/17.
 */

public class MyApplication extends MultiDexApplication {
    private static final String LOG_TAG = "Application";

    public MyApplication() {
        super();
    }

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
}
