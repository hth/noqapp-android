package com.noqapp.android.client.utils;

import com.noqapp.android.client.BuildConfig;

import org.apache.commons.lang3.StringUtils;

import android.util.Log;

/**
 * User: hitender
 * Date: 3/27/17 2:24 PM
 */

public class Constants {
    public static final String DEVICE_TYPE = "A";
    public static final String ISO8601_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final int requestCodeJoinQActivity = 11;
    public static final int requestCodeAfterJoinQActivity = 12;
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
    public static final String CodeQR = "qr";
    public static final String QueueUserState = "u";
    public static final String BusinessType = "bt";
    public static final String TOKEN = "t";
    public static final String QuserID = "qid";
    public static final String MESSAGE_ID = "mi";
    public static final String FCM_TYPE = "ft";
    public static final String ORDER_STATE = "os";
    public static final String ACTIVITY_TO_CLOSE = "activity_status";
    public static final String FROM_JOIN_SCREEN = "from_join_screen";
    //error codes
    public static final int INVALID_CREDENTIAL = 401;
    public static final int INVALID_BAR_CODE = 404;
    public static final int DEFAULT_REVIEW_TIME_SAVED = 3;
    //Urls
    public static final String URL_ABOUT_US = "https://noqapp.com/mobile/m.about-us.html";
    public static final String URL_TERM_CONDITION = "https://noqapp.com/mobile/m.terms.html";
    public static final String URL_PRIVACY_POLICY = "https://noqapp.com/mobile/m.privacy.html";
    public static final String URL_HOW_IT_WORKS = "https://noqapp.com/mobile/m.how-invite-works.html";
    //Google place search api
    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    public static final String OUT_JSON = "/json";
    public static final String COUNTRY_CODE = "IN";
    public static final String API_KEY = "AIzaSyA9eHl3SHvjXmHFq9q5yPjRy0uqBd5awSc";
    private static final String TAG = Constants.class.getName();
    public static final String EMAIL_SUFFIX = "@mail.noqapp.com";
    private static String VERSION_RELEASE;

    public static int SUCCESS = 1;

    /**
     * Computes App version.
     *
     * @return
     */
    public static String appVersion() {
        if (StringUtils.isBlank(VERSION_RELEASE)) {
            switch (BuildConfig.BUILD_TYPE) {
                case "debug":
                    VERSION_RELEASE = "1.2.100";
                    break;
                default:
                    VERSION_RELEASE = BuildConfig.VERSION_NAME;
            }
        }
        Log.i(TAG, "App version=" + VERSION_RELEASE);
        return VERSION_RELEASE;
    }
}
