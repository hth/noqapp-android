package com.noqapp.client.model;

import android.util.Log;

import com.noqapp.client.model.response.open.QueueService;
import com.noqapp.client.network.MyCallBack;
import com.noqapp.client.network.RetrofitClient;
import com.noqapp.client.presenter.QueuePresenter;
import com.noqapp.client.presenter.ResponsePresenter;
import com.noqapp.client.presenter.TokenAndQueuePresenter;
import com.noqapp.client.presenter.TokenPresenter;
import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.presenter.beans.JsonResponse;
import com.noqapp.client.presenter.beans.JsonToken;
import com.noqapp.client.presenter.beans.JsonTokenAndQueueList;
import com.noqapp.client.presenter.beans.body.DeviceToken;

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
        queueService = RetrofitClient.getClient(RetrofitClient.BaseURL).create(QueueService.class);
    }

    /**
     * Gets state of a queue whose QR code was scanned.
     *
     * @param did
     * @param qrCode
     */
    public static void getQueueState(String did, String qrCode) {
        queueService.getQueueState(did, DEVICE_TYPE, qrCode).enqueue(new MyCallBack<JsonQueue>() {
            @Override
            public void onResponse(Call<JsonQueue> call, Response<JsonQueue> response) {
                super.onResponse(call,response);
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    queuePresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonQueue> call, Throwable t) {
                super.onFailure(call, t);
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
        queueService.getAllJoinedQueue(did, DEVICE_TYPE).enqueue(new MyCallBack<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(Call<JsonTokenAndQueueList> call, Response<JsonTokenAndQueueList> response) {
                super.onResponse(call,response);
                if (response.body() != null && response.body().getError() == null) {
                    if (response.body().getTokenAndQueues().size() > 0) {
                        Log.d("Response", String.valueOf(response.body()));
                        //// TODO: 4/16/17 just for testing : remove below line after testing done
                        //tokenAndQueuePresenter.noCurentQueue();
                        //Todo : uncomment the queuresponse
                        tokenAndQueuePresenter.queueResponse(response.body().getTokenAndQueues());
                    } else {
                        //TODO something logical
                        Log.d(TAG, "Empty history");
                        tokenAndQueuePresenter.noCurentQueue();
                    }
                } else if (response.body() != null && response.body().getError() != null) {
                    Log.e(TAG, "Got error");
                }
            }

            @Override
            public void onFailure(Call<JsonTokenAndQueueList> call, Throwable t) {
                super.onFailure(call, t);
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
        queueService.getAllHistoricalJoinedQueue(did, DEVICE_TYPE, deviceToken).enqueue(new MyCallBack<JsonTokenAndQueueList>() {
            @Override
            public void onResponse(Call<JsonTokenAndQueueList> call, Response<JsonTokenAndQueueList> response) {
                super.onResponse(call,response);
                if (response.body() != null && response.body().getError() == null) {
                    if (response.body().getTokenAndQueues().size() > 0) {
                        Log.d("History size :: ", String.valueOf(response.body().getTokenAndQueues().size()));
                        //Todo: Remove below line after testing done and uncomment queue response
                        // tokenAndQueuePresenter.noHistoryQueue();
                        tokenAndQueuePresenter.queueResponse(response.body().getTokenAndQueues());
                    } else {
                        //TODO something logical
                        Log.d(TAG, "Empty history");
                        tokenAndQueuePresenter.noHistoryQueue();
                    }
                } else if (response.body() != null && response.body().getError() != null) {
                    Log.e(TAG, "Got error");
                }
            }

            @Override
            public void onFailure(Call<JsonTokenAndQueueList> call, Throwable t) {
                super.onFailure(call, t);
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
        queueService.joinQueue(did, DEVICE_TYPE, VERSION_RELEASE, codeQR).enqueue(new MyCallBack<JsonToken>() {
            @Override
            public void onResponse(Call<JsonToken> call, Response<JsonToken> response) {
                super.onResponse(call,response);
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    tokenPresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed to join queue");
                }
            }

            @Override
            public void onFailure(Call<JsonToken> call, Throwable t) {
                super.onFailure(call, t);
                Log.e("Response", t.getLocalizedMessage(), t);
                tokenPresenter.queueError();
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
        queueService.abortQueue(did, DEVICE_TYPE, codeQR).enqueue(new MyCallBack<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                super.onResponse(call,response);
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    responsePresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                super.onFailure(call, t);
                Log.e("Response", t.getLocalizedMessage(), t);
                responsePresenter.queueError();
            }
        });
    }
}
