package com.noqapp.android.client.utils;

import android.util.Log;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.common.utils.BaseConstants;

import org.apache.commons.lang3.StringUtils;

/**
 * User: hitender
 * Date: 3/27/17 2:24 PM
 */
public class Constants extends BaseConstants{
    public static final String DEVICE_TYPE = "A";
    public static final int requestCodeJoinQActivity = 11;
    public static final int requestCodeAfterJoinQActivity = 12;
    public static final int requestCodeNotification = 2;
    public static final int SCREEN_TIME_OUT = 5000;
    public static final int DISCONNECT_TIMEOUT = 60000; //// 1 min = 1 * 60 * 1000 ms
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String QRCODE = "qrcode";
    public static final String MESSAGE = "message";
    public static final String STATUS = "status";
    public static final String ISREVIEW = "isreview";
    public static final String FIREBASE_TYPE = "f";
    public static final String CURRENTLY_SERVING = "cs";
    public static final String CODE_QR = "qr";
    public static final String TOKEN = "t";
    public static final String QID = "qid";
    public static final String MESSAGE_ID = "mi";

    public static final int VALID_DOCTOR_STORE_DISTANCE_FOR_TOKEN = 150;
    public static final int VALID_STORE_DISTANCE_FOR_TOKEN = 5;

    public static final String ACTIVITY_TO_CLOSE = "activity_status";

    //error codes
    public static final int INVALID_BAR_CODE = 404;

    //Google place search api
    static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    static final String OUT_JSON = "/json";
    static final String GOOGLE_PLACE_API_KEY = "AIzaSyA9eHl3SHvjXmHFq9q5yPjRy0uqBd5awSc";
    private static final String TAG = Constants.class.getName();
    public static final String MAIL_NOQAPP_COM = "@mail.noqapp.com";
    private static String VERSION_RELEASE;



    // Shared Preferences keys
    public static final String APP_PACKAGE = "com.noqapp.android.client";
    // Pref key for Token status
    public static final String CURRENTLY_SERVING_PREF_KEY = "%s_currently_serving";
    public static final String ESTIMATED_WAIT_TIME_PREF_KEY = "%s_estimated_wait";
    public static final String PRE_REGISTER = "pre_approve";

    /**
     * Computes App version.
     *
     * @return
     */
    public static String appVersion() {
        if (StringUtils.isBlank(VERSION_RELEASE)) {
            switch (BuildConfig.BUILD_TYPE) {
                case "debug":
                    VERSION_RELEASE = "1.2.550";
                    break;
                default:
                    VERSION_RELEASE = BuildConfig.VERSION_NAME;
            }
        }
        Log.i(TAG, "App version=" + VERSION_RELEASE);
        return VERSION_RELEASE;
    }
}
