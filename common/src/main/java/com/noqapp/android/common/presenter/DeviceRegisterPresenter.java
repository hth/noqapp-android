package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.DeviceRegistered;

public interface DeviceRegisterPresenter extends ResponseErrorPresenter {

    void deviceRegisterError();

    void deviceRegisterResponse(DeviceRegistered deviceRegistered);
}
