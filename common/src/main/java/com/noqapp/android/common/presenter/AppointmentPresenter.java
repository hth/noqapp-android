package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;

public interface AppointmentPresenter extends ResponseErrorPresenter {

    void appointmentResponse(JsonScheduleList jsonScheduleList);

    void appointmentBookingResponse(JsonSchedule jsonSchedule);

    void appointmentCancelResponse(JsonResponse jsonResponse);
}
