package com.noqapp.android.client.model;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.model.response.api.TokenQueueApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.CashFreeNotifyQPresenter;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.TokenAndQueuePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.beans.body.JoinQueue;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;

import android.util.Log;
import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * Authorised call required authorised user
 */
public class QueueApiAuthenticCall {
    private final String TAG = QueueApiAuthenticCall.class.getSimpleName();
    private final static TokenQueueApiUrls TOKEN_QUEUE_API_SERVICE;
    private QueuePresenter queuePresenter;
    private TokenPresenter tokenPresenter;
    private ResponsePresenter responsePresenter;
    private TokenAndQueuePresenter tokenAndQueuePresenter;
    private CashFreeNotifyQPresenter cashFreeNotifyQPresenter;
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

    static {
        TOKEN_QUEUE_API_SERVICE = RetrofitClient.getClient().create(TokenQueueApiUrls.class);
    }

    public void getQueueState(String did, String mail, String auth, String codeQR) {
        TOKEN_QUEUE_API_SERVICE.getQueueState(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueue> call, @NonNull Response<JsonQueue> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
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
        TOKEN_QUEUE_API_SERVICE.getAllJoinedQueue(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Response<JsonTokenAndQueueList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response all join queue", String.valueOf(response.body().getTokenAndQueues().size()));
                        Log.d("Response joinqueuevalue", response.body().getTokenAndQueues().toString());
                        List<JsonTokenAndQueue> jsonTokenAndQueues = response.body().getTokenAndQueues();
                        tokenAndQueuePresenter.currentQueueResponse(jsonTokenAndQueues);
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
        TOKEN_QUEUE_API_SERVICE.allHistoricalJoinedQueue(did, Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, mail, auth, deviceToken).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Response<JsonTokenAndQueueList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
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
        TOKEN_QUEUE_API_SERVICE.joinQueue(did, Constants.DEVICE_TYPE, mail, auth, joinQueue).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
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
        TOKEN_QUEUE_API_SERVICE.cashfreeNotify(did, Constants.DEVICE_TYPE, mail, auth, jsonCashfreeNotification).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
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
        TOKEN_QUEUE_API_SERVICE.payBeforeJoinQueue(did, Constants.DEVICE_TYPE, mail, auth, joinQueue).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
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

    public void skipPayBeforeQueue(String did, String mail, String auth, JoinQueue joinQueue) {
        TOKEN_QUEUE_API_SERVICE.skipPayBeforeQueue(did, Constants.DEVICE_TYPE, mail, auth, joinQueue).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp skipPayBeforeQueue", response.body().toString());
                        tokenPresenter.unPaidTokenPresenterResponse(response.body());
                        jsonToken = response.body();
                    } else {
                        Log.e(TAG, "Failed to skipPayBeforeQueue" + response.body().getError());
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
                Log.e("Fail skipPayBeforeQueue", t.getLocalizedMessage(), t);
                tokenPresenter.responseErrorPresenter(null);
                responseReceived = true;
            }
        });
    }

    public void abortQueue(String did, String mail, String auth, String codeQR) {
        TOKEN_QUEUE_API_SERVICE.abortQueue(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
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

    public void cancelPayBeforeQueue(String did, String mail, String auth, JsonToken jsonToken) {
        TOKEN_QUEUE_API_SERVICE.cancelPayBeforeQueue(did, Constants.DEVICE_TYPE, mail, auth, jsonToken).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
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
}
