package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.model.response.api.TokenQueueApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.CashFreeNotifyQPresenter;
import com.noqapp.android.client.presenter.QueueJsonPurchaseOrderPresenter;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.TokenAndQueuePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList;
import com.noqapp.android.client.presenter.beans.body.QueueAuthorize;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.beans.body.JoinQueue;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;
import com.noqapp.android.common.beans.payment.cashfree.JsonResponseWithCFToken;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Authorised call required authorised user
 */
public class QueueApiAuthenticCall {
    private final String TAG = QueueApiAuthenticCall.class.getSimpleName();
    private final static TokenQueueApiUrls tokenQueueApiUrls;
    private QueuePresenter queuePresenter;
    private TokenPresenter tokenPresenter;
    private ResponsePresenter responsePresenter;
    private TokenAndQueuePresenter tokenAndQueuePresenter;
    private CashFreeNotifyQPresenter cashFreeNotifyQPresenter;
    private QueueJsonPurchaseOrderPresenter queueJsonPurchaseOrderPresenter;
    private boolean responseReceived = false;
    public JsonQueue jsonQueue;
    public JsonToken jsonToken;
    public boolean isResponseReceived() {
        return responseReceived;
    }

    public void setResponseReceived(boolean responseReceived) {
        this.responseReceived = responseReceived;
    }

    public void setQueuePresenter(QueuePresenter queuePresenter) {
        this.queuePresenter = queuePresenter;
    }

    public void setTokenPresenter(TokenPresenter tokenPresenter) {
        this.tokenPresenter = tokenPresenter;
    }

    public void setResponsePresenter(ResponsePresenter responsePresenter) {
        this.responsePresenter = responsePresenter;
    }

    public void setTokenAndQueuePresenter(TokenAndQueuePresenter tokenAndQueuePresenter) {
        this.tokenAndQueuePresenter = tokenAndQueuePresenter;
    }

    public void setCashFreeNotifyQPresenter(CashFreeNotifyQPresenter cashFreeNotifyQPresenter) {
        this.cashFreeNotifyQPresenter = cashFreeNotifyQPresenter;
    }

    public void setQueueJsonPurchaseOrderPresenter(QueueJsonPurchaseOrderPresenter queueJsonPurchaseOrderPresenter) {
        this.queueJsonPurchaseOrderPresenter = queueJsonPurchaseOrderPresenter;
    }

    static {
        tokenQueueApiUrls = RetrofitClient.getClient().create(TokenQueueApiUrls.class);
    }

