package com.noqapp.android.client.model;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.model.response.api.TokenQueueApiService;
import com.noqapp.android.client.network.RetrofitClient;
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

import androidx.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * Authorised call required authorised user
 */
public class QueueApiModel {
    private final String TAG = QueueApiModel.class.getSimpleName();
    private final static TokenQueueApiService TOKEN_QUEUE_API_SERVICE;
    private QueuePresenter queuePresenter;
    private TokenPresenter tokenPresenter;
    private ResponsePresenter responsePresenter;
    private TokenAndQueuePresenter tokenAndQueuePresenter;

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

    static {
        TOKEN_QUEUE_API_SERVICE = RetrofitClient.getClient().create(TokenQueueApiService.class);
    }

    public void getQueueState(String did, String mail, String auth, String codeQR) {
        TOKEN_QUEUE_API_SERVICE.getQueueState(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueue> call, @NonNull Response<JsonQueue> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response getQueueState", String.valueOf(response.body()));
                        queuePresenter.queueResponse(response.body());
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
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueue> call, @NonNull Throwable t) {
                Log.e("getQueueState failure", t.getLocalizedMessage(), t);
                queuePresenter.queueError();
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
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Failure joinQueue", t.getLocalizedMessage(), t);
                tokenPresenter.responseErrorPresenter(null);
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
}
