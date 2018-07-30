package com.noqapp.android.merchant.views.model;

import com.noqapp.android.merchant.model.response.api.order.PurchaseOrderService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.order.JsonPurchaseOrderList;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseOrderModel {

    private static final String TAG = PurchaseOrderModel.class.getSimpleName();
    private static final PurchaseOrderService purchaseOrderService;
    private PurchaseOrderPresenter purchaseOrderPresenter;

    public PurchaseOrderModel(PurchaseOrderPresenter purchaseOrderPresenter) {
        this.purchaseOrderPresenter = purchaseOrderPresenter;
    }

    static {
        purchaseOrderService = RetrofitClient.getClient().create(PurchaseOrderService.class);
    }

    public void fetch(String did, String mail, String auth, String codeQR) {
        purchaseOrderService.fetch(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonPurchaseOrderList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Response<JsonPurchaseOrderList> response) {
                if (response.code() == 401) {
                    purchaseOrderPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Get order list", String.valueOf(response.body()));
                    purchaseOrderPresenter.purchaseOrderResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Found error while Get order list");
                    purchaseOrderPresenter.purchaseOrderError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Throwable t) {
                Log.e("Order list error", t.getLocalizedMessage(), t);
                purchaseOrderPresenter.purchaseOrderError();
            }
        });
    }
}
