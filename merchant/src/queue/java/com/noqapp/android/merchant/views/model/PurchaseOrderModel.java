package com.noqapp.android.merchant.views.model;

import com.noqapp.android.merchant.model.response.api.order.PurchaseOrderService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.body.order.OrderServed;
import com.noqapp.android.merchant.presenter.beans.order.JsonPurchaseOrderList;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.AcquireOrderPresenter;
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

    public void setPurchaseOrderPresenter(PurchaseOrderPresenter purchaseOrderPresenter) {
        this.purchaseOrderPresenter = purchaseOrderPresenter;
    }

    public void setAcquireOrderPresenter(AcquireOrderPresenter acquireOrderPresenter) {
        this.acquireOrderPresenter = acquireOrderPresenter;

    }

    private AcquireOrderPresenter acquireOrderPresenter;



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


    public void acquire(String did, String mail, String auth, OrderServed orderServed) {
        purchaseOrderService.acquire(did, Constants.DEVICE_TYPE, mail, auth, orderServed).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == 401) {
                    acquireOrderPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Get order list", String.valueOf(response.body()));
                    acquireOrderPresenter.acquireOrderResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Found error while Get order list");
                    acquireOrderPresenter.acquireOrderError(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Order list error", t.getLocalizedMessage(), t);
                acquireOrderPresenter.acquireOrderError(null);
            }
        });
    }
}
