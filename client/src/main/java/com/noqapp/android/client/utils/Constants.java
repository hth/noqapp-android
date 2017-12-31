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
    public static final String QRCODE = "qrcode";
    public static final String MESSAGE = "message";
    public static final String STATUS = "status";
    public static final String ISREVIEW = "isreview";
    public static final String CURRENT_SERVING = "current_serving";
    public static final String LASTNO = "lastno";
    public static final String MSG_TYPE_F = "f";
    public static final String MSG_TYPE_G = "g";
    public static final String MSG_TYPE_LN = "ln";
    public static final String MSG_TYPE_CS = "cs";
    public static final String MSG_TYPE_Q = "q";
    public static final String MSG_TYPE_C = "c";
    public static final String MSG_TYPE_U = "u";

    //error codes
    public static final int INVALID_CREDENTIAL = 401;
    public static final int INVALID_BAR_CODE = 404;
    public static final int DEFAULT_REVIEW_TIME_SAVED = 1;
    private static final String TAG = Constants.class.getName();
    private static String VERSION_RELEASE;

    //Urls
    public static final String URL_ABOUT_US = "https://noqapp.com/mobile/m.about-us.html";
    public static final String URL_TERM_CONDITION = "https://noqapp.com/mobile/m.terms.html";
    public static final String URL_PRIVACY_POLICY = "https://noqapp.com/mobile/m.privacy.html";
    public static final String URL_HOW_IT_WORKS = "https://noqapp.com/mobile/m.how-invite-works.html";

    public static final String[] colorCodes = new String[]{
            "#F08a5d",
            "#B83b5e",
            "#Aa96da",
            "#Ff9999",
            "#6a2c70",
            "#3fc1c9",
            "#3F72AF",
            "#9896F1",
            "#15B7B9",
            "#52616B",
            "#1FAB89",
            "#3FC1C9",
            "#3498DB",
            "#E41655"};

    /**
     * Computes App version.
     *
     * @return
     */
    public static String appVersion() {
        if (StringUtils.isBlank(VERSION_RELEASE)) {
            String computedVersion = BuildConfig.VERSION_NAME.replace(".", "");
            VERSION_RELEASE = Integer.valueOf(computedVersion) > 100 ? computedVersion : "108";
            Log.i(TAG, "App version=" + VERSION_RELEASE);
        }
        return VERSION_RELEASE;
    }
}
