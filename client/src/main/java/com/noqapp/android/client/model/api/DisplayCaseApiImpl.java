package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.DisplayCaseApi;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.DisplayCasePresenter;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.store.JsonStoreProductList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayCaseApiImpl {

    private final String TAG = DisplayCaseApiImpl.class.getSimpleName();
    private static final DisplayCaseApi DISPLAY_CASE_API;
    private DisplayCasePresenter displayCasePresenter;

    public DisplayCaseApiImpl(DisplayCasePresenter displayCasePresenter) {
        this.displayCasePresenter = displayCasePresenter;
    }

    static {
        DISPLAY_CASE_API = RetrofitClient.getClient().create(DisplayCaseApi.class);
    }

    /**
     * @param did
     * @param codeQR
     */
    public void storeDisplayCase(String did, String mail, String auth, String codeQR) {
        DISPLAY_CASE_API.storeDisplayCase(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonStoreProductList>() {
            @Override
            public void onResponse(@NonNull Call<JsonStoreProductList> call, @NonNull Response<JsonStoreProductList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("ResponseStoreDisplayCas", String.valueOf(response.body()));
                        displayCasePresenter.displayCaseResponse(response.body());
                    } else {
                        Log.e(TAG, "Error storeDisplayCase " + response.body().getError());
                        displayCasePresenter.displayCaseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        displayCasePresenter.authenticationFailure();
                    } else {
                        displayCasePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonStoreProductList> call, @NonNull Throwable t) {
                Log.e("FailureStoreDisplayCase", t.getLocalizedMessage(), t);
                displayCasePresenter.responseErrorPresenter(null);
            }
        });
    }
}
