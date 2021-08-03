package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.body.Survey;
import com.noqapp.android.common.beans.JsonResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SurveyResponse {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("open/survey/response")
    Call<JsonResponse> surveyResponse(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            Survey survey
    );
}
