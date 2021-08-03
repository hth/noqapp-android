package com.noqapp.android.client.model.open;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.open.AdvertisementMobile;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.beans.body.Location;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonAdvertisementList;
import com.noqapp.android.common.presenter.AdvertisementPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdvertisementMobileImpl {
    private static final String TAG = AdvertisementMobileImpl.class.getSimpleName();

    private static final AdvertisementMobile ADVERTISEMENT_MOBILE;
    private AdvertisementPresenter advertisementPresenter;

    public void setAdvertisementPresenter(AdvertisementPresenter advertisementPresenter) {
        this.advertisementPresenter = advertisementPresenter;
    }

    static {
        ADVERTISEMENT_MOBILE = RetrofitClient.getClient().create(AdvertisementMobile.class);
    }

    public void getAdvertisementsByLocation(String did, Location location) {
        ADVERTISEMENT_MOBILE.getAdvertisementsByLocation(did, Constants.DEVICE_TYPE, location).enqueue(new Callback<JsonAdvertisementList>() {
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
}

