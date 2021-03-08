package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.merchant.views.pojos.Receipt;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ReceiptApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#PURCHASE_ORDER_NOT_FOUND}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_NOT_FOUND}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/receipt/detail")
    Call<Receipt> detail(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            Receipt receipt
    );
}
