package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.model.response.api.FeedbackApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.beans.body.Feedback;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.FeedbackPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackApiCall {
    private final String TAG = RegisterApiCall.class.getSimpleName();
    private static final FeedbackApiUrls feedbackApiUrls;
    private FeedbackPresenter feedbackPresenter;

    public FeedbackApiCall(FeedbackPresenter feedbackPresenter) {
        this.feedbackPresenter = feedbackPresenter;
    }

    static {
        feedbackApiUrls = RetrofitClient.getClient().create(FeedbackApiUrls.class);
    }

    public void review(String did, String mail, String auth, Feedback feedback) {
        feedbackApiUrls.review(did, Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, mail, auth, feedback).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
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
