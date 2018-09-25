package com.noqapp.android.client.model;

import com.noqapp.android.client.model.response.api.PurchaseOrderService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.PurchaseOrderPresenter;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.order.JsonPurchaseOrder;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseApiModel {
    private final String TAG = PurchaseApiModel.class.getSimpleName();
    private final static PurchaseOrderService purchaseOrderService;
    private PurchaseOrderPresenter purchaseOrderPresenter;

    public PurchaseApiModel(PurchaseOrderPresenter purchaseOrderPresenter) {
        this.purchaseOrderPresenter = purchaseOrderPresenter;
    }

    static {
        purchaseOrderService = RetrofitClient.getClient().create(PurchaseOrderService.class);
    }

    public void placeOrder(String did, String mail, String auth, JsonPurchaseOrder jsonPurchaseOrder) {
        purchaseOrderService.placeOrder(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrder).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response placeOrder", String.valueOf(response.body()));
                        purchaseOrderPresenter.purchaseOrderResponse(response.body());
                    } else {
                        purchaseOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        purchaseOrderPresenter.authenticationFailure();
                    } else {
                        purchaseOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrder> call, @NonNull Throwable t) {
                Log.e("onFailure placeOrder", t.getLocalizedMessage(), t);
                purchaseOrderPresenter.purchaseOrderError();
            }
        });
    }
}
