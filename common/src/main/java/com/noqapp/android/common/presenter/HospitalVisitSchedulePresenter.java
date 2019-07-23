package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;

public interface HospitalVisitSchedulePresenter extends ResponseErrorPresenter {

    void hospitalVisitScheduleResponse(JsonHospitalVisitScheduleList jsonHospitalVisitScheduleList);

    void hospitalVisitScheduleResponse(JsonHospitalVisitSchedule jsonHospitalVisitSchedule);
}

