package com.noqapp.client.model;

import android.util.Log;

import com.noqapp.client.model.database.utils.NoQueueDB;
import com.noqapp.client.model.response.open.QueueService;
import com.noqapp.client.network.RetrofitClient;
import com.noqapp.client.presenter.QueuePresenter;
import com.noqapp.client.presenter.ResponsePresenter;
import com.noqapp.client.presenter.TokenAndQueuePresenter;
import com.noqapp.client.presenter.TokenPresenter;
import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.presenter.beans.JsonResponse;
import com.noqapp.client.presenter.beans.JsonToken;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.beans.JsonTokenAndQueueList;
import com.noqapp.client.presenter.beans.body.DeviceToken;
import com.noqapp.client.utils.UserUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.client.utils.Constants.DEVICE_TYPE;
import static com.noqapp.client.utils.Constants.VERSION_RELEASE;

/**
 * Unregistered client access.
 * <p>
 * User: omkar
 * Date: 3/26/17 11:49 PM
 */
public final class QueueModel {
    private static final String TAG = QueueModel.class.getSimpleName();

    private static final QueueService queueService;
    public static QueuePresenter queuePresenter;
    public static TokenPresenter tokenPresenter;
    public static ResponsePresenter responsePresenter;
    public static TokenAndQueuePresenter tokenAndQueuePresenter;

    static {
        queueService = RetrofitClient.getClient().create(QueueService.class);
    }

    /**
     * Gets state of a queue whose QR code was scanned.
     *
     * @param did
     * @param qrCode
     */
    public static void getQueueState(String did, String qrCode) {
        queueService.getQueueState(did, DEVICE_TYPE, qrCode).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(Call<JsonQueue> call, Response<JsonQueue> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    queuePresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Get state of queue upon scan");
                }
            }

            @Override
            public void onFailure(Call<JsonQueue> call, Throwable t) {
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
    public static void getAllJoinedQueue(String did) {
        queueService.getAllJoinedQueue(did, DEVICE_TYPE).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(Call<JsonTokenAndQueueList> call, Response<JsonTokenAndQueueList> response) {
                if (response.body() != null && response.body().getError() == null) {
                    if (response.body().getTokenAndQueues().size() > 0) {
                        Log.d("Response all join queue", String.valueOf(response.body().getTokenAndQueues().size()));
                        //// TODO: 4/16/17 just for testing : remove below line after testing done
                        //tokenAndQueuePresenter.noCurrentQueue();
                        //Todo : uncomment the queuresponse
                        List<JsonTokenAndQueue> jsonTokenAndQueues = response.body().getTokenAndQueues();
                        tokenAndQueuePresenter.queueResponse(jsonTokenAndQueues);
                    } else {
                        NoQueueDB.deleteCurrentQueue();
                        Log.d(TAG, "Empty currently joined history");
                        tokenAndQueuePresenter.noCurrentQueue();
                    }
                } else if (response.body() != null && response.body().getError() != null) {
                    Log.e(TAG, "Got error");
                }
            }

            @Override
            public void onFailure(Call<JsonTokenAndQueueList> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                tokenAndQueuePresenter.queueError();
            }
        });
    }

    /**
     * Get all historical queues client had joined.
     *
     * @param did
     */
    public static void getAllHistoricalJoinedQueue(String did, DeviceToken deviceToken) {
        queueService.getAllHistoricalJoinedQueue(did, DEVICE_TYPE, deviceToken).enqueue(new Callback<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(Call<JsonTokenAndQueueList> call, Response<JsonTokenAndQueueList> response) {
                if (response.body() != null && response.body().getError() == null) {
                    if (response.body().getTokenAndQueues().size() > 0) {
                        Log.d("History size :: ", String.valueOf(response.body().getTokenAndQueues().size()));
                        //Todo: Remove below line after testing done and uncomment queue response
                        // tokenAndQueuePresenter.noHistoryQueue();
                        tokenAndQueuePresenter.queueResponse(response.body().getTokenAndQueues());
                    } else {
                        //TODO something logical
                        Log.d(TAG, "Empty historical history");
                        tokenAndQueuePresenter.noHistoryQueue();
                    }
                } else if (response.body() != null && response.body().getError() != null) {
                    Log.e(TAG, "Got error");
                }
            }

            @Override
            public void onFailure(Call<JsonTokenAndQueueList> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                tokenAndQueuePresenter.queueError();
            }
        });
    }

    /**
     * Client request to join a queue.
     *
     * @param did
     * @param codeQR
     */
    public static void joinQueue(String did, String codeQR) {
        queueService.joinQueue(did, DEVICE_TYPE, VERSION_RELEASE, codeQR).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(Call<JsonToken> call, Response<JsonToken> response) {
                if (response.body() != null && response.body().getError() == null) {
                    Log.d("Response", String.valueOf(response.body()));
                    tokenPresenter.tokenPresenterResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed to join queue");
                }
            }

            @Override
            public void onFailure(Call<JsonToken> call, Throwable t) {
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
    public static void abortQueue(String did, String codeQR) {
        queueService.abortQueue(did, DEVICE_TYPE, codeQR).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    responsePresenter.responsePresenterResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed abort queue");
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                responsePresenter.responsePresenterError();
            }
        });
    }
}
