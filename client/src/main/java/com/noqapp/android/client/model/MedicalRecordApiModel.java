package com.noqapp.android.client.model;

import com.noqapp.android.client.model.response.api.health.MedicalRecordService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.MedicalRecordPresenter;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MedicalRecordApiModel {
    private final String TAG = MedicalRecordApiModel.class.getSimpleName();
    private final static MedicalRecordService medicalRecordService;
    private MedicalRecordPresenter medicalRecordPresenter;

    public MedicalRecordApiModel(MedicalRecordPresenter medicalRecordPresenter) {
        this.medicalRecordPresenter = medicalRecordPresenter;
    }

    static {
        medicalRecordService = RetrofitClient.getClient().create(MedicalRecordService.class);
    }

    public void getMedicalRecord(String mail, String auth) {
        medicalRecordService.getMedicalRecord(mail, auth).enqueue(new Callback<JsonMedicalRecordList>() {
            @Override
            public void onResponse(@NonNull Call<JsonMedicalRecordList> call, @NonNull Response<JsonMedicalRecordList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    medicalRecordPresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    medicalRecordPresenter.medicalRecordResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Get state of queue upon scan");
                    medicalRecordPresenter.medicalRecordError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonMedicalRecordList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                medicalRecordPresenter.medicalRecordError();
            }
        });
    }
}
