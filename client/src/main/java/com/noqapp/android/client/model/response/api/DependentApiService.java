package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.common.beans.JsonProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Add dependents APIs.
 * <p>
 * User: hitender
 * Date: 6/20/18 12:35 PM
 */
public interface DependentApiService {

    @POST("api/c/dependent/add.json")
    Call<JsonProfile> add(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            Registration registration
    );
}
