package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.merchant.model.response.api.health.MedicalRecordService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordListPresenter;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;
import com.noqapp.android.merchant.utils.Constants;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalHistoryModel {
    private static final String TAG = MedicalHistoryModel.class.getSimpleName();

    private static final MedicalRecordService medicalRecordService;
    private MedicalRecordPresenter medicalRecordPresenter;
    private MedicalRecordListPresenter medicalRecordListPresenter;

    public MedicalHistoryModel(MedicalRecordListPresenter medicalRecordListPresenter) {
        this.medicalRecordListPresenter = medicalRecordListPresenter;
    }

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
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    medicalRecordPresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    medicalRecordPresenter.medicalRecordResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed to add");
                    medicalRecordPresenter.medicalRecordError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                medicalRecordPresenter.medicalRecordError();
            }
        });
    }


    public void fetch(String did, String mail, String auth, FindMedicalProfile findMedicalProfile) {
        medicalRecordService.fetch(did, Constants.DEVICE_TYPE, mail, auth, findMedicalProfile).enqueue(new Callback<JsonMedicalRecordList>() {
            @Override
            public void onResponse(@NonNull Call<JsonMedicalRecordList> call, @NonNull Response<JsonMedicalRecordList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    medicalRecordListPresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    medicalRecordListPresenter.medicalRecordListResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed to add");
                    medicalRecordListPresenter.medicalRecordListError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonMedicalRecordList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                medicalRecordListPresenter.medicalRecordListError();
            }
        });
    }

}
