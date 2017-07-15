package com.noqapp.android.merchant.utils;

import android.app.Activity;
import android.util.Log;

import com.noqapp.android.merchant.BuildConfig;

import org.apache.commons.lang3.StringUtils;

/**
 * User: hitender
 * Date: 4/16/17 6:06 PM
 */

public class Constants {
    public static final String DEVICE_TYPE = "A";
    public static final int requestCodeNotification = 2;
    // broadcast receiver intent filters
    public static final String PUSH_NOTIFICATION = "pushNotification";
    private static final String TAG = Constants.class.getName();
    public static final String QRCODE = "qrcode";
    public static final String MESSAGE = "message";
    public static final String STATUS = "status";
    public static final String CURRENT_SERVING = "current_serving";
    public static final String LASTNO = "lastno";
    public static final String MSG_TYPE_F = "f";
    public static final String MSG_TYPE_G = "g";
    public static final String MSG_TYPE_LN = "ln";
    public static final String MSG_TYPE_CS = "cs";
    public static final String MSG_TYPE_Q = "q";
    public static final String MSG_TYPE_C = "c";
    private static String VERSION_RELEASE;

    public static final int RESULT_SETTING = 123;
    public static final String CLEAR_DATA = "clearData";

    //error codes
    public static final int INVALID_CREDENTIAL = 401;

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
