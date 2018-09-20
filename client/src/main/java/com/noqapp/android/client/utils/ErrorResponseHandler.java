package com.noqapp.android.client.utils;

import android.content.Context;

import com.noqapp.android.common.beans.ErrorEncounteredJson;

public class ErrorResponseHandler {

    public static void processError(Context context, ErrorEncounteredJson eej){
        if (null != eej) {
            //Add switch case for errors
            ShowAlertInformation.showThemeDialog(context, eej.getSystemError(), eej.getReason());
        }
    }
}
