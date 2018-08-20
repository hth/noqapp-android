package com.noqapp.android.merchant.model;

import com.noqapp.android.merchant.interfaces.PreferredBusinessPresenter;
import com.noqapp.android.merchant.model.response.api.health.PreferredStoreService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;
import com.noqapp.android.merchant.utils.Constants;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreferredBusinessModel {
    private static final String TAG = PreferredBusinessModel.class.getSimpleName();

    private static final PreferredStoreService preferredStoreService;
    private PreferredBusinessPresenter preferredBusinessPresenter;

    public PreferredBusinessModel(PreferredBusinessPresenter preferredBusinessPresenter) {
        this.preferredBusinessPresenter = preferredBusinessPresenter;
    }

    static {
        preferredStoreService = RetrofitClient.getClient().create(PreferredStoreService.class);
    }

    public void getAllPreferredStores(String did, String mail, String auth, String codeQR) {
        preferredStoreService.getAllPreferredStores(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonPreferredBusinessList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPreferredBusinessList> call, @NonNull Response<JsonPreferredBusinessList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    preferredBusinessPresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    preferredBusinessPresenter.preferredBusinessResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed image upload");
                    preferredBusinessPresenter.preferredBusinessError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPreferredBusinessList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                preferredBusinessPresenter.preferredBusinessError();
            }
        });
    }

}
