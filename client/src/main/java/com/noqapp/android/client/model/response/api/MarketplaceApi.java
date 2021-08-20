package com.noqapp.android.client.model.response.api;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.marketplace.JsonMarketplace;
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic;
import com.noqapp.android.common.beans.marketplace.MarketplaceElasticList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MarketplaceApi {

    /**
     * Errors
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @GET("api/c/marketplace")
    Call<MarketplaceElasticList> showMyPostOnMarketplace(
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
     * Create new post on marketplace
     *
     * Errors
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @POST("api/c/marketplace")
    Call<MarketplaceElastic> postOnMarketplace(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        JsonMarketplace jsonMarketplace
    );

    /**
     * On clicking view detail on the post.
     *
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE}
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @POST("api/c/view")
    Call<JsonResponse> viewMarketplace(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        JsonMarketplace jsonMarketplace
    );

    /**
     * Person should be able to do it once. After that the button should be disabled.
     *
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE}
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @POST("api/c/initiateContact")
    Call<JsonResponse> initiateContact(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        JsonMarketplace jsonMarketplace
    );
}
