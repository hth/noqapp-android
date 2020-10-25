package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface DeviceRegistrationPresenter extends ResponseErrorPresenter {

    void deviceRegistrationError();

    void deviceRegistrationResponse(JsonResponse jsonResponse);
}
