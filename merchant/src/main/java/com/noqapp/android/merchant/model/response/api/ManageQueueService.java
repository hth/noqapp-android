package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopicList;
import com.noqapp.android.merchant.presenter.beans.body.Served;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

            @Header("X-R-VR")
            String versionRelease,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    @POST("api/m/mq/served.json")
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

    @POST("api/m/mq/acquire.json")
    Call<JsonToken> acquire(
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

    @POST("api/m/mq/showQueuedClients/{codeQR}.json")
    Call<JsonQueuePersonList> getQueuePersonList(
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

    @POST("api/m/mq/dispenseToken/{codeQR}.json")
    Call<JsonToken> dispenseToken(
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
