package com.noqapp.android.client.model;

import com.noqapp.android.client.model.response.api.HistoricalApiService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.OrderHistoryPresenter;
import com.noqapp.android.client.presenter.QueueHistoryPresenter;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistoricalList;
import com.noqapp.android.client.presenter.beans.JsonQueueHistoricalList;
import com.noqapp.android.client.utils.Constants;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderQueueHistoryModel {
    private final String TAG = OrderQueueHistoryModel.class.getSimpleName();
    private static final HistoricalApiService historicalApiService;
    private OrderHistoryPresenter orderHistoryPresenter;
    private QueueHistoryPresenter queueHistoryPresenter;

    public void setOrderHistoryPresenter(OrderHistoryPresenter orderHistoryPresenter) {
        this.orderHistoryPresenter = orderHistoryPresenter;
    }

    public void setQueueHistoryPresenter(QueueHistoryPresenter queueHistoryPresenter) {
        this.queueHistoryPresenter = queueHistoryPresenter;
    }

    static {
        historicalApiService = RetrofitClient.getClient().create(HistoricalApiService.class);
    }


    public void orders(String mail, String auth) {
        historicalApiService.orders(mail, auth).enqueue(new Callback<JsonPurchaseOrderHistoricalList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderHistoricalList> call, @NonNull Response<JsonPurchaseOrderHistoricalList> response) {
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
            public void onFailure(@NonNull Call<JsonPurchaseOrderHistoricalList> call, @NonNull Throwable t) {
                Log.e("orders history failure", t.getLocalizedMessage(), t);
                orderHistoryPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void queues(String mail, String auth) {
        historicalApiService.queues(mail, auth).enqueue(new Callback<JsonQueueHistoricalList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueueHistoricalList> call, @NonNull Response<JsonQueueHistoricalList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response queues history", String.valueOf(response.body()));
                        queueHistoryPresenter.queueHistoryResponse(response.body());
                    } else {
                        Log.e(TAG, "orders queues error");
                        queueHistoryPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queueHistoryPresenter.authenticationFailure();
                    } else {
                        queueHistoryPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueueHistoricalList> call, @NonNull Throwable t) {
                Log.e("orders queues failure", t.getLocalizedMessage(), t);
                queueHistoryPresenter.responseErrorPresenter(null);
            }
        });
    }
}
