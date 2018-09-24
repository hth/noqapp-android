package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.merchant.interfaces.PatientProfilePresenter;
import com.noqapp.android.merchant.model.response.api.health.MedicalUserProfileService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;
import com.noqapp.android.merchant.utils.Constants;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientProfileModel {
    private static final String TAG = PatientProfileModel.class.getSimpleName();

    private static final MedicalUserProfileService medicalUserProfileService;
    private PatientProfilePresenter patientProfilePresenter;

    public PatientProfileModel(PatientProfilePresenter patientProfilePresenter) {
        this.patientProfilePresenter = patientProfilePresenter;
    }

    static {
        medicalUserProfileService = RetrofitClient.getClient().create(MedicalUserProfileService.class);
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public void fetch(String did, String mail, String auth, FindMedicalProfile findMedicalProfile) {
        medicalUserProfileService.fetch(did, Constants.DEVICE_TYPE, mail, auth, findMedicalProfile).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    patientProfilePresenter.authenticationFailure();
                    return;
                }
                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Response", String.valueOf(response.body()));
                    patientProfilePresenter.patientProfileResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed to fetch patient profile");
                    patientProfilePresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                patientProfilePresenter.patientProfileError();
            }
        });
    }

}
