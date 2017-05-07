package com.noqapp.client.model.response.open;

import com.noqapp.client.presenter.beans.JsonProfile;
import com.noqapp.client.presenter.beans.body.Registration;
import com.noqapp.client.presenter.beans.body.ReviewRating;

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
    Call<JsonProfile> review(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            ReviewRating reviewRating
    );

    @POST("open/review/historical/service.json")
    Call<JsonProfile> reviewHistorical(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            ReviewRating reviewRating
    );
}
