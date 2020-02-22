package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.common.presenter.AllReviewPresenter;
import com.noqapp.android.merchant.model.response.open.ReviewApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewApiUnAuthenticCalls {
    private final String TAG = ReviewApiUnAuthenticCalls.class.getSimpleName();
    private static final ReviewApiUrls reviewApiUrls;
    private AllReviewPresenter allReviewPresenter;
    public void setAllReviewPresenter(AllReviewPresenter allReviewPresenter) {
        this.allReviewPresenter = allReviewPresenter;
    }

    static {
        reviewApiUrls = RetrofitClient.getClient().create(ReviewApiUrls.class);
    }


    public void review(String did, String codeQR) {
        reviewApiUrls.review(did, Constants.DEVICE_TYPE, codeQR).enqueue(new Callback<JsonReviewList>() {
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

}

