package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.merchant.presenter.beans.stats.DoctorStats;

import retrofit2.Response;

public interface ChartPresenter {


    void chartError();

    void chartResponse(DoctorStats doctorStats);
}
