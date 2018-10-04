package com.noqapp.android.client.model;

import com.noqapp.android.client.model.response.api.HistoricalApiService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.OrderHistoryPresenter;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.order.JsonPurchaseOrderList;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderQueueHistoryModel {
    private final String TAG = OrderQueueHistoryModel.class.getSimpleName();
    private static final HistoricalApiService historicalApiService;
    private OrderHistoryPresenter orderHistoryPresenter;

    public void setOrderHistoryPresenter(OrderHistoryPresenter orderHistoryPresenter) {
        this.orderHistoryPresenter = orderHistoryPresenter;
    }

    static {
        historicalApiService = RetrofitClient.getClient().create(HistoricalApiService.class);
    }


    public void orders(String mail, String auth) {
        historicalApiService.orders(mail, auth).enqueue(new Callback<JsonPurchaseOrderList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Response<JsonPurchaseOrderList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response orders history", String.valueOf(response.body()));
                        orderHistoryPresenter.orderHistoryResponse(response.body());
                    } else {
                        Log.e(TAG, "orders history error");
                        orderHistoryPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        orderHistoryPresenter.authenticationFailure();
                    } else {
                        orderHistoryPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Throwable t) {
                Log.e("orders history failure", t.getLocalizedMessage(), t);
                orderHistoryPresenter.orderHistoryError();
            }
        });
    }
}
