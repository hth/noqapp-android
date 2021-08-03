package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.model.response.api.DeviceClient;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceClientImpl {
    private final String TAG = DeviceClientImpl.class.getSimpleName();
    private static final DeviceClient DEVICE_CLIENT;

    private DeviceRegisterPresenter deviceRegisterPresenter;

    public void setDeviceRegisterPresenter(DeviceRegisterPresenter deviceRegisterPresenter) {
        this.deviceRegisterPresenter = deviceRegisterPresenter;
    }

    static {
        DEVICE_CLIENT = RetrofitClient.getClient().create(DeviceClient.class);
    }

    /**
     * Register device.
     * Device client registration is called when user is logged in. Otherwise call Device registration.
     * Most of the time it will start with device registration as user is not logged in. But if logged in
     * then call this api. Most likely this would be removed in future as device is registered just once.
     *
     * @param did
     * @param mail
     * @param auth
     * @param deviceToken
     */
    public void register(String did, String mail, String auth, DeviceToken deviceToken) {
        Log.d(TAG, "Registered device api called");
        DEVICE_CLIENT.registration(did, Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, mail, auth, deviceToken).enqueue(new Callback<DeviceRegistered>() {
            @Override
            public void onResponse(@NonNull Call<DeviceRegistered> call, @NonNull Response<DeviceRegistered> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "Registered device " + response.body());
                        deviceRegisterPresenter.deviceRegisterResponse(response.body());
                    } else {
                        Log.e(TAG, "Empty body");
                        deviceRegisterPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        deviceRegisterPresenter.authenticationFailure();
                    } else {
                        deviceRegisterPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeviceRegistered> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure device register" + t.getLocalizedMessage(), t);
                deviceRegisterPresenter.deviceRegisterError();
            }
        });
    }
}
