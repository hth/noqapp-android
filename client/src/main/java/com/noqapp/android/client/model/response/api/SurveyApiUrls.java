package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.JsonQuestionnaire;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SurveyApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/c/survey")
    Call<JsonQuestionnaire> survey(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );
}
