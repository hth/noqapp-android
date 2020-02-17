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
    @POST("open/search.json")
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
    @POST("open/search.json")
    Call<BizStoreElasticList> searchBizItem(
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
    @POST("open/search/healthCare.json")
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
    @POST("open/search/otherMerchant.json")
    Call<BizStoreElasticList> otherMerchant(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            SearchStoreQuery searchStoreQuery
    );
}
