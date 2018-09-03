package com.noqapp.android.client.model;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

import com.noqapp.android.client.model.response.open.ReviewService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.interfaces.ReviewPresenter;
import com.noqapp.android.client.presenter.beans.body.ReviewRating;
import com.noqapp.android.common.beans.JsonResponse;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 5/7/17 12:39 PM
 */

public class ReviewModel {
    private final String TAG = RegisterModel.class.getSimpleName();
    private static final ReviewService reviewService;
    private ReviewPresenter reviewPresenter;

    public ReviewModel(ReviewPresenter reviewPresenter) {
        this.reviewPresenter = reviewPresenter;
    }


    static {
        reviewService = RetrofitClient.getClient().create(ReviewService.class);
    }

    /**
     * @param did
     * @param reviewRating
     */
    public void review(String did, ReviewRating reviewRating) {
        reviewService.review(did, DEVICE_TYPE, reviewRating).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.body() != null) {
                    Log.d("Response Review", String.valueOf(response.body()));
                    reviewPresenter.reviewResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                reviewPresenter.reviewError();
            }
        });
    }
}
