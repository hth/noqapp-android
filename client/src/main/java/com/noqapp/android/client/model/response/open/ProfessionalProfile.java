package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.JsonProfessionalProfile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * User: hitender
 * Date: 6/4/18 11:00 AM
 */
public interface ProfessionalProfile {

    /**
     * ERROR
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @GET("open/professional/profile/{webProfileId}")
    Call<JsonProfessionalProfile> profile(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Path("webProfileId")
        String webProfileId
    );

}
