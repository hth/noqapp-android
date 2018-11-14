package com.noqapp.android.client.model;

import com.noqapp.android.client.model.response.api.PurchaseOrderService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.PurchaseOrderPresenter;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.body.OrderDetail;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;

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

    public void purchase(String did, String mail, String auth, JsonPurchaseOrder jsonPurchaseOrder) {
        purchaseOrderService.purchase(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrder).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response purchase", String.valueOf(response.body()));
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
                Log.e("onFailure purchase", t.getLocalizedMessage(), t);
                purchaseOrderPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void cancelOrder(String did, String mail, String auth, JsonPurchaseOrder jsonPurchaseOrder) {
        purchaseOrderService.cancel(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrder).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response purchase", String.valueOf(response.body()));
                        purchaseOrderPresenter.purchaseOrderCancelResponse(response.body());
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
                Log.e("onFailure purchase", t.getLocalizedMessage(), t);
                purchaseOrderPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void activateOrder(String did, String mail, String auth, JsonPurchaseOrderHistorical jsonPurchaseOrderHistorical) {
        purchaseOrderService.activate(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrderHistorical).enqueue(new Callback<JsonPurchaseOrderHistorical>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderHistorical> call, @NonNull Response<JsonPurchaseOrderHistorical> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response purchase", String.valueOf(response.body()));
                        purchaseOrderPresenter.purchaseOrderActivateResponse(response.body());
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
            public void onFailure(@NonNull Call<JsonPurchaseOrderHistorical> call, @NonNull Throwable t) {
                Log.e("onFailure purchase", t.getLocalizedMessage(), t);
                purchaseOrderPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void orderDetail(String did, String mail, String auth, OrderDetail orderDetail) {
        purchaseOrderService.orderDetail(did, Constants.DEVICE_TYPE, mail, auth, orderDetail).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response orderDetail", String.valueOf(response.body()));
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
                Log.e("onFailure orderDetail", t.getLocalizedMessage(), t);
                purchaseOrderPresenter.responseErrorPresenter(null);
            }
        });
    }
}
