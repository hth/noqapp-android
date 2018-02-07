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
    public static final String Firebase_Type = "f";
    public static final String GoTo_Counter = "g";
    public static final String LastNumber = "ln";
    public static final String CurrentlyServing = "cs";
    public static final String QueueStatus = "q";
    public static final String CodeQR = "c";
    public static final String QueueUserState = "u";

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
            "#AA96DA",
            "#A1EAFB",
            "#FFCEF3",
            "#CABBE9",
            "#E5C9C9",
            "#FFECDA",
            "#F9FFEA",
            "#A6D0E4",
            "#D6E4F0",
            "#FFCCCC",
            "#B2E6E9",
            "#CDCFD2",
            "#BBE5DB",
            "#A6E4E7",
            "#D5D5F9",
            "#FFFEEC",
            "#FCCDE2",
            "#E9E9E5",
            "#FDFCE0",
            "#D1D8F5",
            "#C3E9FD",
            "#C4F5F1",
            "#F4FADE",
            "#D8E9F0",
            "#D4A5A5",
            "#BBDBDD"
    };

    /**
     * Computes App version.
     *
     * @return
     */
    public static String appVersion() {
        if (StringUtils.isBlank(VERSION_RELEASE)) {
            switch (BuildConfig.BUILD_TYPE) {
                case "debug":
                    VERSION_RELEASE = "1.1.1";
                    break;
                default:
                    VERSION_RELEASE = BuildConfig.VERSION_NAME;
            }
        }
        Log.i(TAG, "App version=" + VERSION_RELEASE);
        return VERSION_RELEASE;
    }
}
