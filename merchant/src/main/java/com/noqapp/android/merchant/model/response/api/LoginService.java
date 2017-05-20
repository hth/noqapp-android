package com.noqapp.android.merchant.model.response.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 4/22/17 1:02 PM
 */

public interface LoginService {

    @FormUrlEncoded
    @POST("login")
    Call<Void> login(
            @Field("mail")
            String mail,

            @Field("password")
            String password
    );
}
