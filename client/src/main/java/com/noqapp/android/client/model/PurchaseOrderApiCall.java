package com.noqapp.android.client.model;

import com.noqapp.android.client.model.response.api.PurchaseOrderApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.PurchaseOrderPresenter;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.body.OrderDetail;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.presenter.CashFreeNotifyPresenter;

import androidx.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseOrderApiCall {
    private final String TAG = PurchaseOrderApiCall.class.getSimpleName();
    private final static PurchaseOrderApiUrls purchaseOrderApiUrls;
    private PurchaseOrderPresenter purchaseOrderPresenter;
    private CashFreeNotifyPresenter cashFreeNotifyPresenter;

    public PurchaseOrderApiCall(PurchaseOrderPresenter purchaseOrderPresenter) {
        this.purchaseOrderPresenter = purchaseOrderPresenter;
    }

    public void setCashFreeNotifyPresenter(CashFreeNotifyPresenter cashFreeNotifyPresenter) {
        this.cashFreeNotifyPresenter = cashFreeNotifyPresenter;
    }

    static {
        purchaseOrderApiUrls = RetrofitClient.getClient().create(PurchaseOrderApiUrls.class);
    }

    public void purchase(String did, String mail, String auth, JsonPurchaseOrder jsonPurchaseOrder) {
        purchaseOrderApiUrls.purchase(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrder).enqueue(new Callback<JsonPurchaseOrder>() {
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
        purchaseOrderApiUrls.cancel(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrder).enqueue(new Callback<JsonPurchaseOrder>() {
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
        purchaseOrderApiUrls.activate(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrderHistorical).enqueue(new Callback<JsonPurchaseOrderHistorical>() {
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
        purchaseOrderApiUrls.orderDetail(did, Constants.DEVICE_TYPE, mail, auth, orderDetail).enqueue(new Callback<JsonPurchaseOrder>() {
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

    public void cashFreeNotify(String did, String mail, String auth, JsonCashfreeNotification jsonCashfreeNotification) {
        purchaseOrderApiUrls.cashFreeNotify(did, Constants.DEVICE_TYPE, mail, auth, jsonCashfreeNotification).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response cashFreeNotify", String.valueOf(response.body()));
                        cashFreeNotifyPresenter.cashFreeNotifyResponse(response.body());
                    } else {
                        cashFreeNotifyPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        cashFreeNotifyPresenter.authenticationFailure();
                    } else {
                        cashFreeNotifyPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrder> call, @NonNull Throwable t) {
                Log.e("onFailureCashFreeNotify", t.getLocalizedMessage(), t);
                cashFreeNotifyPresenter.responseErrorPresenter(null);
            }
        });
    }
}
