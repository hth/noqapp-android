package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.body.merchant.StoreHours;

public interface StoreHoursSettingPresenter extends ResponseErrorPresenter {

    void queueStoreHoursSettingResponse(StoreHours storeHours);

    void queueStoreHoursSettingModifyResponse(JsonResponse jsonResponse);

    void queueStoreHoursSettingError();
}
