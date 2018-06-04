package com.noqapp.android.client.model.response.open;

import com.noqapp.library.beans.JsonHealthCareProfile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * User: hitender
 * Date: 6/4/18 11:00 AM
 */
public interface HealthCareProfileService {

    @GET("open/healthCare/profile/{codeQR}.json")
    Call<JsonHealthCareProfile> getAllQueueState(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Path("codeQR")
            String codeQR
    );

}
