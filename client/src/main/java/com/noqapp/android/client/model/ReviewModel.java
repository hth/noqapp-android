package com.noqapp.android.client.model;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

import com.noqapp.android.client.model.response.open.ReviewService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.AllReviewPresenter;
import com.noqapp.android.client.presenter.ReviewPresenter;
import com.noqapp.android.client.presenter.beans.body.QueueReview;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonReviewList;

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
    private AllReviewPresenter allReviewPresenter;

    public void setReviewPresenter(ReviewPresenter reviewPresenter) {
        this.reviewPresenter = reviewPresenter;
    }

    public void setAllReviewPresenter(AllReviewPresenter allReviewPresenter) {
        this.allReviewPresenter = allReviewPresenter;
    }

    static {
        reviewService = RetrofitClient.getClient().create(ReviewService.class);
    }

    /**
     * @param did
     * @param queueReview
     */
    public void queue(String did, QueueReview queueReview) {
        reviewService.queue(did, DEVICE_TYPE, queueReview).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response Review", String.valueOf(response.body()));
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
                reviewPresenter.reviewError();
            }
        });
    }


    public void review(String did, String codeQR) {
        reviewService.review(did, DEVICE_TYPE, codeQR).enqueue(new Callback<JsonReviewList>() {
            @Override
            public void onResponse(@NonNull Call<JsonReviewList> call, @NonNull Response<JsonReviewList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response all Review", String.valueOf(response.body()));
                        allReviewPresenter.allReviewResponse(response.body());
                    } else {
                        Log.e(TAG, "Error all review " + response.body().getError());
                        allReviewPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        allReviewPresenter.authenticationFailure();
                    } else {
                        allReviewPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonReviewList> call, @NonNull Throwable t) {
                Log.e("Failure all review ", t.getLocalizedMessage(), t);
                allReviewPresenter.responseErrorPresenter(null);
            }
        });
    }
}
