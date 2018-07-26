package com.noqapp.android.merchant.utils;

import com.noqapp.android.common.model.types.DeviceTypeEnum;
import com.noqapp.android.merchant.BuildConfig;

import org.apache.commons.lang3.StringUtils;

import android.util.Log;

/**
 * User: hitender
 * Date: 4/16/17 6:06 PM
 */

public class Constants {
    public static final String ISO8601_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DEVICE_TYPE = DeviceTypeEnum.A.getName();
    public static final int requestCodeNotification = 2;
    // broadcast receiver intent filters
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String QRCODE = "qrcode";
    public static final String MESSAGE = "message";
    public static final String STATUS = "status";
    public static final String CURRENT_SERVING = "current_serving";
    public static final String LASTNO = "lastno";
    public static final String Firebase_Type = "f";
    public static final String GoTo_Counter = "g";
    public static final String LastNumber = "ln";
    public static final String CurrentlyServing = "cs";
    public static final String QueueStatus = "q";
    public static final String CodeQR = "qr";
    public static final int RESULT_SETTING = 123;
    public static final int RESULT_ACQUIRE = 124;
    public static final String CLEAR_DATA = "clearData";
    public static final String CUSTOMER_ACQUIRE = "acquire_customer";
    //error codes
    public static final int INVALID_CREDENTIAL = 401;
    private static final String TAG = Constants.class.getName();
    private static String VERSION_RELEASE;

    /**
     * Computes App version.
     *
     * @return
     */
    public static String appVersion() {
        if (StringUtils.isBlank(VERSION_RELEASE)) {
            switch (BuildConfig.BUILD_TYPE) {
                case "debug":
                    VERSION_RELEASE = "1.2.0";
                    break;
                default:
                    VERSION_RELEASE = BuildConfig.VERSION_NAME;
            }
        }
        Log.i(TAG, "App version=" + VERSION_RELEASE);
        return VERSION_RELEASE;
    }
}
