package com.noqapp.android.client.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.client.model.response.api.health.MedicalRecordService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.MedicalRecordPresenter;
import com.noqapp.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.client.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MedicalRecordApiModel {
    private static final String TAG = MedicalRecordApiModel.class.getSimpleName();

    private final static MedicalRecordService medicalRecordService;
    public static MedicalRecordPresenter medicalRecordPresenter;

    static {
        medicalRecordService = RetrofitClient.getClient().create(MedicalRecordService.class);
    }

    public static void getMedicalRecord(String mail, String auth) {
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
