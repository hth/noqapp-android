package com.noqapp.merchant.model.response.api;

import com.noqapp.merchant.presenter.beans.body.Authenticate;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 4/22/17 1:02 PM
 */

public interface LoginService {

    @POST("login")
    Call<Void> login(
            @Body
            Authenticate deviceToken
    );
}
