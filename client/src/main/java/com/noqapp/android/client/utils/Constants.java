package com.noqapp.android.client.utils;

import android.util.Log;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.common.utils.BaseConstants;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * User: hitender
 * Date: 3/27/17 2:24 PM
 */
public class Constants extends BaseConstants{
    public static final String DEVICE_TYPE = "A";
    public static final int requestCodeJoinQActivity = 11;
    public static final int requestCodeAfterJoinQActivity = 12;
    public static final int requestCodeNotification = 2;
    public static final int REQUEST_CODE_SELECT_ADDRESS = 78;
    public static final int REQUEST_CODE_ADD_ADDRESS = 79;

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 10_001;
    public static final int RESULT_CURRENT_LOCATION = 10_002;
    public static final int REQUEST_CHECK_SETTINGS = 10_003;

    public static final int SCREEN_TIME_OUT = 5_000;
    public static final int DISCONNECT_TIMEOUT = 60_000; //// 1 min = 1 * 60 * 1000 ms
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String QRCODE = "qrcode";
    public static final String MESSAGE = "message";
    public static final String STATUS = "status";
    public static final String ISREVIEW = "isreview";
    public static final String FIREBASE_TYPE = "f";
    public static final String CURRENTLY_SERVING = "cs";
    public static final String DISPLAY_SERVING_NUMBER = "ds";
    public static final String CODE_QR = "qr";
    public static final String TOKEN = "t";
    public static final String QID = "qid";
    public static final String MESSAGE_ID = "mi";
    public static final String JSON_USER_ADDRESS = "jsonUserAddress";

    /** Distance in KMs. */
    public static final int VALID_DOCTOR_STORE_DISTANCE_FOR_TOKEN = 150;
    public static final int VALID_CDQ_AND_CD_STORE_DISTANCE_FOR_TOKEN = 100;
    public static final int VALID_STORE_DISTANCE_FOR_TOKEN = 5;

    public static final String ACTIVITY_TO_CLOSE = "activity_status";

    /** Error codes. */
    public static final int INVALID_BAR_CODE = 404;

    /** Google place search api. */
    static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    static final String OUT_JSON = "/json";
    static final String GOOGLE_PLACE_API_KEY = "AIzaSyA9eHl3SHvjXmHFq9q5yPjRy0uqBd5awSc";
    private static final String TAG = Constants.class.getName();
    public static final String MAIL_NOQAPP_COM = "@mail.noqapp.com";
    private static String VERSION_RELEASE;

    /** Shared Preferences keys. */
    public static final String APP_PACKAGE = "com.noqapp.android.client";

    /** Pref key for Token status. */
    public static final String CURRENTLY_SERVING_PREF_KEY = "%s_currently_serving";
    public static final String DISPLAY_SERVING_NUMBER_PREF_KEY = "%s_display_serving_number";
    public static final String ESTIMATED_WAIT_TIME_PREF_KEY = "%s_estimated_wait";
    public static final String PRE_REGISTER = "pre_approve";
    public static final String ADDRESS_LIST = "addresslist";

    public interface LocationConstants {
        int SUCCESS_RESULT = 10_001;
        int FAILURE_RESULT = 10_002;
        int FETCH_LOCATION_JOB_ID = 10_003;
        String RECEIVER = "locationaddress.receiver";
        String RESULT_DATA_KEY = "locationaddress.resultdatakey";

        String COUNTRY_SHORT_NAME = "locationaddress.country.shortName";
        String AREA = "locationaddress.sublocality.level1.longName";
        String TOWN = "locationaddress.locality.longName";
        String DISTRICT = "locationaddress.administrative.area.level2.longName";
        String STATE = "locationaddress.administrative.area.level1.longName";
        String STATE_SHORT_NAME = "locationaddress.administrative.area.level1.shortName";

        String LOCATION_DATA_EXTRA = "locationaddress.locationdataextra";
        String PLACE_NAME = "locationaddress.placename";
        String LOCATION_LAT_DATA_EXTRA = "locationaddress.latextra";
        String LOCATION_LNG_DATA_EXTRA = "locationaddress.lngextra";
    }

    public interface NotificationTypeConstant {
        String FOREGROUND = "foreground";
        String BACKGROUND = "background";
    }

    /**
     * Computes App version.
     *
     * @return
     */
    public static String appVersion() {
        if (StringUtils.isBlank(VERSION_RELEASE)) {
            switch (BuildConfig.BUILD_TYPE) {
                case "debug":
                    VERSION_RELEASE = "1.3.150";
                    break;
                default:
                    VERSION_RELEASE = BuildConfig.VERSION_NAME;
            }
        }
        Log.i(TAG, "App version=" + VERSION_RELEASE);
        return VERSION_RELEASE;
    }
}
