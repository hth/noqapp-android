package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchQuery;
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
}
