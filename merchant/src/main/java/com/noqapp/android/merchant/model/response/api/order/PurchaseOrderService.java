package com.noqapp.android.merchant.model.response.api.order;

import com.noqapp.android.common.fcm.data.JsonTopicOrderData;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.body.order.OrderServed;
import com.noqapp.android.merchant.presenter.beans.order.JsonPurchaseOrderList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PurchaseOrderService {

    @POST("api/m/o/purchaseOrder/showOrders/{codeQR}.json")
    Call<JsonPurchaseOrderList> fetch(
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

    @POST("api/m/o/purchaseOrder/served.json")
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
            OrderServed OrderServed
    );

    @POST("api/m/o/purchaseOrder/acquire.json")
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
            OrderServed OrderServed
    );

    @POST("api/m/o/purchaseOrder/processed.json")
    Call<JsonPurchaseOrderList> processed(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            OrderServed OrderServed
    );
}
