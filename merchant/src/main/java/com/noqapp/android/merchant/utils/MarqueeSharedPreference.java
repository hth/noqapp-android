package com.noqapp.android.merchant.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MarqueeSharedPreference {
    private static final String KEY_MARQUEE_LIST = "marqueeList";
    private static SharedPreferences mSharedPref;
    public static OnPreferenceChangeListener onPreferenceChangeListener;
    private static SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    public interface OnPreferenceChangeListener {
        void onPreferenceChange( );
    }

    private MarqueeSharedPreference() { }

    public static void init(Context context )
    {
        if(mSharedPref == null) {
            mSharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            sharedPreferenceChangeListener = (sharedPreferences, key) -> {
                Log.e("Marquee update", "preference change");
                //TODO Note: key should not be null. There is another issue that needs to be fixed. Better to remove null from shared preferences
                if (StringUtils.isNotBlank(key) && key.equals(KEY_MARQUEE_LIST)) {
                    if (null != onPreferenceChangeListener)
                        onPreferenceChangeListener.onPreferenceChange();
                }

            };
            mSharedPref.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        }
    }


    public static void saveMarquee(List<String> data) {
        Gson gson = new Gson();
        List<String> textList = new ArrayList<String>();
        textList.addAll(data);
        String jsonText = gson.toJson(textList);
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(KEY_MARQUEE_LIST, jsonText);
        editor.apply();
    }

    public static List<String> getMarquee() {
        Gson gson = new Gson();
        String jsonPreferences = mSharedPref.getString(KEY_MARQUEE_LIST, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> marqueeList = gson.fromJson(jsonPreferences, type);
        if (null == marqueeList)
            marqueeList = new ArrayList<String>();
        return marqueeList;
    }

}
