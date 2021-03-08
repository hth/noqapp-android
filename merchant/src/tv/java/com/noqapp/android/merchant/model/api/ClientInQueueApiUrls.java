package com.noqapp.android.merchant.model.api;

import com.noqapp.android.merchant.presenter.beans.JsonQueueTVList;
import com.noqapp.android.merchant.presenter.beans.body.QueueDetail;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ClientInQueueApiUrls {

    @POST("api/tv/queue/toBeServedClients")
    Call<JsonQueueTVList> toBeServedClients(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            QueueDetail queueDetail
    );
}
