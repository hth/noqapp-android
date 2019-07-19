package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface HospitalVisitSchedulePresenter extends ResponseErrorPresenter {

    void hospitalVisitScheduleResponse(JsonHospitalVisitScheduleList jsonHospitalVisitScheduleList);
}

