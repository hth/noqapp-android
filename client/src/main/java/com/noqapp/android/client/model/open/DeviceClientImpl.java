package com.noqapp.android.client.model.open;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.model.response.open.DeviceClient;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.AppBlacklistPresenter;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonLatestAppVersion;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.presenter.DeviceRegisterListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/2/17 6:40 PM
 */
public class DeviceClientImpl {
    private final String TAG = DeviceClientImpl.class.getSimpleName();
    private static final DeviceClient DEVICE_REGISTRATION;
    private AppBlacklistPresenter appBlacklistPresenter;
    private DeviceRegisterListener deviceRegisterListener;

    private boolean responseReceived = false;
    private DeviceRegistered deviceRegistered;
    private JsonLatestAppVersion jsonLatestAppVersion;
    private ErrorEncounteredJson errorEncounteredJson;

    public void setDeviceRegisterPresenter(DeviceRegisterListener deviceRegisterListener) {
        this.deviceRegisterListener = deviceRegisterListener;
    }

    public void setAppBlacklistPresenter(AppBlacklistPresenter appBlacklistPresenter) {
        this.appBlacklistPresenter = appBlacklistPresenter;
    }

    static {
        DEVICE_REGISTRATION = RetrofitClient.getClient().create(DeviceClient.class);
    }

    /**
     * Register device.
     *
     * @param deviceToken
     */
    public void register(DeviceToken deviceToken) {
        Log.d(TAG, "Un-Registered device api called");
        DEVICE_REGISTRATION.register(Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, deviceToken).enqueue(new Callback<DeviceRegistered>() {
            @Override
            public void onResponse(@NonNull Call<DeviceRegistered> call, @NonNull Response<DeviceRegistered> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "Registered device " + response.body());
                        deviceRegisterListener.deviceRegisterResponse(response.body());
                        deviceRegistered = response.body();
                    } else {
                        Log.e(TAG, "Empty body");
                        deviceRegisterListener.responseErrorPresenter(response.body().getError());
                        errorEncounteredJson = response.body().getError();
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        deviceRegisterListener.authenticationFailure();
                    } else {
                        deviceRegisterListener.responseErrorPresenter(response.code());
                    }
                }
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<DeviceRegistered> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure device register" + t.getLocalizedMessage(), t);
                deviceRegisterListener.deviceRegisterError();
                responseReceived = true;
            }
        });
    }

    /**
     * Check is current app version is supported.
     */
    public void isSupportedAppVersion() {
        DEVICE_REGISTRATION.isSupportedAppVersion(Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, Constants.appVersion()).enqueue(new Callback<JsonLatestAppVersion>() {
            @Override
            public void onResponse(@NonNull Call<JsonLatestAppVersion> call, @NonNull Response<JsonLatestAppVersion> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    Log.d("response body isSupport", response.body().toString());
                    if (null != response.body() && null == response.body().getError()) {
                        appBlacklistPresenter.appBlacklistResponse(response.body());
                        jsonLatestAppVersion = response.body();
                    } else {
                        appBlacklistPresenter.appBlacklistError(response.body().getError());
                        errorEncounteredJson = response.body().getError();
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        appBlacklistPresenter.authenticationFailure();
                    } else {
                        appBlacklistPresenter.responseErrorPresenter(response.code());
                    }
                }
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<JsonLatestAppVersion> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure Response " + t.getLocalizedMessage(), t);
                appBlacklistPresenter.appBlacklistError(null);
                responseReceived = true;
            }
        });
    }

    boolean isResponseReceived() {
        return responseReceived;
    }

    DeviceRegistered getDeviceRegistered() {
        return deviceRegistered;
    }

    JsonLatestAppVersion getJsonLatestAppVersion() {
        return jsonLatestAppVersion;
    }

    ErrorEncounteredJson getErrorEncounteredJson() {
        return errorEncounteredJson;
    }
}
