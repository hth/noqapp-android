package com.noqapp.android.merchant.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
            sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    Log.e("Marquee update", "preference change");
                    if (key.equals(KEY_MARQUEE_LIST)) {
                        if (null != onPreferenceChangeListener)
                            onPreferenceChangeListener.onPreferenceChange();
                    }

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
