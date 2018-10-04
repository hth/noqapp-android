package com.noqapp.android.client.model.response.api;

import com.noqapp.android.common.beans.order.JsonPurchaseOrderList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface HistoricalApiService {

    @GET("api/c/historical/orders.json")
    Call<JsonPurchaseOrderList> orders(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );
}
