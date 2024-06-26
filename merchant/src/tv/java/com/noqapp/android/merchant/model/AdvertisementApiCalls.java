package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.JsonAdvertisementList;
import com.noqapp.android.common.beans.JsonProfessionalProfileTVList;
import com.noqapp.android.common.presenter.AdvertisementPresenter;
import com.noqapp.android.merchant.model.api.AdvertisementApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.ProfessionalProfilesPresenter;
import com.noqapp.android.merchant.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdvertisementApiCalls {
    private static final String TAG = AdvertisementApiCalls.class.getSimpleName();

    protected static final AdvertisementApiUrls advertisementApiUrls;
    private AdvertisementPresenter advertisementPresenter;
    private ProfessionalProfilesPresenter professionalProfilesPresenter;

    public void setAdvertisementPresenter(AdvertisementPresenter advertisementPresenter) {
        this.advertisementPresenter = advertisementPresenter;
    }

    public void setProfessionalProfilesPresenter(ProfessionalProfilesPresenter professionalProfilesPresenter) {
        this.professionalProfilesPresenter = professionalProfilesPresenter;
    }

    static {
        advertisementApiUrls = RetrofitClient.getClient().create(AdvertisementApiUrls.class);
    }

    public void getAllAdvertisements(String did, String mail, String auth) {
        advertisementApiUrls.getAllAdvertisements(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonAdvertisementList>() {
            @Override
            public void onResponse(@NonNull Call<JsonAdvertisementList> call, @NonNull Response<JsonAdvertisementList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        advertisementPresenter.advertisementResponse(response.body());
                        Log.d("getAllAdvertisements", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty all Advertisements");
                        advertisementPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        advertisementPresenter.authenticationFailure();
                    } else {
                        advertisementPresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonAdvertisementList> call, @NonNull Throwable t) {
                Log.e("onFailure Advt", t.getLocalizedMessage(), t);
                advertisementPresenter.responseErrorPresenter(null);
            }
        });
    }



    public void professionalProfiles(String did, String mail, String auth) {
        advertisementApiUrls.professionalProfiles(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonProfessionalProfileTVList>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfessionalProfileTVList> call, @NonNull Response<JsonProfessionalProfileTVList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        professionalProfilesPresenter.professionalProfilesResponse(response.body());
                        Log.d("professionalProfiles", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty professionalProfiles");
                        professionalProfilesPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        professionalProfilesPresenter.authenticationFailure();
                    } else {
                        professionalProfilesPresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfessionalProfileTVList> call, @NonNull Throwable t) {
                Log.e("onFailure profProfiles", t.getLocalizedMessage(), t);
                professionalProfilesPresenter.responseErrorPresenter(null);
            }
        });
    }
}
