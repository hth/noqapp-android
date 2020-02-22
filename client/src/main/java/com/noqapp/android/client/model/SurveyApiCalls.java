package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.SurveyApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.SurveyPresenter;
import com.noqapp.android.client.presenter.beans.JsonQuestionnaire;
import com.noqapp.android.client.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyApiCalls {

    private final String TAG = SurveyApiCalls.class.getSimpleName();
    private static final SurveyApiUrls surveyApiUrls;
    private SurveyPresenter surveyPresenter;

    public SurveyApiCalls(SurveyPresenter surveyPresenter) {
        this.surveyPresenter = surveyPresenter;
    }

    static {
        surveyApiUrls = RetrofitClient.getClient().create(SurveyApiUrls.class);
    }


    public void survey(String mail, String auth) {
        surveyApiUrls.survey(mail, auth).enqueue(new Callback<JsonQuestionnaire>() {
            @Override
            public void onResponse(@NonNull Call<JsonQuestionnaire> call, @NonNull Response<JsonQuestionnaire> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response survey", String.valueOf(response.body()));
                        surveyPresenter.surveyResponse(response.body());
                    } else {
                        Log.e(TAG, "Error survey " + response.body().getError());
                        surveyPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        surveyPresenter.authenticationFailure();
                    } else {
                        surveyPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQuestionnaire> call, @NonNull Throwable t) {
                Log.e("Failure survey ", t.getLocalizedMessage(), t);
                surveyPresenter.responseErrorPresenter(null);
            }
        });
    }
}
