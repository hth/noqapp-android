package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.model.response.api.DeviceClientApiUrls;
import com.noqapp.android.client.model.response.open.DeviceApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.AppBlacklistPresenter;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonLatestAppVersion;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 4/2/17 6:40 PM
 */
public class DeviceApiCall {
    private final String TAG = DeviceApiCall.class.getSimpleName();
    private static final DeviceApiUrls deviceApiUrls;
    private static final DeviceClientApiUrls deviceClientApiUrls;
    private AppBlacklistPresenter appBlacklistPresenter;
    private DeviceRegisterPresenter deviceRegisterPresenter;

    private boolean responseReceived = false;
    private DeviceRegistered deviceRegistered;
    private JsonLatestAppVersion jsonLatestAppVersion;
    private ErrorEncounteredJson errorEncounteredJson;

    public void setDeviceRegisterPresenter(DeviceRegisterPresenter deviceRegisterPresenter) {
        this.deviceRegisterPresenter = deviceRegisterPresenter;
    }

    public void setAppBlacklistPresenter(AppBlacklistPresenter appBlacklistPresenter) {
        this.appBlacklistPresenter = appBlacklistPresenter;
    }

    static {
        deviceApiUrls = RetrofitClient.getClient().create(DeviceApiUrls.class);
        deviceClientApiUrls = RetrofitClient.getClient().create(DeviceClientApiUrls.class);
    }

    /**
     * Register device.
     *
     * @param deviceToken
     */
    public void register(DeviceToken deviceToken) {
        Log.d(TAG, "Un-Registered device api called");
        deviceApiUrls.register(Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, deviceToken).enqueue(new Callback<DeviceRegistered>() {
            @Override
            public void onResponse(@NonNull Call<DeviceRegistered> call, @NonNull Response<DeviceRegistered> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "Registered device " + response.body());
                        deviceRegisterPresenter.deviceRegisterResponse(response.body());
                        deviceRegistered = response.body();
                    } else {
                        Log.e(TAG, "Empty body");
                        deviceRegisterPresenter.responseErrorPresenter(response.body().getError());
                        errorEncounteredJson = response.body().getError();
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        deviceRegisterPresenter.authenticationFailure();
                    } else {
                        deviceRegisterPresenter.responseErrorPresenter(response.code());
                    }
                }
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<DeviceRegistered> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure device register" + t.getLocalizedMessage(), t);
                deviceRegisterPresenter.deviceRegisterError();
                responseReceived = true;
            }
        });
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
        deviceClientApiUrls.registration(did, DEVICE_TYPE, BuildConfig.APP_FLAVOR, mail, auth, deviceToken).enqueue(new Callback<DeviceRegistered>() {
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

    /**
     * Check is current app version is supported.
     */
    public void isSupportedAppVersion() {
        deviceApiUrls.isSupportedAppVersion(Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, Constants.appVersion()).enqueue(new Callback<JsonLatestAppVersion>() {
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
