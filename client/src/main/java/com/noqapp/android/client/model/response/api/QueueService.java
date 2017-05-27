package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonToken;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Registered client APIs.
 * //TODO
 * <p>
 * User: hitender
 * Date: 3/27/17 8:05 PM
 */
public interface QueueService {

    //TODO need to work on this
    @GET("api/c/token/{codeQR}.json")
    Call<JsonQueue> getQueueState(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("codeQR")
            String codeQR
    );

    @POST("api/c/token/remote/{codeQR}.json")
    Call<JsonQueue> remoteScanQueueState(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("codeQR")
            String codeQR
    );

    @POST("api/c/token/remote/queue/{codeQR}.json")
    Call<JsonToken> remoteJoinQueue(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("codeQR")
            String codeQR
    );
}
