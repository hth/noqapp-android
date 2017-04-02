package com.noqapp.client.model.response.open;

import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.presenter.beans.JsonResponse;
import com.noqapp.client.presenter.beans.JsonToken;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Unregistered client APIs.
 * <p>
 * User: omkar
 * Date: 3/26/17 11:50 PM
 */
public interface QueueService {

    @GET("open/token/{codeQR}.json")
    Call<JsonQueue> getQueueState(
            @Header("X-R-DID")
                    String did,

            @Header("X-R-DT")
                    String dt,

            @Path("codeQR")
                    String codeQR
    );

    @GET("open/token/queues.json")
    Call<List<JsonTokenAndQueue>> getAllJoinedQueue(
            @Header("X-R-DID")
                    String did,

            @Header("X-R-DT")
                    String dt
    );

    @GET("open/token/historical.json")
    Call<List<JsonTokenAndQueue>> getAllHistoricalJoinedQueue(
            @Header("X-R-DID")
                    String did,

            @Header("X-R-DT")
                    String dt
    );

    @POST("open/token/queue/{codeQR}.json")
    Call<JsonToken> joinQueue(
            @Header("X-R-DID")
                    String did,

            @Header("X-R-DT")
                    String dt,

            @Path("codeQR")
                    String codeQR
    );

    @POST("open/token/abort/{codeQR}.json")
    Call<JsonResponse> abortQueue(
            @Header("X-R-DID")
                    String did,

            @Header("X-R-DT")
                    String dt,

            @Path("codeQR")
                    String codeQR
    );
}
