package com.noqapp.android.client.model.response.api;

import com.noqapp.android.common.beans.JsonCouponList;
import com.noqapp.android.common.beans.body.CouponOnOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 2019-06-10 13:33
 */
public interface CouponApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#PROMOTION_ACCESS_DENIED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/c/coupon/available.json")
    Call<JsonCouponList> availableCoupon(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    @POST("api/c/coupon/apply.json")
    Call<JsonPurchaseOrder> apply(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            CouponOnOrder couponOnOrder
    );
}
