package com.noqapp.android.client.views.interfaces;

import com.noqapp.android.common.beans.DeviceRegistered;

public interface DeviceRegisterPresenter {

    void deviceRegisterError();

    void deviceRegisterResponse(DeviceRegistered deviceRegistered);

}
