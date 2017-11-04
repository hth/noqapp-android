package com.noqapp.android.client.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.client.model.response.open.DeviceService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.beans.DeviceRegistered;
import com.noqapp.android.client.presenter.beans.body.AppVersionCheck;
import com.noqapp.android.client.presenter.beans.body.DeviceToken;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.views.interfaces.AppBlacklistPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        deviceService.register(did, Constants.DEVICE_TYPE, deviceToken).enqueue(new Callback<DeviceRegistered>() {
            @Override
            public void onResponse(@NonNull Call<DeviceRegistered> call, @NonNull Response<DeviceRegistered> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history" + response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeviceRegistered> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }

    /**
     * Check is current app version is supported.
     *
     * @param did
     */
    public static void isSupportedAppVersion(String did) {
        deviceService.isSupportedAppVersion(did, Constants.DEVICE_TYPE, Constants.appVersion()).enqueue(new Callback<AppVersionCheck>() {
            @Override
            public void onResponse(@NonNull Call<AppVersionCheck> call, @NonNull Response<AppVersionCheck> response) {

                if (null != response.body() && null != response.body().getError()) {
                    Log.d("Get queue setting", String.valueOf(response.body()));
                    appBlacklistPresenter.appBlacklistError();
                } else {
                    appBlacklistPresenter.appBlacklistResponse();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppVersionCheck> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                appBlacklistPresenter.appBlacklistResponse();
            }
        });
    }

}
