package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.model.response.open.TokenQueueApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.TokenAndQueuePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.DeviceToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Unregistered client access.
 */
public class QueueApiUnAuthenticCall {
    private final String TAG = QueueApiUnAuthenticCall.class.getSimpleName();
    private static final TokenQueueApiUrls tokenQueueApiUrls;
    private QueuePresenter queuePresenter;
    private TokenPresenter tokenPresenter;
    private ResponsePresenter responsePresenter;
    private TokenAndQueuePresenter tokenAndQueuePresenter;
    public BizStoreElasticList bizStoreElasticList;
    private boolean responseReceived = false;

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

    static {
        tokenQueueApiUrls = RetrofitClient.getClient().create(TokenQueueApiUrls.class);
    }

    /**
     * Gets state of a queue whose QR code was scanned.
     *
     * @param did
     * @param qrCode
     */
    public void getQueueState(String did, String qrCode) {
        tokenQueueApiUrls.getQueueState(did, Constants.DEVICE_TYPE, qrCode).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueue> call, @NonNull Response<JsonQueue> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (response.body() != null && null == response.body().getError()) {
                        Log.d("Response getQueueState", String.valueOf(response.body()));
                        queuePresenter.queueResponse(response.body());
                    } else {

                        Log.e(TAG, "Get state of queue upon scan");
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
                Log.e("failure getQueueState", t.getLocalizedMessage(), t);
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
        tokenQueueApiUrls.getAllQueueState(did, Constants.DEVICE_TYPE, qrCode).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp: getAllQueueState", String.valueOf(response.body()));
                        queuePresenter.queueResponse(response.body());
                    } else {

                        Log.e(TAG, "Get state of getAllQueueState");
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
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("getAllQueueState fail", t.getLocalizedMessage(), t);
                queuePresenter.queueError();
            }
        });
    }

    public void getAllQueueStateLevelUp(String did, String codeQR) {
        tokenQueueApiUrls.getAllQueueStateLevelUp(did, Constants.DEVICE_TYPE, codeQR).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("getAllQueueStateLevelUp", String.valueOf(response.body()));
                        queuePresenter.queueResponse(response.body());
                        bizStoreElasticList = response.body();
                    } else {
                        Log.e(TAG, "error getAllQueueStateLevelUp");
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
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("getAllQueueStateLevelUp", t.getLocalizedMessage(), t);
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
        tokenQueueApiUrls.getAllJoinedQueue(did, Constants.DEVICE_TYPE).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Response<JsonTokenAndQueueList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (response.body() != null && response.body().getError() == null) {
                        Log.d("Response all join queue", String.valueOf(response.body().getTokenAndQueues().size()));
                        Log.d("Response joinqueuevalue", response.body().getTokenAndQueues().toString());
                       // List<JsonTokenAndQueue> jsonTokenAndQueues = response.body().getTokenAndQueues();
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
    public void getAllHistoricalJoinedQueue(String did, DeviceToken deviceToken) {
        tokenQueueApiUrls.getAllHistoricalJoinedQueue(did, Constants.DEVICE_TYPE, BuildConfig.APP_FLAVOR, deviceToken).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTokenAndQueueList> call, @NonNull Response<JsonTokenAndQueueList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (response.body() != null && response.body().getError() == null) {
                        Log.d("History size :: ", String.valueOf(response.body().getTokenAndQueues().size()));
                        tokenAndQueuePresenter.historyQueueResponse(response.body().getTokenAndQueues(), response.body().isSinceBeginning());
                    } else if (response.body() != null && response.body().getError() != null) {
                        Log.e(TAG, "Got error");
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

    /**
     * Client request to join a queue.
     *
     * @param did
     * @param codeQR
     */
    public void joinQueue(String did, String codeQR) {
        tokenQueueApiUrls.joinQueue(did, Constants.DEVICE_TYPE, codeQR).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (response.body() != null && response.body().getError() == null) {
                        Log.d("Response", response.body().toString());
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

    /**
     * Client request to abort a joined queue.
     *
     * @param did
     * @param codeQR
     */
    public void abortQueue(String did, String codeQR) {
        tokenQueueApiUrls.abortQueue(did, Constants.DEVICE_TYPE, codeQR).enqueue(new Callback<JsonResponse>() {
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
}
