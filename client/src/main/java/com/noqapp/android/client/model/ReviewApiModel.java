package com.noqapp.android.client.model;

import com.noqapp.android.client.model.response.api.ReviewApiService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ReviewPresenter;
import com.noqapp.android.client.presenter.beans.body.ReviewRating;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewApiModel {
    private final String TAG = RegisterModel.class.getSimpleName();
    private static final ReviewApiService reviewApiService;
    private ReviewPresenter reviewPresenter;

    public ReviewApiModel(ReviewPresenter reviewPresenter) {
        this.reviewPresenter = reviewPresenter;
    }


    static {
        reviewApiService = RetrofitClient.getClient().create(ReviewApiService.class);
    }

    /**
     * @param did
     * @param reviewRating
     */
    public void review(String did, String mail, String auth, ReviewRating reviewRating) {
        reviewApiService.review(did, Constants.DEVICE_TYPE, mail, auth, reviewRating).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response Review", String.valueOf(response.body()));
                        reviewPresenter.reviewResponse(response.body());
                    } else {
                        Log.e(TAG, "Error review" + response.body().getError());
                        reviewPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        reviewPresenter.authenticationFailure();
                    } else {
                        reviewPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Failure review", t.getLocalizedMessage(), t);
                reviewPresenter.reviewError();
            }
        });
    }
}
