package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.open.AdvertisementMobileApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.beans.body.Location;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonAdvertisementList;
import com.noqapp.android.common.presenter.AdvertisementPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdvertisementApiCalls {
    private static final String TAG = AdvertisementApiCalls.class.getSimpleName();

    private static final AdvertisementMobileApiUrls advertisementMobileApiUrls;
    private AdvertisementPresenter advertisementPresenter;

    public void setAdvertisementPresenter(AdvertisementPresenter advertisementPresenter) {
        this.advertisementPresenter = advertisementPresenter;
    }

    static {
        advertisementMobileApiUrls = RetrofitClient.getClient().create(AdvertisementMobileApiUrls.class);
    }

    public void getAllAdvertisements(String did) {
        advertisementMobileApiUrls.getAllAdvertisements(did, Constants.DEVICE_TYPE).enqueue(new Callback<JsonAdvertisementList>() {
            @Override
            public void onResponse(@NonNull Call<JsonAdvertisementList> call, @NonNull Response<JsonAdvertisementList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
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


    public void getAdvertisementsByLocation(String did, Location location) {
        advertisementMobileApiUrls.getAdvertisementsByLocation(did, Constants.DEVICE_TYPE, location).enqueue(new Callback<JsonAdvertisementList>() {
            @Override
            public void onResponse(@NonNull Call<JsonAdvertisementList> call, @NonNull Response<JsonAdvertisementList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
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
}

