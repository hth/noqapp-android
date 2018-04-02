package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.body.PurchaseOrderParam;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PurchaseOrderService {

    @POST("api/c/purchaseOrder/purchase.json")
    Call<JsonResponse> placeOrder(
            @Header("X-R-DID")
                    String did,

            @Header("X-R-DT")
                    String dt,

            @Header("X-R-MAIL")
                    String mail,

            @Header("X-R-AUTH")
                    String auth,

            @Body
            PurchaseOrderParam purchaseOrderParam
    );

}
