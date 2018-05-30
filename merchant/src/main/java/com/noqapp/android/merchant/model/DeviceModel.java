package com.noqapp.android.merchant.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.merchant.model.response.api.DeviceService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.DeviceRegistered;
import com.noqapp.android.merchant.presenter.beans.body.DeviceToken;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.AppBlacklistPresenter;
import com.noqapp.library.beans.JsonLatestAppVersion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.merchant.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 4/2/17 6:40 PM
 */
public class DeviceModel {
    private static final String TAG = DeviceModel.class.getSimpleName();

    private static final DeviceService deviceService;
    public static AppBlacklistPresenter appBlacklistPresenter;

    static {
        deviceService = RetrofitClient.getClient().create(DeviceService.class);
    }

    /**
     * Register device.
     *
     * @param did
     * @param deviceToken
     */
    public static void register(String did, DeviceToken deviceToken) {
        deviceService.register(did, DEVICE_TYPE, deviceToken).enqueue(new Callback<DeviceRegistered>() {
            @Override
            public void onResponse(@NonNull Call<DeviceRegistered> call, @NonNull Response<DeviceRegistered> response) {
                if (response.body() != null) {
                    Log.d(TAG, "Registered device " + String.valueOf(response.body()));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty body");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeviceRegistered> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure Response " + t.getLocalizedMessage(), t);
            }
        });
    }

    /**
     * Check is current app version is supported.
     *
     * @param did
     */
    public static void isSupportedAppVersion(String did) {
        deviceService.isSupportedAppVersion(did, DEVICE_TYPE, Constants.appVersion()).enqueue(new Callback<JsonLatestAppVersion>() {
            @Override
            public void onResponse(@NonNull Call<JsonLatestAppVersion> call, @NonNull Response<JsonLatestAppVersion> response) {

                if (null != response.body() && null != response.body().getError()) {
                    Log.d(TAG, "Oldest supported version " + String.valueOf(response.body()));
                    appBlacklistPresenter.appBlacklistError();
                } else {
                    appBlacklistPresenter.appBlacklistResponse();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonLatestAppVersion> call, @NonNull Throwable t) {
                Log.e(TAG, "Failure Response " + t.getLocalizedMessage(), t);
                appBlacklistPresenter.appBlacklistResponse();
            }
        });
    }
}
