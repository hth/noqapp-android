package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStatList;

public interface ChartPresenter extends ResponseErrorPresenter{

    void chartError();

    void chartResponse(HealthCareStatList healthCareStatList);

    void authenticationFailure(int errorCode);
}
