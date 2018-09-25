package com.noqapp.android.client.model;

import com.noqapp.android.client.model.response.open.StoreService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.utils.Constants;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Unregistered client access.
 * <p>
 * User: omkar
 * Date: 3/26/17 11:49 PM
 */
public final class StoreModel {
    private final String TAG = StoreModel.class.getSimpleName();
    private static final StoreService storeService;
    private StorePresenter storePresenter;

    public StoreModel(StorePresenter storePresenter) {
        this.storePresenter = storePresenter;
    }

    static {
        storeService = RetrofitClient.getClient().create(StoreService.class);
    }

    /**
     * Gets state of a queue whose QR code was scanned.
     *
     * @param did
     * @param qrCode
     */
    public void getStoreService(String did, String qrCode) {
        storeService.getStoreService(did, Constants.DEVICE_TYPE, qrCode).enqueue(new Callback<JsonStore>() {
            @Override
            public void onResponse(@NonNull Call<JsonStore> call, @NonNull Response<JsonStore> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("jsonStore response", String.valueOf(response.body()));
                        storePresenter.storeResponse(response.body());
                    } else {
                        Log.e(TAG, "jsonStore error");
                        storePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        storePresenter.authenticationFailure();
                    } else {
                        storePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonStore> call, @NonNull Throwable t) {
                Log.e("jsonStore response", t.getLocalizedMessage(), t);
                storePresenter.storeError();
            }
        });
    }
}
