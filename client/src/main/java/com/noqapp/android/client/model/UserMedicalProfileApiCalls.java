package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.health.UserMedicalProfileApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.MedicalRecordProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.MedicalProfile;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.beans.medical.JsonMedicalProfile;
import com.noqapp.android.common.presenter.HospitalVisitSchedulePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMedicalProfileApiCalls {
    private final static UserMedicalProfileApiUrls userMedicalProfileApiUrls;
    private MedicalRecordProfilePresenter medicalRecordProfilePresenter;
    private HospitalVisitSchedulePresenter hospitalVisitSchedulePresenter;

    public void setHospitalVisitSchedulePresenter(HospitalVisitSchedulePresenter hospitalVisitSchedulePresenter) {
        this.hospitalVisitSchedulePresenter = hospitalVisitSchedulePresenter;
    }

    public void setMedicalRecordProfilePresenter(MedicalRecordProfilePresenter medicalRecordProfilePresenter) {
        this.medicalRecordProfilePresenter = medicalRecordProfilePresenter;
    }

    static {
        userMedicalProfileApiUrls = RetrofitClient.getClient().create(UserMedicalProfileApiUrls.class);
    }

    public void updateUserMedicalProfile(String mail, String auth, MedicalProfile medicalProfile) {
        userMedicalProfileApiUrls.updateUserMedicalProfile(mail, auth, medicalProfile).enqueue(new Callback<JsonMedicalProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonMedicalProfile> call, @NonNull Response<JsonMedicalProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
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

    public void medicalProfile(String mail, String auth, MedicalProfile medicalProfile) {
        userMedicalProfileApiUrls.profile(mail, auth, medicalProfile).enqueue(new Callback<JsonMedicalProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonMedicalProfile> call, @NonNull Response<JsonMedicalProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
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

    public void hospitalVisitSchedule(String mail, String auth, MedicalProfile medicalProfile) {
        userMedicalProfileApiUrls.hospitalVisitSchedule(mail, auth, medicalProfile).enqueue(new Callback<JsonHospitalVisitScheduleList>() {
            @Override
            public void onResponse(@NonNull Call<JsonHospitalVisitScheduleList> call, @NonNull Response<JsonHospitalVisitScheduleList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp hospitalVSchedule", String.valueOf(response.body()));
                        hospitalVisitSchedulePresenter.hospitalVisitScheduleResponse(response.body());
                    } else {
                        hospitalVisitSchedulePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        hospitalVisitSchedulePresenter.authenticationFailure();
                    } else {
                        hospitalVisitSchedulePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonHospitalVisitScheduleList> call, @NonNull Throwable t) {
                Log.e("hospitalVSchedule fail", t.getLocalizedMessage(), t);
                hospitalVisitSchedulePresenter.responseErrorPresenter(null);
            }
        });
    }
}
