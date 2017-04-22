package com.noqapp.merchant.model.response.api;

import com.noqapp.merchant.presenter.beans.JsonToken;
import com.noqapp.merchant.presenter.beans.JsonTopicList;
import com.noqapp.merchant.presenter.beans.body.Served;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 4/16/17 5:40 PM
 */

public interface ManageQueueService {

    @GET("api/m/mq/queues.json")
    Call<JsonTopicList> getQueues(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    @POST("api/m/mq/served")
    Call<JsonToken> served(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            Served served
    );
}
