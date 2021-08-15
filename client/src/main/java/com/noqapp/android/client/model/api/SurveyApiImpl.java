package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.SurveyApi;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.SurveyPresenter;
import com.noqapp.android.client.presenter.beans.JsonQuestionnaire;
import com.noqapp.android.client.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyApiImpl {

    private final String TAG = SurveyApiImpl.class.getSimpleName();
    private static final SurveyApi SURVEY_API;
    private SurveyPresenter surveyPresenter;

    public SurveyApiImpl(SurveyPresenter surveyPresenter) {
        this.surveyPresenter = surveyPresenter;
    }

    static {
        SURVEY_API = RetrofitClient.getClient().create(SurveyApi.class);
    }

    public void survey(String mail, String auth) {
        SURVEY_API.survey(mail, auth).enqueue(new Callback<JsonQuestionnaire>() {
            @Override
            public void onResponse(@NonNull Call<JsonQuestionnaire> call, @NonNull Response<JsonQuestionnaire> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG,"Response survey " + response.body());
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
                Log.e(TAG, "Failure survey " + t.getLocalizedMessage(), t);
                surveyPresenter.responseErrorPresenter(null);
            }
        });
    }
}
