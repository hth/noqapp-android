package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.merchant.presenter.beans.JsonCouponList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CouponOnOrder;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    @GET("api/m/coupon/available/{codeQR}.json")
    Call<JsonCouponList> availableCoupon(
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
    
    @POST("api/m/coupon/apply.json")
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
