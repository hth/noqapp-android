package com.noqapp.android.client.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.client.model.response.open.QueueService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.TokenAndQueuePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.DeviceToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Unregistered client access.
 */
public final class QueueModel {
    private final String TAG = QueueModel.class.getSimpleName();
    private static final QueueService queueService;
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
        queueService = RetrofitClient.getClient().create(QueueService.class);
    }

    /**
     * Gets state of a queue whose QR code was scanned.
     *
     * @param did
     * @param qrCode
     */
    public void getQueueState(String did, String qrCode) {
        queueService.getQueueState(did, Constants.DEVICE_TYPE, qrCode).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueue> call, @NonNull Response<JsonQueue> response) {
                if (response.code() == Constants.INVALID_BAR_CODE) {
                    queuePresenter.authenticationFailure(response.code());
                    return;
                }
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    queuePresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Get state of queue upon scan");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueue> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);

                queuePresenter.queueError();
            }
        });
    }

    /**
     * Gets state of a queue whose QR code was scanned.
     *
     * @param did
     * @param qrCode
     */
    public void getAllQueueState(String did, String qrCode) {
        queueService.getAllQueueState(did, Constants.DEVICE_TYPE, qrCode).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.INVALID_BAR_CODE) {
                    queuePresenter.authenticationFailure(response.code());
                    return;
                }
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    queuePresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Get state of queue upon scan");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);

                queuePresenter.queueError();
            }
        });
    }

    public void getAllQueueStateLevelUp(String did, String qrCode) {
        queueService.getAllQueueStateLevelUp(did, Constants.DEVICE_TYPE, qrCode).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.INVALID_BAR_CODE) {
                    queuePresenter.authenticationFailure(response.code());
                    return;
                }
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    queuePresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Get state of queue upon scan");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);

                queuePresenter.queueError();
            }
        });
    }

    /**
     * Get all the queues client has joined.
     *
     * @param did
     */
    public void getAllJoinedQueue(String did) {
        queueService.getAllJoinedQueue(did, Constants.DEVICE_TYPE).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Response<JsonTokenAndQueueList> response) {
                if (response.body() != null && response.body().getError() == null) {
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

    /**
     * Get all historical queues client had joined.
     *
     * @param did
     */
    public void getHistoryQueueList(String did, DeviceToken deviceToken) {
        queueService.getAllHistoricalJoinedQueue(did, Constants.DEVICE_TYPE, deviceToken).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Response<JsonTokenAndQueueList> response) {
                if (response.body() != null && response.body().getError() == null) {
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
                } else if (response.body() != null && response.body().getError() != null) {
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

    /**
     * Client request to join a queue.
     *
     * @param did
     * @param codeQR
     */
    public void joinQueue(String did, String codeQR) {
        queueService.joinQueue(did, Constants.DEVICE_TYPE, codeQR).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.INVALID_BAR_CODE) {
                    tokenPresenter.authenticationFailure(response.code());
                    return;
                }
                if (response.body() != null && response.body().getError() == null) {
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

    /**
     * Client request to abort a joined queue.
     *
     * @param did
     * @param codeQR
     */
    public void abortQueue(String did, String codeQR) {
        queueService.abortQueue(did, Constants.DEVICE_TYPE, codeQR).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.body() != null) {
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
