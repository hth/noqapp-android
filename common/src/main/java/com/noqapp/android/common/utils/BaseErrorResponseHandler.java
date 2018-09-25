package com.noqapp.android.common.utils;


import static com.noqapp.android.common.model.types.ErrorTypeEnum.ALERT;
import static com.noqapp.android.common.model.types.ErrorTypeEnum.ERROR;

import com.noqapp.android.common.R;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.ErrorTypeEnum;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;

import android.util.Log;


public abstract class BaseErrorResponseHandler {
    protected int icon;
    protected String title = "";
    protected String msg = "";

    protected void processError(ErrorEncounteredJson eej){
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
                        break;
                    case MOBILE:
                        break;
                    case MOBILE_JSON:
                        break;
                    case MOBILE_UPGRADE:
                        break;
                    case MOBILE_UPLOAD:
                        break;
                    case MOBILE_UPLOAD_NO_SIZE:
                        break;
                    case MOBILE_UPLOAD_EXCEED_SIZE:
                        break;
                    case MOBILE_UPLOAD_UNSUPPORTED_FORMAT:
                        break;
                    case MOBILE_ACTION_NOT_PERMITTED:
                        break;
                    case MERCHANT_COULD_NOT_ACQUIRE:
                        break;
                    case USER_EXISTING:
                        break;
                    case USER_NOT_FOUND:
                        break;
                    case USER_SOCIAL:
                        break;
                    case MAIL_OTP_FAILED:
                        break;
                    case USER_MAX_DEPENDENT:
                        break;
                    case MEDICAL_RECORD_ENTRY_DENIED:
                        break;
                    case MEDICAL_RECORD_ACCESS_DENIED:
                        break;
                    case BUSINESS_NOT_AUTHORIZED:
                        break;
                    case BUSINESS_CUSTOMER_ID_DOES_NOT_EXISTS:
                        break;
                    case BUSINESS_CUSTOMER_ID_EXISTS:
                        break;
                    case SEVERE:
                        break;
                    case DEVICE_DETAIL_MISSING:
                        break;
                    case WEB_APPLICATION:
                        break;
                    default:
                        msg = eej.getReason();
                }
            }catch (Exception e){
                e.printStackTrace();
                icon = getDefaultDrawable();
                title = eej.getSystemError();
                msg = eej.getReason();
            }
        }
    }


    protected void processFailureResponseCode(int errorCode) {
        Log.e("Error code recieved: ",""+errorCode);
        switch (errorCode) {
            case 500:
                msg = "Temporary service unavailable";
                break;
            case 404:
                break;
            default:
                msg = "Something weird happen";
        }


    }
    protected int getDefaultDrawable(){
        return R.drawable.ic_info;
    }

    protected int getDefaultAlertIcon(){
        return R.drawable.ic_alert;
    }
}
