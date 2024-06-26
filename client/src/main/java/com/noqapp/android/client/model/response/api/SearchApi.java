package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchQuery;
import com.noqapp.android.common.beans.marketplace.MarketplaceElasticList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SearchApi {

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @GET("api/c/search")
    Call<SearchQuery> search(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth
    );

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("api/c/search")
    Call<BizStoreElasticList> search(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            SearchQuery searchQuery
    );

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("api/c/search/business")
    Call<BizStoreElasticList> business(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        SearchQuery searchQuery
    );

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("api/c/search/marketplace")
    Call<MarketplaceElasticList> marketplace(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        SearchQuery searchQuery
    );
}
