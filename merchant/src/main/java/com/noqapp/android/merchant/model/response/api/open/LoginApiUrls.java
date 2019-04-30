package com.noqapp.android.merchant.model.response.api.open;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 4/22/17 1:02 PM
 */

public interface LoginApiUrls {

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_NOT_FOUND}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_INPUT}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @FormUrlEncoded
    @POST("login")
    Call<Void> login(
        @Field("mail")
        String mail,

        @Field("password")
        String password
    );
}
