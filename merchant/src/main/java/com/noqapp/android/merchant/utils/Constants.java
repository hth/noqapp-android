package com.noqapp.android.merchant.utils;

import android.util.Log;

import com.noqapp.android.merchant.BuildConfig;

import org.apache.commons.lang3.StringUtils;

/**
 * User: hitender
 * Date: 4/16/17 6:06 PM
 */

public class Constants {
    private static final String TAG = Constants.class.getName();

    public static final String DEVICE_TYPE = "A";
    public static final int requestCodeNotification = 2;
    // broadcast receiver intent filters
    public static final String PUSH_NOTIFICATION = "pushNotification";
    private static String VERSION_RELEASE;

    /**
     * Computes App version.
     *
     * @return
     */
    public static String appVersion() {
        if (StringUtils.isBlank(VERSION_RELEASE)) {
            String computedVersion = BuildConfig.VERSION_NAME.replaceAll(".", "");
            VERSION_RELEASE = Integer.valueOf(computedVersion) > 100 ? computedVersion : "100";
            Log.i(TAG, "App version=" + VERSION_RELEASE);
        }
        return VERSION_RELEASE;
    }
}
