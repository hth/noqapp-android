package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.JsonScheduleList;

public interface AppointmentPresenter extends ResponseErrorPresenter{

    void appointmentResponse(JsonScheduleList jsonScheduleList);
}
