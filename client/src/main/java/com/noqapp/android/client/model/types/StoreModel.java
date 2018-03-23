package com.noqapp.android.client.model.types;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.client.model.response.open.QueueService;
import com.noqapp.android.client.model.response.open.StoreService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.TokenAndQueuePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonQueueList;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList;
import com.noqapp.android.client.presenter.beans.body.DeviceToken;
import com.noqapp.android.client.utils.Constants;

import java.util.List;

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
                    Log.d("Response", String.valueOf(response.body()));
                    storePresenter.storeResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Get state of queue upon scan");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonStore> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                storePresenter.storeError();
            }
        });
    }


}
