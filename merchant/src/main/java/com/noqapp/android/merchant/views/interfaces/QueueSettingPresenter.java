package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.body.QueueSetting;


public interface QueueSettingPresenter extends ResponseErrorPresenter{
    void queueSettingResponse(QueueSetting queueSetting);

    void queueSettingError();
}
