package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.model.response.api.DeviceRegistrationApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.DeviceRegistrationPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.merchant.utils.Constants.DEVICE_TYPE;

public class DeviceRegistrationApiCalls {
    private final String TAG = DeviceRegistrationApiCalls.class.getSimpleName();

    private static final DeviceRegistrationApiUrls deviceRegistrationApiUrls;
    private DeviceRegistrationPresenter deviceRegistrationPresenter;

    public void setDeviceRegisterPresenter(DeviceRegistrationPresenter deviceRegistrationPresenter) {
        this.deviceRegistrationPresenter = deviceRegistrationPresenter;
    }

    static {
        deviceRegistrationApiUrls = RetrofitClient.getClient().create(DeviceRegistrationApiUrls.class);
    }

    /**
     * Register device.
     *
     * @param did
     * @param mail
     * @param auth
     * @param deviceToken
     */
    public void register(String did, String mail, String auth, DeviceToken deviceToken) {
        deviceRegistrationApiUrls.registration(did, DEVICE_TYPE, BuildConfig.APP_FLAVOR, mail, auth, deviceToken).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "Registered device " + response.body());
                        deviceRegistrationPresenter.deviceRegistrationResponse(response.body());
                    } else {
                        Log.e(TAG, "Empty body");
                        deviceRegistrationPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        deviceRegistrationPresenter.authenticationFailure();
                    } else {
                        deviceRegistrationPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "register " + t.getLocalizedMessage(), t);
                deviceRegistrationPresenter.deviceRegistrationError();
            }
        });
    }
}
