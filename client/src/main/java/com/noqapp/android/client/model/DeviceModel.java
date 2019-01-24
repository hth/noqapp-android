package com.noqapp.android.client.model;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.model.response.open.DeviceService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.AppBlacklistPresenter;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.JsonLatestAppVersion;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/2/17 6:40 PM
 */
public class DeviceModel {
    private final String TAG = DeviceModel.class.getSimpleName();
    private static final DeviceService deviceService;
    private AppBlacklistPresenter appBlacklistPresenter;
    private DeviceRegisterPresenter deviceRegisterPresenter;

    public void setDeviceRegisterPresenter(DeviceRegisterPresenter deviceRegisterPresenter) {
        this.deviceRegisterPresenter = deviceRegisterPresenter;
    }

    public void setAppBlacklistPresenter(AppBlacklistPresenter appBlacklistPresenter) {
        this.appBlacklistPresenter = appBlacklistPresenter;
    }

    static {
        deviceService = RetrofitClient.getClient().create(DeviceService.class);
    }

    /**
     * Register device.
     *
     * @param did
     * @param deviceToken
     */
    public void register(String did, DeviceToken deviceToken) {
        deviceService.register(did, Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, deviceToken).enqueue(new Callback<DeviceRegistered>() {
            @Override
            public void onResponse(@NonNull Call<DeviceRegistered> call, @NonNull Response<DeviceRegistered> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "Registered device " + String.valueOf(response.body()));
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
     *
     * @param did
     */
    public void isSupportedAppVersion(String did) {
        deviceService.isSupportedAppVersion(did, Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, Constants.appVersion()).enqueue(new Callback<JsonLatestAppVersion>() {
            @Override
            public void onResponse(@NonNull Call<JsonLatestAppVersion> call, @NonNull Response<JsonLatestAppVersion> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    Log.d("response body issupport", response.body().toString());
                    if (null != response.body() && null == response.body().getError()) {
                        appBlacklistPresenter.appBlacklistResponse(response.body());
                    } else {
                        appBlacklistPresenter.appBlacklistError(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        appBlacklistPresenter.authenticationFailure();
                    } else {
                        appBlacklistPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonLatestAppVersion> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure Response " + t.getLocalizedMessage(), t);
                appBlacklistPresenter.appBlacklistError(null);
            }
        });
    }

}
