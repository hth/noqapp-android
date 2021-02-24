package com.noqapp.android.merchant.model.response.api.health;

import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessBucket;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * User: hitender
 * Date: 8/13/18 3:55 PM
 */
public interface PreferredStoreApiUrls {


    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/m/h/preferredStore/all")
    Call<JsonPreferredBusinessBucket> getAllPreferredStores(
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
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     */
    @GET("api/m/h/preferredStore/file/{codeQR}/{bizStoreId}")
    Call<ResponseBody> file(

            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("codeQR")
            String codeQR,

            @Path("bizStoreId")
            String bizStoreId
    );
}
