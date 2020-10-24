package com.noqapp.android.client.utils;

import android.app.Activity;
import android.content.Context;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.utils.BaseErrorResponseHandler;

public class ErrorResponseHandler extends BaseErrorResponseHandler {

    public void processError(Activity contextActivity, ErrorEncounteredJson eej) {
        processError(eej);
        if (null != eej)
            ShowAlertInformation.showThemeDialog(contextActivity, title, msg, icon);
    }

    public void processFailureResponseCode(Activity contextActivity, int errorCode) {
        processFailureResponseCode(errorCode);
        ShowAlertInformation.showThemeDialog(contextActivity, "Error", msg, getDefaultAlertIcon());
    }

    @Override
    protected int getDefaultDrawable() {
        return R.mipmap.launcher;
    }
}
