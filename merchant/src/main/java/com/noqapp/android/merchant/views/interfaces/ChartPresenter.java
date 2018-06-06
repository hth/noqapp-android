package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStatList;

public interface ChartPresenter {


    void chartError();

    void chartResponse(HealthCareStatList healthCareStatList);
}
