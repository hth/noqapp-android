package com.noqapp.android.merchant.utils;

import android.util.Log;

import com.noqapp.android.common.model.types.DeviceTypeEnum;
import com.noqapp.android.common.utils.BaseConstants;
import com.noqapp.android.merchant.BuildConfig;

import org.apache.commons.lang3.StringUtils;

/**
 * User: hitender
 * Date: 4/16/17 6:06 PM
 */

public class Constants extends BaseConstants {
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
    public static final String FIREBASE_TYPE = "f";
    public static final String GOTO_COUNTER = "g";
    public static final String LAST_NUMBER = "ln";
    public static final String CURRENTLY_SERVING = "cs";
    public static final String QUEUE_STATUS = "q";
    public static final String CODE_QR = "qr";
    public static final int RESULT_SETTING = 123;
    public static final int RESULT_ACQUIRE = 124;
    public static final String CLEAR_DATA = "clearData";
    public static final String CUSTOMER_ACQUIRE = "acquire_customer";

    private static final String TAG = Constants.class.getName();
    private static String VERSION_RELEASE;

    public static final String SYMPTOMS = "SYMPTOMS";
    public static final String PROVISIONAL_DIAGNOSIS = "PROVISIONAL_DIAGNOSIS";
    public static final String DIAGNOSIS = "DIAGNOSIS";
    public static final String INSTRUCTION = "INSTRUCTION";
    public static final String MEDICINE = "MEDICINE";
    public static final String PREFERRED_STORE = "PREFERRED_STORE";
    public static final String DENTAL_PROCEDURE = "DENTAL_PROCEDURE";

    public static final int MAX_IMAGE_UPLOAD_LIMIT = 25;

    /**
     * Computes App version.
     *
     * @return
     */
    public static String appVersion() {
        if (StringUtils.isBlank(VERSION_RELEASE)) {
            switch (BuildConfig.BUILD_TYPE) {
                case "debug":
                    VERSION_RELEASE = "1.2.450";
                    break;
                default:
                    VERSION_RELEASE = BuildConfig.VERSION_NAME;
            }
        }
        Log.i(TAG, "App version=" + VERSION_RELEASE);
        return VERSION_RELEASE;
    }
}
