package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by chandra on 3/22/18.
 */

public interface SearchBusinessStoreApiUrls {

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("open/search")
    Call<BizStoreElasticList> search(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            SearchStoreQuery searchStoreQuery
    );

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("open/search/kiosk")
    Call<BizStoreElasticList> kiosk(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            SearchStoreQuery searchStoreQuery
    );


    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("open/search/healthCare")
    Call<BizStoreElasticList> healthCare(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            SearchStoreQuery searchStoreQuery
    );

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("open/search/otherMerchant")
    Call<BizStoreElasticList> otherMerchant(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            SearchStoreQuery searchStoreQuery
    );

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("open/search/canteen")
    Call<BizStoreElasticList> canteen(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            SearchStoreQuery searchStoreQuery
    );

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("open/search/restaurant")
    Call<BizStoreElasticList> restaurants(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            SearchStoreQuery searchStoreQuery
    );

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("open/search/placeOfWorship")
    Call<BizStoreElasticList> placeOfWorship(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Body
        SearchStoreQuery searchStoreQuery
    );
}
