package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.health.MedicalRecordApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.MedicalRecordPresenter;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MedicalRecordApiCall {
    private final static MedicalRecordApiUrls medicalRecordApiUrls;
    private MedicalRecordPresenter medicalRecordPresenter;
    public void setMedicalRecordPresenter(MedicalRecordPresenter medicalRecordPresenter) {
        this.medicalRecordPresenter = medicalRecordPresenter;
    }

    static {
        medicalRecordApiUrls = RetrofitClient.getClient().create(MedicalRecordApiUrls.class);
    }

    public void history(String mail, String auth) {
        medicalRecordApiUrls.history(mail, auth).enqueue(new Callback<JsonMedicalRecordList>() {
            @Override
            public void onResponse(@NonNull Call<JsonMedicalRecordList> call, @NonNull Response<JsonMedicalRecordList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp history", String.valueOf(response.body()));
                        medicalRecordPresenter.medicalRecordResponse(response.body());
                    } else {
                        medicalRecordPresenter.responseErrorPresenter(response.body().getError());
                    }
                }else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        medicalRecordPresenter.authenticationFailure();
                    } else{
                        medicalRecordPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonMedicalRecordList> call, @NonNull Throwable t) {
                Log.e("history fail", t.getLocalizedMessage(), t);
                medicalRecordPresenter.responseErrorPresenter(null);
            }
        });
    }
}
