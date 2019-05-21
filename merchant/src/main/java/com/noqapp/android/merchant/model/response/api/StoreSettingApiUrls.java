package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.merchant.presenter.beans.body.StoreSetting;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by chandra on 7/15/17.
 */
public interface StoreSettingApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/m/ss/state/{codeQR}.json")
    Call<StoreSetting> getQueueState(
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

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/ss/removeSchedule/{codeQR}.json")
    Call<StoreSetting> removeSchedule(
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

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_ACTION_NOT_PERMITTED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/ss/modify.json")
    Call<StoreSetting> modify(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        StoreSetting storeSetting
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#PRODUCT_PRICE_CANNOT_BE_ZERO}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SERVICE_PAYMENT_NOT_ENABLED_FOR_THIS_BUSINESS_TYPE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/ss/serviceCost.json")
    Call<StoreSetting> serviceCost(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        StoreSetting storeSetting
    );
}
