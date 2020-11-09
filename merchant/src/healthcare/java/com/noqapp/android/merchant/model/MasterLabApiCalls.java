package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.MasterLabPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MasterLabApiCalls extends BaseMasterLabApiCalls {


    private MasterLabPresenter masterLabPresenter;


    public void setMasterLabPresenter(MasterLabPresenter masterLabPresenter) {
        this.masterLabPresenter = masterLabPresenter;
    }

    public void add(String did, String mail, String auth, JsonMasterLab jsonMasterLab) {
        masterLabApiUrls.add(did, Constants.DEVICE_TYPE, mail, auth, jsonMasterLab).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("medical profile fetch", String.valueOf(response.body()));
                        masterLabPresenter.masterLabUploadResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to fetch fetch profile");
                        masterLabPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        masterLabPresenter.authenticationFailure();
                    } else {
                        masterLabPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("failureMedicalProfileFe", t.getLocalizedMessage(), t);
                masterLabPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void flag(String did, String mail, String auth, JsonMasterLab jsonMasterLab) {
        masterLabApiUrls.flag(did, Constants.DEVICE_TYPE, mail, auth, jsonMasterLab).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("data flagged", String.valueOf(response.body()));
                        masterLabPresenter.masterLabUploadResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to flag data");
                        masterLabPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        masterLabPresenter.authenticationFailure();
                    } else {
                        masterLabPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("failure data flag", t.getLocalizedMessage(), t);
                masterLabPresenter.responseErrorPresenter(null);
            }
        });
    }


}
