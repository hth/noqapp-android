package com.noqapp.android.merchant.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.merchant.model.response.api.health.MedicalRecordService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalHistoryModel {
    private static final String TAG = MedicalHistoryModel.class.getSimpleName();

    private static final MedicalRecordService medicalRecordService;
    private MedicalRecordPresenter medicalRecordPresenter;

    public MedicalHistoryModel(MedicalRecordPresenter medicalRecordPresenter) {
        this.medicalRecordPresenter = medicalRecordPresenter;
    }

    static {
        medicalRecordService = RetrofitClient.getClient().create(MedicalRecordService.class);
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public void add(String did, String mail, String auth, JsonMedicalRecord jsonMedicalRecord) {
        medicalRecordService.add(did, Constants.DEVICE_TYPE, mail, auth, jsonMedicalRecord).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == 401) {
                    medicalRecordPresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    medicalRecordPresenter.medicalRecordResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed to add");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                medicalRecordPresenter.medicalRecordError();
            }
        });
    }

}
