package com.noqapp.client.model;

import android.util.Log;

import com.noqapp.client.model.response.open.DeviceService;
import com.noqapp.client.network.RetrofitClient;
import com.noqapp.client.presenter.beans.DeviceRegistered;
import com.noqapp.client.presenter.beans.body.DeviceToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.client.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 4/2/17 6:40 PM
 */

public class DeviceModel {
    private static final DeviceService deviceService;

    static {
        deviceService = RetrofitClient.getClient(RetrofitClient.BaseURL).create(DeviceService.class);
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
            public void onResponse(Call<DeviceRegistered> call, Response<DeviceRegistered> response) {
                Log.d("Response", String.valueOf(response.body()));
            }

            @Override
            public void onFailure(Call<DeviceRegistered> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }
}
