package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.merchant.presenter.beans.body.QueueSetting;


public interface QueueSettingPresenter {
    void queueSettingResponse(QueueSetting queueSetting);

    void queueSettingError();

    void queueSettingError(ErrorEncounteredJson eej);

    void authenticationFailure(int errorCode);
}
