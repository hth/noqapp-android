package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.body.ReviewRating;
import com.noqapp.common.beans.JsonResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 5/7/17 12:40 PM
 */
public interface ReviewService {
    @POST("open/review/service.json")
    Call<JsonResponse> review(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            ReviewRating reviewRating
    );

    @POST("open/review/historical/service.json")
    Call<JsonResponse> reviewHistorical(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            ReviewRating reviewRating
    );
}
