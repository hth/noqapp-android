package com.noqapp.android.common.utils;


import com.noqapp.android.common.R;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.ErrorTypeEnum;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;


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
            }catch (Exception e){
                e.printStackTrace();
                icon = getDefaultDrawable();
                title = eej.getSystemError();
            }
        }
    }

    protected int getDefaultDrawable(){
        return R.drawable.ic_info;
    }
}
