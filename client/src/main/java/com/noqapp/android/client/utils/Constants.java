package com.noqapp.android.client.utils;

import android.util.Log;

import com.noqapp.android.client.BuildConfig;

import org.apache.commons.lang3.StringUtils;

/**
 * User: hitender
 * Date: 3/27/17 2:24 PM
 */

public class Constants {
    public static final String DEVICE_TYPE = "A";
    public static final String ISO8601_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final int requestCodeJoinQActivity = 11;
    public static final int requestCodeNotification = 2;
    public static final String PUSH_NOTIFICATION = "pushNotification";
    private static final String TAG = Constants.class.getName();
    private static String VERSION_RELEASE;

    /**
     * Computes App version.
     *
     * @return
     */
    public static String appVersion() {
        if (StringUtils.isBlank(VERSION_RELEASE)) {
            String computedVersion = BuildConfig.VERSION_NAME.replace(".", "");
            VERSION_RELEASE = Integer.valueOf(computedVersion) > 100 ? computedVersion : "100";
            Log.i(TAG, "App version=" + VERSION_RELEASE);
        }
        return VERSION_RELEASE;
    }
}
