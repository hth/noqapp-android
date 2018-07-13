package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.JsonPurchaseOrder;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PurchaseOrderService {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("api/c/purchaseOrder/purchase.json")
    Call<JsonPurchaseOrder> placeOrder(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonPurchaseOrder jsonPurchaseOrder
    );
}
