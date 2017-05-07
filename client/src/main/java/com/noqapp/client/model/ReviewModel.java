package com.noqapp.client.model;

import android.util.Log;

import com.noqapp.client.model.response.open.RegisterService;
import com.noqapp.client.model.response.open.ReviewService;
import com.noqapp.client.network.RetrofitClient;
import com.noqapp.client.presenter.ProfilePresenter;
import com.noqapp.client.presenter.beans.JsonProfile;
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

    static {
        reviewService = RetrofitClient.getClient(RetrofitClient.BaseURL).create(ReviewService.class);
    }

    /**
     *
     * @param did
     * @param reviewRating
     */
    public static void review(String did, ReviewRating reviewRating) {
        reviewService.review(did, DEVICE_TYPE, reviewRating).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(Call<JsonProfile> call, Response<JsonProfile> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonProfile> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }

    /**
     *
     * @param did
     * @param reviewRating
     */
    public static void reviewHistorical(String did, ReviewRating reviewRating) {
        reviewService.reviewHistorical(did, DEVICE_TYPE, reviewRating).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(Call<JsonProfile> call, Response<JsonProfile> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonProfile> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }

}
