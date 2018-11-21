package com.noqapp.android.merchant.model;

import static com.noqapp.android.merchant.utils.Constants.DEVICE_TYPE;

import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.JsonLatestAppVersion;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.model.response.api.DeviceService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.AppBlacklistPresenter;

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
        deviceService.register(did, DEVICE_TYPE, BuildConfig.APP_FLAVOR, deviceToken).enqueue(new Callback<DeviceRegistered>() {
            @Override
            public void onResponse(@NonNull Call<DeviceRegistered> call, @NonNull Response<DeviceRegistered> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
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
                Log.e(TAG, "register " + t.getLocalizedMessage(), t);
            }
        });
    }

    /**
     * Check is current app version is supported.
     *
     * @param did
     */
    public void isSupportedAppVersion(String did) {
        deviceService.isSupportedAppVersion(did, DEVICE_TYPE, BuildConfig.APP_FLAVOR, Constants.appVersion()).enqueue(new Callback<JsonLatestAppVersion>() {
            @Override
            public void onResponse(@NonNull Call<JsonLatestAppVersion> call, @NonNull Response<JsonLatestAppVersion> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null != response.body().getError()) {
                        Log.d(TAG, "Oldest supported version " + String.valueOf(response.body()));
                        appBlacklistPresenter.appBlacklistError();
                    } else {
                        appBlacklistPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    appBlacklistPresenter.responseErrorPresenter(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonLatestAppVersion> call, @NonNull Throwable t) {
                Log.e(TAG, "isSupportedAppVersion " + t.getLocalizedMessage(), t);
                appBlacklistPresenter.appBlacklistResponse();
            }
        });
    }
}
