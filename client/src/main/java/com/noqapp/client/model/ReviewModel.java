package com.noqapp.client.model;

import android.util.Log;

import com.noqapp.client.model.response.open.RegisterService;
import com.noqapp.client.model.response.open.ReviewService;
import com.noqapp.client.network.RetrofitClient;
import com.noqapp.client.presenter.ProfilePresenter;
import com.noqapp.client.presenter.ReviewPresenter;
import com.noqapp.client.presenter.beans.JsonProfile;
import com.noqapp.client.presenter.beans.JsonResponse;
import com.noqapp.client.presenter.beans.body.Registration;
import com.noqapp.client.presenter.beans.body.ReviewRating;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.client.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 5/7/17 12:39 PM
 */

public class ReviewModel {
    private static final String TAG = RegisterModel.class.getSimpleName();

    private static final ReviewService reviewService;
    public static ReviewPresenter reviewPresenter;
    static {
        reviewService = RetrofitClient.getClient(RetrofitClient.BaseURL).create(ReviewService.class);
    }

    /**
     *
     * @param did
     * @param reviewRating
     */
    public static void review(String did, ReviewRating reviewRating) {
        reviewService.review(did, DEVICE_TYPE, reviewRating).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if (response.body() != null) {
                    Log.d("Response Review", String.valueOf(response.body()));
                    reviewPresenter.reviewResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                reviewPresenter.reviewError();
            }
        });
    }

    /**
     *
     * @param did
     * @param reviewRating
     */
    public static void reviewHistorical(String did, ReviewRating reviewRating) {
        reviewService.reviewHistorical(did, DEVICE_TYPE, reviewRating).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }

}
