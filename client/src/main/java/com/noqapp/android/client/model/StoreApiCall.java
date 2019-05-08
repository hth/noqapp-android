package com.noqapp.android.client.model;

import android.util.Log;

import com.noqapp.android.client.model.response.open.StoreDetailApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.utils.Constants;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Unregistered client access.
 * <p>
 * User: omkar
 * Date: 3/26/17 11:49 PM
 */
public class StoreApiCall {
    private final String TAG = StoreApiCall.class.getSimpleName();
    private static final StoreDetailApiUrls storeDetailApiUrls;
    private StorePresenter storePresenter;

    public StoreApiCall(StorePresenter storePresenter) {
        this.storePresenter = storePresenter;
    }

    static {
        storeDetailApiUrls = RetrofitClient.getClient().create(StoreDetailApiUrls.class);
    }

    /**
     * Gets state of a queue whose QR code was scanned.
     *
     * @param did
     * @param qrCode
     */
    public void getStoreService(String did, String qrCode) {
        storeDetailApiUrls.getStoreService(did, Constants.DEVICE_TYPE, qrCode).enqueue(new Callback<JsonStore>() {
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
                storePresenter.responseErrorPresenter(null);
            }
        });
    }
}
