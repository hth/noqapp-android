package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.body.Login;
import com.noqapp.android.client.presenter.beans.body.Registration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 4/8/17 8:29 PM
 */

public interface RegisterService {

    @POST("open/client/registration.json")
    Call<JsonProfile> register(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            Registration registration
    );

    @POST("open/client/login.json")
    Call<JsonProfile> login(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            Login login
    );
}
