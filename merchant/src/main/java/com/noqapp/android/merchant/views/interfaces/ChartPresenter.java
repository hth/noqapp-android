package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.merchant.presenter.beans.stats.DoctorStats;

public interface ChartPresenter {


    void chartError();

    void chartResponse(DoctorStats doctorStats);
}
