package com.noqapp.android.client.model.response.api;

import com.noqapp.android.common.beans.JsonProfile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Display case products APIs.
 * <p>
 * User: hitender
 * Date: 10/03/20 10:35 PM
 */
public interface DisplayCaseAPIUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     */
    @GET("api/c/display/{codeQR}.json")
    Call<JsonProfile> storeDisplayCase(
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
}
