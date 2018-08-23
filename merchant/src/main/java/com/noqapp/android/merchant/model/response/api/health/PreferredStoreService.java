package com.noqapp.android.merchant.model.response.api.health;

import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * User: hitender
 * Date: 8/13/18 3:55 PM
 */
public interface PreferredStoreService {

    @GET("api/m/h/preferredStore/{businessType}/{codeQR}.json")
    Call<JsonPreferredBusinessList> getPreferredStoresByBusinessType(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("businessType")
            BusinessTypeEnum businessType,

            @Path("codeQR")
            String codeQR
    );

    @GET("api/m/h/preferredStore/{codeQR}.json")
    Call<JsonPreferredBusinessList> getAllPreferredStores(
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

    @GET("api/m/h/preferredStore/file/{bizStoreId}.json")
    void file(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("bizStoreId")
            String bizStoreId
    );
}
