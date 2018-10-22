package com.noqapp.android.merchant.views.model;

import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.merchant.model.response.api.store.PurchaseOrderService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.body.store.OrderServed;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.AcquireOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.OrderProcessedPresenter;
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
    private OrderProcessedPresenter orderProcessedPresenter;
    private AcquireOrderPresenter acquireOrderPresenter;

    public void setOrderProcessedPresenter(OrderProcessedPresenter orderProcessedPresenter) {
        this.orderProcessedPresenter = orderProcessedPresenter;
    }

    public void setPurchaseOrderPresenter(PurchaseOrderPresenter purchaseOrderPresenter) {
        this.purchaseOrderPresenter = purchaseOrderPresenter;
    }

    public void setAcquireOrderPresenter(AcquireOrderPresenter acquireOrderPresenter) {
        this.acquireOrderPresenter = acquireOrderPresenter;
    }

    static {
        purchaseOrderService = RetrofitClient.getClient().create(PurchaseOrderService.class);
    }

    public void fetch(String did, String mail, String auth, String codeQR) {
        purchaseOrderService.fetch(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonPurchaseOrderList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Response<JsonPurchaseOrderList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Get order list", String.valueOf(response.body()));
                        purchaseOrderPresenter.purchaseOrderResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while Get order list");
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
            public void onFailure(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Throwable t) {
                Log.e("Order list error", t.getLocalizedMessage(), t);
                purchaseOrderPresenter.purchaseOrderError();
            }
        });
    }

    public void actionOnOrder(String did, String mail, String auth, OrderServed orderServed) {
        purchaseOrderService.actionOnOrder(did, Constants.DEVICE_TYPE, mail, auth, orderServed).enqueue(new Callback<JsonPurchaseOrderList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Response<JsonPurchaseOrderList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("actionOnOrder", String.valueOf(response.body()));
                        orderProcessedPresenter.orderProcessedResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while actionOnOrder");
                        orderProcessedPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        orderProcessedPresenter.authenticationFailure();
                    } else {
                        orderProcessedPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Throwable t) {
                Log.e("actionOnOrder fail", t.getLocalizedMessage(), t);
                orderProcessedPresenter.orderProcessedError();
            }
        });
    }

    public void cancel(String did, String mail, String auth, OrderServed orderServed) {
        purchaseOrderService.cancel(did, Constants.DEVICE_TYPE, mail, auth, orderServed).enqueue(new Callback<JsonPurchaseOrderList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Response<JsonPurchaseOrderList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response Order cancel", String.valueOf(response.body()));
                        orderProcessedPresenter.orderProcessedResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while Order cancel");
                        orderProcessedPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        orderProcessedPresenter.authenticationFailure();
                    } else {
                        orderProcessedPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Throwable t) {
                Log.e("Order cancel fail", t.getLocalizedMessage(), t);
                orderProcessedPresenter.orderProcessedError();
            }
        });
    }

    public void acquire(String did, String mail, String auth, OrderServed orderServed) {
        purchaseOrderService.acquire(did, Constants.DEVICE_TYPE, mail, auth, orderServed).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("order acquire", String.valueOf(response.body()));
                        acquireOrderPresenter.acquireOrderResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while order acquire");
                        acquireOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        acquireOrderPresenter.authenticationFailure();
                    } else {
                        acquireOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Order acquire fail", t.getLocalizedMessage(), t);
                acquireOrderPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void served(String did, String mail, String auth, OrderServed orderServed) {
        purchaseOrderService.served(did, Constants.DEVICE_TYPE, mail, auth, orderServed).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("order served", String.valueOf(response.body()));
                        acquireOrderPresenter.acquireOrderResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while order served");
                        acquireOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        acquireOrderPresenter.authenticationFailure();
                    } else {
                        acquireOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Order served fail", t.getLocalizedMessage(), t);
                acquireOrderPresenter.responseErrorPresenter(null);
            }
        });
    }
}