    public void getQueueState(String did, String mail, String auth, String codeQR) {
        tokenQueueApiUrls.getQueueState(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueue> call, @NonNull Response<JsonQueue> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response getQueueState", String.valueOf(response.body()));
                        queuePresenter.queueResponse(response.body());
                        jsonQueue = response.body();
                    } else {
                        Log.e(TAG, "Error getQueueState");
                        queuePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queuePresenter.authenticationFailure();
                    } else {
                        queuePresenter.responseErrorPresenter(response.code());
                    }
                }
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueue> call, @NonNull Throwable t) {
                Log.e("getQueueState failure", t.getLocalizedMessage(), t);
                queuePresenter.queueError();
                responseReceived = true;
            }
        });
    }

    public void getAllJoinedQueues(String did, String mail, String auth) {
        tokenQueueApiUrls.getAllJoinedQueue(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Response<JsonTokenAndQueueList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response all join queue", String.valueOf(response.body().getTokenAndQueues().size()));
                        Log.d("Response joinqueuevalue", response.body().getTokenAndQueues().toString());
                        //List<JsonTokenAndQueue> jsonTokenAndQueues = response.body().getTokenAndQueues();
                        tokenAndQueuePresenter.currentQueueResponse(response.body());
                    } else if (response.body() != null && response.body().getError() != null) {
                        Log.e(TAG, "Got error getAllJoinedQueue");
                        tokenAndQueuePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        tokenAndQueuePresenter.authenticationFailure();
                    } else {
                        tokenAndQueuePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Throwable t) {
                Log.e("getAllJoinedQue failure", t.getLocalizedMessage(), t);
                tokenAndQueuePresenter.currentQueueError();
            }
        });
    }

    public void allHistoricalJoinedQueue(String did, String mail, String auth, DeviceToken deviceToken) {
        tokenQueueApiUrls.allHistoricalJoinedQueue(did, Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, mail, auth, deviceToken).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Response<JsonTokenAndQueueList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("History size :: ", String.valueOf(response.body().getTokenAndQueues().size()));
                        tokenAndQueuePresenter.historyQueueResponse(response.body().getTokenAndQueues(), response.body().isSinceBeginning());
                    } else if (null != response.body() && null != response.body().getError()) {
                        Log.e(TAG, "Got error allHistoricalJoinedQueue");
                        tokenAndQueuePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        tokenAndQueuePresenter.authenticationFailure();
                    } else {
                        tokenAndQueuePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                tokenAndQueuePresenter.historyQueueError();
            }
        });
    }

    public void joinQueue(String did, String mail, String auth, JoinQueue joinQueue) {
        tokenQueueApiUrls.joinQueue(did, Constants.DEVICE_TYPE, mail, auth, joinQueue).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response joinQueue", response.body().toString());
                        tokenPresenter.tokenPresenterResponse(response.body());
                        jsonToken = response.body();
                    } else {
                        Log.e(TAG, "Failed to join queue" + response.body().getError());
                        tokenPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        tokenPresenter.authenticationFailure();
                    } else {
                        tokenPresenter.responseErrorPresenter(response.code());
                    }
                }
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Failure joinQueue", t.getLocalizedMessage(), t);
                tokenPresenter.responseErrorPresenter(null);
                responseReceived = true;
            }
        });
    }
    public void cashFreeQNotify(String did, String mail, String auth, JsonCashfreeNotification jsonCashfreeNotification) {
        tokenQueueApiUrls.cashfreeNotify(did, Constants.DEVICE_TYPE, mail, auth, jsonCashfreeNotification).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Res cashFreeQNotify", String.valueOf(response.body()));
                        cashFreeNotifyQPresenter.cashFreeNotifyQResponse(response.body());
                    } else {
                        cashFreeNotifyQPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        cashFreeNotifyQPresenter.authenticationFailure();
                    } else {
                        cashFreeNotifyQPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("onFail CashFreeQNotify", t.getLocalizedMessage(), t);
                cashFreeNotifyQPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void payBeforeJoinQueue(String did, String mail, String auth, JoinQueue joinQueue) {
        tokenQueueApiUrls.payBeforeJoinQueue(did, Constants.DEVICE_TYPE, mail, auth, joinQueue).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp payBeforeJoinQueue", response.body().toString());
                        tokenPresenter.paidTokenPresenterResponse(response.body());
                        jsonToken = response.body();
                    } else {
                        Log.e(TAG, "Failed to payBeforeJoinQueue" + response.body().getError());
                        tokenPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        tokenPresenter.authenticationFailure();
                    } else {
                        tokenPresenter.responseErrorPresenter(response.code());
                    }
                }
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Fail payBeforeJoinQueue", t.getLocalizedMessage(), t);
                tokenPresenter.responseErrorPresenter(null);
                responseReceived = true;
            }
        });
    }

    public void abortQueue(String did, String mail, String auth, String codeQR) {
        tokenQueueApiUrls.abortQueue(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response abortQueue", String.valueOf(response.body()));
                        responsePresenter.responsePresenterResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed abort queue");
                        responsePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        responsePresenter.authenticationFailure();
                    } else {
                        responsePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("abortQueue failure", t.getLocalizedMessage(), t);
                responsePresenter.responsePresenterError();
            }
        });
    }

    public void authorize(String did, String mail, String auth, QueueAuthorize queueAuthorize) {
        tokenQueueApiUrls.authorize(did, Constants.DEVICE_TYPE, mail, auth, queueAuthorize).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response success", String.valueOf(response.body()));
                        responsePresenter.responsePresenterResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to add authorized");
                        responsePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        responsePresenter.authenticationFailure();
                    } else {
                        responsePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("authorize failure", t.getLocalizedMessage(), t);
                responsePresenter.responsePresenterError();
            }
        });
    }

    public void cancelPayBeforeQueue(String did, String mail, String auth, JsonToken jsonToken) {
        tokenQueueApiUrls.cancelPayBeforeQueue(did, Constants.DEVICE_TYPE, mail, auth, jsonToken).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Res: cancelPayBeforeQ", String.valueOf(response.body()));
                        responsePresenter.responsePresenterResponse(response.body());
                    } else {
                        Log.e(TAG, "Fail cancelPayBeforeQueue");
                        responsePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        responsePresenter.authenticationFailure();
                    } else {
                        responsePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("cancelPayBeforeQ fail", t.getLocalizedMessage(), t);
                responsePresenter.responsePresenterError();
            }
        });
    }

    public void purchaseOrder(String did, String mail, String auth, String token,String codeQr) {
        tokenQueueApiUrls.purchaseOrder(did, Constants.DEVICE_TYPE, mail, auth, token,codeQr).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Res: purchaseOrder", String.valueOf(response.body()));
                        queueJsonPurchaseOrderPresenter.queueJsonPurchaseOrderResponse(response.body());
                    } else {
                        Log.e(TAG, "Fail purchaseOrder");
                        queueJsonPurchaseOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queueJsonPurchaseOrderPresenter.authenticationFailure();
                    } else {
                        queueJsonPurchaseOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrder> call, @NonNull Throwable t) {
                Log.e("purchaseOrder fail", t.getLocalizedMessage(), t);
                queueJsonPurchaseOrderPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void payNow(String did, String mail, String auth, JsonPurchaseOrder jsonPurchaseOrder) {
        tokenQueueApiUrls.payNow(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrder).enqueue(new Callback<JsonResponseWithCFToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponseWithCFToken> call, @NonNull Response<JsonResponseWithCFToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Res: payNow", String.valueOf(response.body()));
                        queueJsonPurchaseOrderPresenter.paymentInitiateResponse(response.body());
                    } else {
                        Log.e(TAG, "Fail payNow");
                        queueJsonPurchaseOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queueJsonPurchaseOrderPresenter.authenticationFailure();
                    } else {
                        queueJsonPurchaseOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponseWithCFToken> call, @NonNull Throwable t) {
                Log.e("payNow fail", t.getLocalizedMessage(), t);
                queueJsonPurchaseOrderPresenter.responseErrorPresenter(null);
            }
        });
    }
}
