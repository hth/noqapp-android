package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.body.Feedback;
import com.noqapp.android.common.beans.JsonResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FeedbackApi {

    @POST("api/c/feedback")
    Call<JsonResponse> review(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header ("X-R-AF")
            String appFlavor,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            Feedback feedback
    );
}
