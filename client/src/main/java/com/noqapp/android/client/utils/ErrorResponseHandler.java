package com.noqapp.android.client.utils;

import android.content.Context;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.utils.BaseErrorResponseHandler;

public class ErrorResponseHandler extends BaseErrorResponseHandler {

    public void processError(Context context, ErrorEncounteredJson eej) {
        processError(eej);
        if (null != eej)
            ShowAlertInformation.showThemeDialog(context, title, msg, icon);
    }

    @Override
    protected int getDefaultDrawable() {
        return R.mipmap.launcher;
    }
}
