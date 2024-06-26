package com.noqapp.android.client.model.open;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.open.StoreDetail;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.StoreHoursPresenter;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonHourList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Unregistered client access.
 * <p>
 * User: omkar
 * Date: 3/26/17 11:49 PM
 */
public class StoreDetailImpl {
    private final String TAG = StoreDetailImpl.class.getSimpleName();
    private static final StoreDetail STORE_DETAIL;
    private StorePresenter storePresenter;
    private StoreHoursPresenter storeHoursPresenter;
    private boolean responseReceived = false;
    public JsonStore jsonStore;

    public boolean isResponseReceived() {
        return responseReceived;
    }

    public void setResponseReceived(boolean responseReceived) {
        this.responseReceived = responseReceived;
    }

    public StoreDetailImpl(StorePresenter storePresenter) {
        this.storePresenter = storePresenter;
    }

    public StoreDetailImpl(StoreHoursPresenter storeHoursPresenter) {
        this.storeHoursPresenter = storeHoursPresenter;
    }

    static {
        STORE_DETAIL = RetrofitClient.getClient().create(StoreDetail.class);
    }

    /**
     * Gets state of a queue whose QR code was scanned.
     *
     * @param did
     * @param codeQR
     */
    public void getStoreDetail(String did, String codeQR) {
        STORE_DETAIL.getStoreDetail(did, Constants.DEVICE_TYPE, codeQR).enqueue(new Callback<JsonStore>() {
            @Override
            public void onResponse(@NonNull Call<JsonStore> call, @NonNull Response<JsonStore> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("jsonStore response", String.valueOf(response.body()));
                        storePresenter.storeResponse(response.body());
                        jsonStore = response.body();
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
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<JsonStore> call, @NonNull Throwable t) {
                Log.e("jsonStore response", t.getLocalizedMessage(), t);
                storePresenter.responseErrorPresenter(null);
            }
        });
    }

    /**
     * Gets state of a queue whose QR code was scanned.
     *
     * @param did
     * @param codeQR
     */
    public void storeHours(String did, String codeQR) {
        STORE_DETAIL.storeHours(did, Constants.DEVICE_TYPE, codeQR).enqueue(new Callback<JsonHourList>() {
            @Override
            public void onResponse(@NonNull Call<JsonHourList> call, @NonNull Response<JsonHourList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("storeHours response", String.valueOf(response.body()));
                        storeHoursPresenter.storeHoursResponse(response.body());
                    } else {
                        Log.e(TAG, "storeHours error");
                        storeHoursPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        storeHoursPresenter.authenticationFailure();
                    } else {
                        storeHoursPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonHourList> call, @NonNull Throwable t) {
                Log.e("storeHours response", t.getLocalizedMessage(), t);
                storeHoursPresenter.responseErrorPresenter(null);
            }
        });
    }
}
