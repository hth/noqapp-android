package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by chandra on 3/22/18.
 */
public interface Search {

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @GET("open/search")
    Call<SearchStoreQuery> search(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt
    );

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
    @POST("open/search/business")
    Call<BizStoreElasticList> business(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Body
        SearchStoreQuery searchStoreQuery
    );
}
