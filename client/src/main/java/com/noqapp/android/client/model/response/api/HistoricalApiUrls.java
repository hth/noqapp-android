package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistoricalList;
import com.noqapp.android.client.presenter.beans.JsonQueueHistoricalList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface HistoricalApiUrls {

    @GET("api/c/historical/orders")
    Call<JsonPurchaseOrderHistoricalList> orders(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    @GET("api/c/historical/queues")
    Call<JsonQueueHistoricalList> queues(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );
}
