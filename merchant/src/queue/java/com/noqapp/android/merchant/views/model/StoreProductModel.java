package com.noqapp.android.merchant.views.model;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.merchant.model.response.api.store.StoreProductService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.views.interfaces.ActionOnProductPresenter;
import com.noqapp.android.merchant.views.interfaces.StoreProductPresenter;
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
    private ActionOnProductPresenter actionOnProductPresenter;

    public void setStoreProductPresenter(StoreProductPresenter storeProductPresenter) {
        this.storeProductPresenter = storeProductPresenter;
    }

    public void setActionOnProductPresenter(ActionOnProductPresenter actionOnProductPresenter) {
        this.actionOnProductPresenter = actionOnProductPresenter;
    }

    static {
        storeProductService = RetrofitClient.getClient().create(StoreProductService.class);
    }

    public void storeProduct(String did, String mail, String auth, String codeQR) {
        storeProductService.storeProduct(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonStore>() {
            @Override
            public void onResponse(@NonNull Call<JsonStore> call, @NonNull Response<JsonStore> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
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

    public void actionOnProduct(String mail, String auth, String codeQR, ActionTypeEnum actionTypeEnum ,JsonStoreProduct jsonStoreProduct) {
        storeProductService.actionOnProduct( mail, auth, codeQR,actionTypeEnum,jsonStoreProduct).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("actionOnProductResponse", String.valueOf(response.body()));
                        actionOnProductPresenter.actionOnProductResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while actionOnProduct");
                        actionOnProductPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        actionOnProductPresenter.authenticationFailure();
                    } else {
                        actionOnProductPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("actionOnProduct error", t.getLocalizedMessage(), t);
                actionOnProductPresenter.responseErrorPresenter(null);
            }
        });
    }
}
