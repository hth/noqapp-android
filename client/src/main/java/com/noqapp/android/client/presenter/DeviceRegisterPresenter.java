package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface DeviceRegisterPresenter extends ResponseErrorPresenter {

    void deviceRegisterError();

    void deviceRegisterResponse(DeviceRegistered deviceRegistered);

}
