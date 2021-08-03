package com.noqapp.android.client.model.open;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.open.SurveyResponse;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.beans.body.Survey;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyResponseImpl {

    private final String TAG = SurveyResponseImpl.class.getSimpleName();
    private static final SurveyResponse SURVEY_RESPONSE;
    private ResponsePresenter responsePresenter;

    public SurveyResponseImpl(ResponsePresenter responsePresenter) {
        this.responsePresenter = responsePresenter;
    }

    static {
        SURVEY_RESPONSE = RetrofitClient.getClient().create(SurveyResponse.class);
    }

    public void surveyResponse(String did, Survey survey) {
        SURVEY_RESPONSE.surveyResponse(did, Constants.DEVICE_TYPE, survey).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response surveyResponse", String.valueOf(response.body()));
                        responsePresenter.responsePresenterResponse(response.body());
                    } else {
                        Log.e(TAG, "Error surveyResponse " + response.body().getError());
                        responsePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        responsePresenter.authenticationFailure();
                    } else {
                        responsePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Failure surveyResponse ", t.getLocalizedMessage(), t);
                responsePresenter.responseErrorPresenter(null);
            }
        });
    }
}
