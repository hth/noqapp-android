package com.noqapp.merchant.model.response.api;



import com.noqapp.merchant.presenter.beans.DeviceRegistered;
import com.noqapp.merchant.presenter.beans.body.DeviceToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

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
