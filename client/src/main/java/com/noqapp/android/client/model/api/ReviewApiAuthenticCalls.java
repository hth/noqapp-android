package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.ReviewApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ReviewPresenter;
import com.noqapp.android.client.presenter.beans.body.OrderReview;
import com.noqapp.android.client.presenter.beans.body.QueueReview;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewApiAuthenticCalls {
    private final String TAG = ReviewApiAuthenticCalls.class.getSimpleName();
    private static final ReviewApiUrls reviewApiUrls;
    private ReviewPresenter reviewPresenter;

    public ReviewApiAuthenticCalls(ReviewPresenter reviewPresenter) {
        this.reviewPresenter = reviewPresenter;
    }


    static {
        reviewApiUrls = RetrofitClient.getClient().create(ReviewApiUrls.class);
    }

    /**
     * @param did
     * @param queueReview
     */
    public void queue(String did, String mail, String auth, QueueReview queueReview) {
        reviewApiUrls.queue(did, Constants.DEVICE_TYPE, mail, auth, queueReview).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response queue Review", String.valueOf(response.body()));
                        reviewPresenter.reviewResponse(response.body());
                    } else {
                        Log.e(TAG, "Error queue review " + response.body().getError());
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
                Log.e("Failure queue review ", t.getLocalizedMessage(), t);
                reviewPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void order(String did, String mail, String auth, OrderReview orderReview) {
        reviewApiUrls.order(did, Constants.DEVICE_TYPE, mail, auth, orderReview).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response orderReview", String.valueOf(response.body()));
                        reviewPresenter.reviewResponse(response.body());
                    } else {
                        Log.e(TAG, "Error order review " + response.body().getError());
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
                Log.e("Failure order review ", t.getLocalizedMessage(), t);
                reviewPresenter.responseErrorPresenter(null);
            }
        });
    }
}
