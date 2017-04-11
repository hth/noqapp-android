package com.noqapp.client.model.response.open;

import com.noqapp.client.presenter.beans.JsonProfile;
import com.noqapp.client.presenter.beans.body.Registration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 4/8/17 8:29 PM
 */

public interface RegisterService {

    @POST("open/client/registration.json")
    Call<JsonProfile> register(
            @Body
            Registration registration
    );
}
