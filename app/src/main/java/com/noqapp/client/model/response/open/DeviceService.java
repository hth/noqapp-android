package com.noqapp.client.model.response.open;

import com.noqapp.client.presenter.beans.DeviceRegistered;
import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.presenter.beans.body.DeviceToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * User: omkar
 * Date: 4/2/17 6:37 PM
 */
public interface DeviceService {

    @POST("open/device/register.json")
    Call<DeviceRegistered> register(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            DeviceToken deviceToken
    );
}
