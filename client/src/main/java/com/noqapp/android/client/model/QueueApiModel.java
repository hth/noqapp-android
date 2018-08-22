package com.noqapp.android.client.model;

import com.noqapp.android.client.model.response.api.QueueService;
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

import android.support.annotation.NonNull;
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
    private final static QueueService queueService;
    private QueuePresenter queuePresenter;
    private TokenPresenter tokenPresenter;
    private ResponsePresenter responsePresenter;
    private TokenAndQueuePresenter tokenAndQueuePresenter;

    public QueueApiModel setQueuePresenter(QueuePresenter queuePresenter) {
        this.queuePresenter = queuePresenter;
        return this;
    }

    public QueueApiModel setTokenPresenter(TokenPresenter tokenPresenter) {
        this.tokenPresenter = tokenPresenter;
        return this;
    }

    public QueueApiModel setResponsePresenter(ResponsePresenter responsePresenter) {
        this.responsePresenter = responsePresenter;
        return this;
    }

    public QueueApiModel setTokenAndQueuePresenter(TokenAndQueuePresenter tokenAndQueuePresenter) {
        this.tokenAndQueuePresenter = tokenAndQueuePresenter;
        return this;
    }

    static {
        queueService = RetrofitClient.getClient().create(QueueService.class);
    }

    public void getQueueState(String did, String mail, String auth, String codeQR) {
        queueService.getQueueState(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueue> call, @NonNull Response<JsonQueue> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    queuePresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    queuePresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Get state of queue upon scan");
                    queuePresenter.queueError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueue> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                queuePresenter.queueError();
            }
        });
    }

    public void getAllJoinedQueues(String did, String mail, String auth) {
        queueService.getAllJoinedQueue(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Response<JsonTokenAndQueueList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    tokenAndQueuePresenter.authenticationFailure(response.code());
                    return;
                } else if (response.code() == 500) {
                    tokenAndQueuePresenter.currentQueueError();
                    return;
                }
                if (null != response.body() && null == response.body().getError()) {
                    /// if (response.body().getTokenAndQueues().size() > 0) {
                    Log.d("Response all join queue", String.valueOf(response.body().getTokenAndQueues().size()));
                    Log.d("Response joinqueuevalue", response.body().getTokenAndQueues().toString());
                    //// TODO: 4/16/17 just for testing : remove below line after testing done
                    //tokenAndQueuePresenter.noCurrentQueue();
                    //Todo : uncomment the queuresponse
                    List<JsonTokenAndQueue> jsonTokenAndQueues = response.body().getTokenAndQueues();
                    tokenAndQueuePresenter.currentQueueResponse(jsonTokenAndQueues);
//                    } else {
//                        TokenAndQueueDB.deleteCurrentQueue();
//                        Log.d(TAG, "Empty currently joined history");
//                        tokenAndQueuePresenter.noCurrentQueue();
//                    }
                } else if (response.body() != null && response.body().getError() != null) {
                    Log.e(TAG, "Got error");
                    tokenAndQueuePresenter.currentQueueError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                tokenAndQueuePresenter.currentQueueError();
            }
        });
    }

    public void allHistoricalJoinedQueues(String did, String mail, String auth, DeviceToken deviceToken) {
        queueService.allHistoricalJoinedQueue(did, Constants.DEVICE_TYPE, mail, auth, deviceToken).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Response<JsonTokenAndQueueList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    tokenAndQueuePresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body() && null == response.body().getError()) {
                    // if (response.body().getTokenAndQueues().size() > 0) {
                    Log.d("History size :: ", String.valueOf(response.body().getTokenAndQueues().size()));
                    //Todo: Remove below line after testing done and uncomment queue response
                    // tokenAndQueuePresenter.noHistoryQueue();
                    tokenAndQueuePresenter.historyQueueResponse(response.body().getTokenAndQueues(), response.body().isSinceBeginning());
//                    } else {
//                        //TODO something logical
//                        Log.d(TAG, "Empty historical history");
//                        tokenAndQueuePresenter.noHistoryQueue();
//                    }
                } else if (null != response.body() && null != response.body().getError()) {
                    Log.e(TAG, "Got error");
                    tokenAndQueuePresenter.historyQueueError();
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
        queueService.joinQueue(did, Constants.DEVICE_TYPE, mail, auth, joinQueue).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    tokenPresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Response", response.body().toString());
                    tokenPresenter.tokenPresenterResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed to join queue" + response.body().getError());
                    tokenPresenter.tokenPresenterError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                tokenPresenter.tokenPresenterError();
            }
        });
    }

    public void abortQueue(String did, String mail, String auth, String codeQR) {
        queueService.abortQueue(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    responsePresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    responsePresenter.responsePresenterResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed abort queue");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                responsePresenter.responsePresenterError();
            }
        });
    }


}
