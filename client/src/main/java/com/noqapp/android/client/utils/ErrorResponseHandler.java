package com.noqapp.android.client.utils;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.utils.BaseErrorResponseHandler;

import android.content.Context;

public class ErrorResponseHandler extends BaseErrorResponseHandler {

    public void processError(Context context, ErrorEncounteredJson eej) {
        processError(eej);
        if (null != eej)
            ShowAlertInformation.showThemeDialog(context, title, msg, icon);
    }

    public void processFailureResponseCode(Context context, int errorCode) {
        processFailureResponseCode(errorCode);
        ShowAlertInformation.showThemeDialog(context, "Error", msg, getDefaultAlertIcon());
    }

    @Override
    protected int getDefaultDrawable() {
        return R.mipmap.launcher;
    }
}
