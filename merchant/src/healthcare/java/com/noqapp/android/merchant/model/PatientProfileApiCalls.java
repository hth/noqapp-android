package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.merchant.interfaces.PatientProfilePresenter;
import com.noqapp.android.merchant.model.response.api.health.MedicalUserProfileApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.body.merchant.FindMedicalProfile;
import com.noqapp.android.merchant.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientProfileApiCalls {
    private static final String TAG = PatientProfileApiCalls.class.getSimpleName();

    private static final MedicalUserProfileApiUrls medicalUserProfileApiUrls;
    private PatientProfilePresenter patientProfilePresenter;

    public PatientProfileApiCalls(PatientProfilePresenter patientProfilePresenter) {
        this.patientProfilePresenter = patientProfilePresenter;
    }

    static {
        medicalUserProfileApiUrls = RetrofitClient.getClient().create(MedicalUserProfileApiUrls.class);
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public void fetch(String did, String mail, String auth, FindMedicalProfile findMedicalProfile) {
        medicalUserProfileApiUrls.fetch(did, Constants.DEVICE_TYPE, mail, auth, findMedicalProfile).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("medical profile fetch", String.valueOf(response.body()));
                        patientProfilePresenter.patientProfileResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch fetch profile");
                        patientProfilePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        patientProfilePresenter.authenticationFailure();
                    } else {
                        patientProfilePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("failureMedicalProfileFe", t.getLocalizedMessage(), t);
                patientProfilePresenter.patientProfileError();
            }
        });
    }

}
