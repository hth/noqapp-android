package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.body.StoreSetting;

public interface StoreSettingPresenter extends ResponseErrorPresenter{
    void queueSettingResponse(StoreSetting storeSetting);

    void queueSettingModifyResponse(StoreSetting storeSetting);

    void queueSettingError();
}
