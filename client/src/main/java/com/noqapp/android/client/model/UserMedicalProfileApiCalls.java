package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.health.UserMedicalProfileApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ImmunizationHistoryPresenter;
import com.noqapp.android.client.presenter.MedicalRecordProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.MedicalProfile;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.medical.JsonImmunizationList;
import com.noqapp.android.common.beans.medical.JsonMedicalProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMedicalProfileApiCalls {
    private final static UserMedicalProfileApiUrls userMedicalProfileApiUrls;
    private MedicalRecordProfilePresenter medicalRecordProfilePresenter;
    private ImmunizationHistoryPresenter immunizationHistoryPresenter;

    public void setImmunizationHistoryPresenter(ImmunizationHistoryPresenter immunizationHistoryPresenter) {
        this.immunizationHistoryPresenter = immunizationHistoryPresenter;
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

    public void medicalProfile(String mail, String auth, MedicalProfile medicalProfile) {
        userMedicalProfileApiUrls.profile(mail, auth, medicalProfile).enqueue(new Callback<JsonMedicalProfile>() {
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

    public void immunizationHistory(String mail, String auth, MedicalProfile medicalProfile) {
        userMedicalProfileApiUrls.immunizationHistory(mail, auth,medicalProfile).enqueue(new Callback<JsonImmunizationList>() {
            @Override
            public void onResponse(@NonNull Call<JsonImmunizationList> call, @NonNull Response<JsonImmunizationList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp immunizationHis", String.valueOf(response.body()));
                        immunizationHistoryPresenter.immunizationHistoryResponse(response.body());
                    } else {
                        immunizationHistoryPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        immunizationHistoryPresenter.authenticationFailure();
                    } else {
                        immunizationHistoryPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonImmunizationList> call, @NonNull Throwable t) {
                Log.e("immunizationHis fail", t.getLocalizedMessage(), t);
                immunizationHistoryPresenter.responseErrorPresenter(null);
            }
        });
    }
}
