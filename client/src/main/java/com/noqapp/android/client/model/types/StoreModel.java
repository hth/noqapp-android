package com.noqapp.android.client.model.types;

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
    private static final String TAG = StoreModel.class.getSimpleName();

    private static final StoreService storeService;
    public static StorePresenter storePresenter;

    static {
        storeService = RetrofitClient.getClient().create(StoreService.class);
    }

    /**
     * Gets state of a queue whose QR code was scanned.
     *
     * @param did
     * @param qrCode
     */
    public static void getStoreService(String did, String qrCode) {
        storeService.getStoreService(did, Constants.DEVICE_TYPE, qrCode).enqueue(new Callback<JsonStore>() {
            @Override
            public void onResponse(@NonNull Call<JsonStore> call, @NonNull Response<JsonStore> response) {
                if (response.code() == Constants.INVALID_BAR_CODE) {
                    storePresenter.authenticationFailure(response.code());
                    return;
                }
                if (response.body() != null) {
                    Log.d("jsonStore response", String.valueOf(response.body()));
                    storePresenter.storeResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "jsonStore error");
                    storePresenter.storeError();
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
