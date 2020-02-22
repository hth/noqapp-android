package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.open.ReviewApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ReviewPresenter;
import com.noqapp.android.client.presenter.beans.body.QueueReview;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.common.presenter.AllReviewPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 5/7/17 12:39 PM
 */

public class ReviewApiUnAuthenticCall {
    private final String TAG = ReviewApiUnAuthenticCall.class.getSimpleName();
    private static final ReviewApiUrls reviewApiUrls;
    private ReviewPresenter reviewPresenter;
    private AllReviewPresenter allReviewPresenter;

    public void setReviewPresenter(ReviewPresenter reviewPresenter) {
        this.reviewPresenter = reviewPresenter;
    }

    public void setAllReviewPresenter(AllReviewPresenter allReviewPresenter) {
        this.allReviewPresenter = allReviewPresenter;
    }

    static {
        reviewApiUrls = RetrofitClient.getClient().create(ReviewApiUrls.class);
    }

    /**
     * @param did
     * @param queueReview
     */
    public void queue(String did, QueueReview queueReview) {
        reviewApiUrls.queue(did, DEVICE_TYPE, queueReview).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
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
                reviewPresenter.responseErrorPresenter(null);
            }
        });
    }


    public void review(String did, String codeQR) {
        reviewApiUrls.review(did, DEVICE_TYPE, codeQR).enqueue(new Callback<JsonReviewList>() {
            @Override
            public void onResponse(@NonNull Call<JsonReviewList> call, @NonNull Response<JsonReviewList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
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

    public void reviewsLevelUp(String did, String codeQR) {
        reviewApiUrls.reviewsLevelUp(did, DEVICE_TYPE, codeQR).enqueue(new Callback<JsonReviewList>() {
            @Override
            public void onResponse(@NonNull Call<JsonReviewList> call, @NonNull Response<JsonReviewList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp: all reviewLevelUp", String.valueOf(response.body()));
                        allReviewPresenter.allReviewResponse(response.body());
                    } else {
                        Log.e(TAG, "Error all reviewsLevelUp " + response.body().getError());
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
                Log.e("Failure reviewsLevelUp ", t.getLocalizedMessage(), t);
                allReviewPresenter.responseErrorPresenter(null);
            }
        });
    }
}
