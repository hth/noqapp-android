package com.noqapp.android.client.utils;

import android.content.Context;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.ErrorTypeEnum;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;

public class ErrorResponseHandler {

    public static void processError(Context context, ErrorEncounteredJson eej){
        if (null != eej) {
            //Add switch case for errors
            int icon = -1;
            ErrorTypeEnum ete = MobileSystemErrorCodeEnum.valueOf(eej.getSystemErrorCode()).getErrorType();
            switch (ete){
                case INFO: icon = R.drawable.ic_info;
                    break;
                case ALERT: icon = R.drawable.ic_alert;
                    break;
                case ERROR: icon = R.drawable.ic_error;
                    break;
                    default: icon = R.mipmap.launcher;
            }
            ShowAlertInformation.showThemeDialog(context, eej.getSystemError(), eej.getReason(),icon);
        }
    }
}
