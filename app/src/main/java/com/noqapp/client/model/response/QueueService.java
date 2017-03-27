package com.noqapp.client.model.response;

import com.noqapp.client.presenter.Beans.JsonQueue;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * User: omkar
 * Date: 3/26/17 11:50 PM
 */
public interface QueueService {

    @GET("open/token/{codeQR}.json")
    Call<JsonQueue> getQueue(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Path("codeQR")
            String codeQR
    );
}
