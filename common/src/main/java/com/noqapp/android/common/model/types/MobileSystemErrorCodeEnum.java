package com.noqapp.android.common.model.types;

import static com.noqapp.android.common.model.types.ErrorTypeEnum.ALERT;
import static com.noqapp.android.common.model.types.ErrorTypeEnum.ERROR;

/**
 * Error code to share between APP and Mobile API.
 * User: hitender
 * Date: 1/10/17 10:57 PM
 */
public enum MobileSystemErrorCodeEnum {
    /** Can be user input or mobile submission. */
    USER_INPUT("1000", ERROR),

    /** Issue in mobile data submitted. */
    MOBILE("2000", ERROR),

    /** When cannot parse JSON sent to Mobile Server from mobile devices. */
    MOBILE_JSON("2010", ERROR),
    MOBILE_UPGRADE("2022", ALERT),
    MOBILE_UPLOAD("2023", ERROR),
    MOBILE_UPLOAD_NO_SIZE("2024", ALERT),
    MOBILE_UPLOAD_EXCEED_SIZE("2025", ALERT),
    MOBILE_UPLOAD_UNSUPPORTED_FORMAT("2026", ALERT),

    MOBILE_ACTION_NOT_PERMITTED("2101", ALERT),

    /** System alerts or warning when something out of ordinary happens. */
    USER_ALREADY_IN_QUEUE("3030", ALERT),
    MERCHANT_COULD_NOT_ACQUIRE("3050", ALERT),
    STORE_OFFLINE("3060", ALERT),
    STORE_DAY_CLOSED("3061", ALERT),
    STORE_TEMP_DAY_CLOSED("3062", ALERT),
    STORE_PREVENT_JOIN("3063", ALERT),
    STORE_NO_LONGER_EXISTS("3064", ALERT),

    /** User related. */
    USER_EXISTING("4010", ALERT),
    USER_NOT_FOUND("4012", ALERT),
    USER_SOCIAL("4016", ALERT),
    MAIL_OTP_FAILED("4020", ERROR),
    USER_MAX_DEPENDENT("4050", ALERT),
    CHANGE_USER_IN_QUEUE("4060", ALERT),

    /** Medical. */
    MEDICAL_RECORD_ENTRY_DENIED("4101", ERROR),
    MEDICAL_RECORD_ACCESS_DENIED("4102", ERROR),
    MEDICAL_RECORD_DOES_NOT_EXISTS("4104", ERROR),
    MEDICAL_RECORD_POPULATED_WITH_LAB("4105", ERROR),
    BUSINESS_NOT_AUTHORIZED("4120", ERROR),
    BUSINESS_CUSTOMER_ID_DOES_NOT_EXISTS("4121", ALERT),
    BUSINESS_CUSTOMER_ID_EXISTS("4122", ALERT),

    /** Orders. */
    PURCHASE_ORDER_PRICE_MISMATCH("4203", ALERT),
    PURCHASE_ORDER_NOT_FOUND("4204", ALERT),
    PURCHASE_ORDER_FAILED_TO_CANCEL_PARTIAL_PAY("4207", ALERT),
    PURCHASE_ORDER_FAILED_TO_CANCEL("4208", ALERT),
    PURCHASE_ORDER_ALREADY_CANCELLED("4209", ALERT),
    PURCHASE_ORDER_CANNOT_ACTIVATE("4210", ALERT),
    ORDER_PAYMENT_UPDATE_FAILED("4211", ALERT),
    PURCHASE_ORDER_PRODUCT_NOT_FOUND("4212", ALERT),

    /** Queue. */
    QUEUE_JOIN_FAILED_PAYMENT_CALL_REQUEST("4304", ERROR),
    QUEUE_JOIN_PAYMENT_FAILED("4305", ERROR),

    /** Transaction. */
    TRANSACTION_GATEWAY_DEFAULT("4500", ALERT),

    /** Mobile application related issue. */
    SEVERE("5000", ERROR),
    DEVICE_DETAIL_MISSING("5010", ERROR),
    ACCOUNT_INACTIVE("5015", ERROR),

    /** Not mobile web application. */
    WEB_APPLICATION("6000", ERROR);

    private String code;
    private ErrorTypeEnum errorType;

    MobileSystemErrorCodeEnum(String code, ErrorTypeEnum errorType) {
        this.code = code;
        this.errorType = errorType;
    }

    public String getCode() {
        return code;
    }

    public ErrorTypeEnum getErrorType() {
        return errorType;
    }
}