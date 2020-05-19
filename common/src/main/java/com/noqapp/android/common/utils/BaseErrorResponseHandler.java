package com.noqapp.android.common.utils;

import com.noqapp.android.common.R;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.ErrorTypeEnum;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;

import android.util.Log;

public abstract class BaseErrorResponseHandler {
    protected int icon;
    protected String title = "";
    protected String msg = "";

    protected void processError(ErrorEncounteredJson eej) {
        if (null != eej) {
            //Add switch case for errors
            try {
                ErrorTypeEnum ete = MobileSystemErrorCodeEnum.valueOf(eej.getSystemError()).getErrorType();
                title = ete.name();
                msg = eej.getReason();
                switch (ete) {
                    case INFO:
                        icon = R.drawable.ic_info;
                        break;
                    case ALERT:
                        icon = R.drawable.ic_alert;
                        break;
                    case ERROR:
                        icon = R.drawable.ic_error;
                        break;
                    default:
                        icon = getDefaultDrawable();
                }
                MobileSystemErrorCodeEnum mobileSystemErrorCodeEnum = MobileSystemErrorCodeEnum.valueOf(eej.getSystemError());
                switch (mobileSystemErrorCodeEnum) {
                    case USER_INPUT:

                    case MOBILE:

                    case MOBILE_JSON:
                    case MOBILE_UPGRADE:
                    case MOBILE_UPLOAD:
                    case MOBILE_UPLOAD_NO_SIZE:
                    case MOBILE_UPLOAD_EXCEED_SIZE:
                    case MOBILE_UPLOAD_UNSUPPORTED_FORMAT:

                    case MOBILE_ACTION_NOT_PERMITTED:

                    case USER_ALREADY_IN_QUEUE:
                    case MERCHANT_COULD_NOT_ACQUIRE:
                    case STORE_OFFLINE:
                    case STORE_DAY_CLOSED:
                    case STORE_TEMP_DAY_CLOSED:
                    case STORE_PREVENT_JOIN:
                    case STORE_NO_LONGER_EXISTS:
                    case QUEUE_NOT_STARTED:
                    case QUEUE_NOT_RE_STARTED:

                    case USER_EXISTING:
                    case USER_NOT_FOUND:
                    case USER_SOCIAL:
                    case MAIL_OTP_FAILED:
                    case USER_MAX_DEPENDENT:
                    case CHANGE_USER_IN_QUEUE:
                    case FAILED_FINDING_ADDRESS:
                    case DEVICE_TIMEZONE_OFF:

                    case MEDICAL_RECORD_ENTRY_DENIED:
                    case MEDICAL_RECORD_ACCESS_DENIED:
                    case MEDICAL_RECORD_DOES_NOT_EXISTS:
                    case MEDICAL_RECORD_POPULATED_WITH_LAB:
                    case BUSINESS_NOT_AUTHORIZED:
                    case BUSINESS_CUSTOMER_ID_DOES_NOT_EXISTS:
                    case BUSINESS_CUSTOMER_ID_EXISTS:
                    case MEDICAL_PROFILE_DOES_NOT_EXISTS:
                    case MEDICAL_PROFILE_CANNOT_BE_CHANGED:

                    case PURCHASE_ORDER_PRICE_MISMATCH:
                    case PURCHASE_ORDER_NOT_FOUND:
                    case PRODUCT_PRICE_CANNOT_BE_ZERO:
                    case PURCHASE_ORDER_FAILED_TO_CANCEL_AS_EXTERNALLY_PAID:
                    case PURCHASE_ORDER_FAILED_TO_CANCEL_PARTIAL_PAY:
                    case PURCHASE_ORDER_FAILED_TO_CANCEL:
                    case PURCHASE_ORDER_ALREADY_CANCELLED:
                    case PURCHASE_ORDER_CANNOT_ACTIVATE:
                    case ORDER_PAYMENT_UPDATE_FAILED:
                    case ORDER_PAYMENT_PAID_ALREADY_FAILED:
                    case PURCHASE_ORDER_PRODUCT_NOT_FOUND:
                    case FAILED_PLACING_MEDICAL_ORDER_AS_INCORRECT_BUSINESS:

                    case QUEUE_JOIN_FAILED_PAYMENT_CALL_REQUEST:
                    case QUEUE_JOIN_PAYMENT_FAILED:
                    case QUEUE_NO_SERVICE_NO_PAY:
                    case QUEUE_ORDER_ABORT_EXPIRED_LIMITED_TIME:
                    case QUEUE_SERVICE_LIMIT:
                    case QUEUE_TOKEN_LIMIT:

                    case SURVEY_NOT_FOUND:

                    case TRANSACTION_GATEWAY_DEFAULT:
                    case SERVICE_PAYMENT_NOT_ALLOWED_FOR_THIS_BUSINESS_TYPE:

                    case CANNOT_ACCEPT_APPOINTMENT:
                    case CANNOT_BOOK_APPOINTMENT:
                    case FAILED_TO_FIND_APPOINTMENT:
                    case FAILED_TO_CANCEL_APPOINTMENT:
                    case FAILED_TO_RESCHEDULE_APPOINTMENT:
                    case APPOINTMENT_ALREADY_EXISTS:
                    case APPOINTMENT_ACTION_NOT_PERMITTED:

                    case SEVERE:
                    case DEVICE_DETAIL_MISSING:
                    case ACCOUNT_INACTIVE:

                    case PROMOTION_ACCESS_DENIED:
                    case COUPON_NOT_APPLICABLE:
                    case COUPON_REMOVAL_FAILED:

                    case WEB_APPLICATION:
                        break;
                    default:
                        msg = eej.getReason();
                }
            } catch (Exception e) {
                e.printStackTrace();
                icon = getDefaultDrawable();
                title = eej.getSystemError();
                msg = eej.getReason();
            }
        }
    }

    protected void processFailureResponseCode(int errorCode) {
        Log.e("Error code received: ", String.valueOf(errorCode));
        switch (errorCode) {
            case 400:
                msg = "Bad request received";
                break;
            case 403:
                msg = "Un-authorized request access";
                break;
            case 404:
                msg = "Oops! Could not find what you are looking for. Looks like we misplaced it :(";
                break;
            case 405:
                msg = "This request is not supported";
                break;
            case 410:
                msg = "Expired link. Try again.";
                break;
            case 414:
                msg = "Failed to understand URL";
                break;
            case 500:
            default:
                msg = "Something went wrong. Engineers are looking into this.";
        }
    }

    protected int getDefaultDrawable() {
        return R.drawable.ic_info;
    }

    protected int getDefaultAlertIcon() {
        return R.drawable.ic_alert;
    }
}
