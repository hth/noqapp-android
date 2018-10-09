package com.noqapp.android.client.model;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.model.response.api.FeedbackApiService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.beans.body.Feedback;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.FeedbackPresenter;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackApiModel {
    private final String TAG = RegisterModel.class.getSimpleName();
    private static final FeedbackApiService feedbackApiService;
    private FeedbackPresenter feedbackPresenter;

    public FeedbackApiModel(FeedbackPresenter feedbackPresenter) {
        this.feedbackPresenter = feedbackPresenter;
    }


    static {
        feedbackApiService = RetrofitClient.getClient().create(FeedbackApiService.class);
    }

    public void review(String did, String mail, String auth, Feedback feedback) {
        feedbackApiService.review(did, Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, mail, auth, feedback).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response feedback", String.valueOf(response.body()));
                        feedbackPresenter.feedbackResponse(response.body());
                    } else {
                        Log.e(TAG, "Error feedback" + response.body().getError());
                        feedbackPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        feedbackPresenter.authenticationFailure();
                    } else {
                        feedbackPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Failure feedback", t.getLocalizedMessage(), t);
                feedbackPresenter.responseErrorPresenter(null);
            }
        });
    }
}
