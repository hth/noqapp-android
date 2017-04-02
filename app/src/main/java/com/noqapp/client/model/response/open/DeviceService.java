package com.noqapp.client.model.response.open;

import com.noqapp.client.presenter.beans.JsonQueue;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by omkar on 4/2/17.
 */

public interface DeviceService {
    @POST("open/device/register.json")
    Call<JsonQueue> getQueueState(
            @Header("X-R-DID")
                    String did,

            @Header("X-R-DT")
                    String dt

//            @Body("")
//                    String codeQR
    );
}
