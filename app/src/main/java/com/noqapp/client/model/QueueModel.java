package com.noqapp.client.model;

import android.util.Log;

import com.noqapp.client.model.response.open.QueueService;
import com.noqapp.client.network.RetrofitClient;
import com.noqapp.client.presenter.TokenPresenter;
import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.presenter.QueuePresenter;
import com.noqapp.client.presenter.beans.JsonToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.client.utils.Constants.DEVICE_TYPE;

/**
 * Unregistered client access.
 *
 * User: omkar
 * Date: 3/26/17 11:49 PM
 */
public final class QueueModel {
    public static QueuePresenter queuePresenter;
    public static TokenPresenter tokenPresenter;
    private static final QueueService queueService;

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
        queueService.getQueueState(did, DEVICE_TYPE, qrCode).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(Call<JsonQueue> call, Response<JsonQueue> response) {
                Log.d("Response", String.valueOf(response.body()));
                queuePresenter.queueResponse(response.body());
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
        queueService.getAllJoinedQueue(did, DEVICE_TYPE).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(Call<JsonQueue> call, Response<JsonQueue> response) {
                Log.d("Response", String.valueOf(response.body()));
                queuePresenter.queueResponse(response.body());
            }

            @Override
            public void onFailure(Call<JsonQueue> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                queuePresenter.queueError();
            }
        });
    }

    /**
     * Get all historical queues client had joined.
     *
     * @param did
     */
    public static void getAllHistoricalJoinedQueue(String did) {
        queueService.getAllHistoricalJoinedQueue(did, DEVICE_TYPE).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(Call<JsonQueue> call, Response<JsonQueue> response) {
                Log.d("Response", String.valueOf(response.body()));
                queuePresenter.queueResponse(response.body());
            }

            @Override
            public void onFailure(Call<JsonQueue> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                queuePresenter.queueError();
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
        queueService.joinQueue(did, DEVICE_TYPE, codeQR).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(Call<JsonToken> call, Response<JsonToken> response) {
                Log.d("Response", String.valueOf(response.body()));
                tokenPresenter.queueResponse(response.body());
            }

            @Override
            public void onFailure(Call<JsonToken> call, Throwable t) {
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
        queueService.abortQueue(did, DEVICE_TYPE, codeQR).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(Call<JsonQueue> call, Response<JsonQueue> response) {
                Log.d("Response", String.valueOf(response.body()));
                queuePresenter.queueResponse(response.body());
            }

            @Override
            public void onFailure(Call<JsonQueue> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                queuePresenter.queueError();
            }
        });
    }
}
