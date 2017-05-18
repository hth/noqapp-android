package com.noqapp.merchant.model;

import android.util.Log;


import com.noqapp.merchant.model.response.api.DeviceService;
import com.noqapp.merchant.network.RetrofitClient;
import com.noqapp.merchant.presenter.beans.DeviceRegistered;
import com.noqapp.merchant.presenter.beans.body.DeviceToken;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.noqapp.merchant.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 4/2/17 6:40 PM
 */

public class DeviceModel {
    private static final String TAG = DeviceModel.class.getSimpleName();

    private static final DeviceService deviceService;

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
            public void onResponse(Call<DeviceRegistered> call, Response<DeviceRegistered> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<DeviceRegistered> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }
}
