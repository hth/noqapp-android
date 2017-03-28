package com.noqapp.client.model.response.api;

import com.noqapp.client.presenter.beans.JsonQueue;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Registered client APIs.
 * //TODO
 *
 * User: hitender
 * Date: 3/27/17 8:05 PM
 */
public interface QueueService {

    //TODO need to work on this
    @GET("api/token/{codeQR}.json")
    Call<JsonQueue> getQueueState(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Path("codeQR")
            String codeQR
    );
}
