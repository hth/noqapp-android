package com.noqapp.android.client.model;

import android.util.Log;

import com.noqapp.android.client.model.response.api.health.UserMedicalProfileApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.MedicalRecordProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.UserMedicalProfile;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.medical.JsonMedicalProfile;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMedicalProfileApiCall {
    private final static UserMedicalProfileApiUrls userMedicalProfileApiUrls;
    private MedicalRecordProfilePresenter medicalRecordProfilePresenter;

    public void setMedicalRecordProfilePresenter(MedicalRecordProfilePresenter medicalRecordProfilePresenter) {
        this.medicalRecordProfilePresenter = medicalRecordProfilePresenter;
    }

    static {
        userMedicalProfileApiUrls = RetrofitClient.getClient().create(UserMedicalProfileApiUrls.class);
    }

    public void medicalProfile(String mail, String auth, UserMedicalProfile userMedicalProfile) {
        userMedicalProfileApiUrls.profile(mail, auth, userMedicalProfile).enqueue(new Callback<JsonMedicalProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonMedicalProfile> call, @NonNull Response<JsonMedicalProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp history", String.valueOf(response.body()));
                        medicalRecordProfilePresenter.medicalRecordProfileResponse(response.body());
                    } else {
                        medicalRecordProfilePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        medicalRecordProfilePresenter.authenticationFailure();
                    } else {
                        medicalRecordProfilePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonMedicalProfile> call, @NonNull Throwable t) {
                Log.e("history fail", t.getLocalizedMessage(), t);
                medicalRecordProfilePresenter.responseErrorPresenter(null);
            }
        });
    }
}
