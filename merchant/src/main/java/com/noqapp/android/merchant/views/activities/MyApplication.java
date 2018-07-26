package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.utils.FontsOverride;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

/**
 * Created by chandra on 5/20/17.
 */

public class MyApplication extends Application {
    private static final String LOG_TAG = "Application";

    public MyApplication() {
        super();
        // TODO Auto-generated constructor stub
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
        setLocale();
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

    private void overwriteConfigurationLocale(Configuration config,
                                              Locale locale) {
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    public void setLocale() {
        Locale locale = getLocaleFromPref();
        Log.d(LOG_TAG, "Set locale to " + locale);
        Configuration config = getBaseContext().getResources()
                .getConfiguration();
        overwriteConfigurationLocale(config, locale);
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
    }
}
