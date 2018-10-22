package com.noqapp.android.merchant.model;

import com.noqapp.android.merchant.model.response.api.store.StoreProductService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.StoreProductPresenter;
import com.noqapp.android.merchant.presenter.beans.store.JsonStore;
import com.noqapp.android.merchant.utils.Constants;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreProductModel {

    private static final String TAG = StoreProductModel.class.getSimpleName();
    private static final StoreProductService storeProductService;
    private StoreProductPresenter storeProductPresenter;

    public void setStoreProductPresenter(StoreProductPresenter storeProductPresenter) {
        this.storeProductPresenter = storeProductPresenter;
    }

    static {
        storeProductService = RetrofitClient.getClient().create(StoreProductService.class);
    }

    public void storeProduct(String did, String mail, String auth, String codeQR) {
        storeProductService.storeProduct(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonStore>() {
            @Override
            public void onResponse(@NonNull Call<JsonStore> call, @NonNull Response<JsonStore> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("storeProduct response", String.valueOf(response.body()));
                        storeProductPresenter.storeProductResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while storeProduct");
                        storeProductPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        storeProductPresenter.authenticationFailure();
                    } else {
                        storeProductPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonStore> call, @NonNull Throwable t) {
                Log.e("storeProduct error", t.getLocalizedMessage(), t);
                storeProductPresenter.responseErrorPresenter(null);
            }
        });
    }
}
