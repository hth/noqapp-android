package com.noqapp.android.client.model.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.client.model.response.api.QueueService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.TokenAndQueuePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 5/27/17 5:38 PM
 */

public class QueueApiModel {
    private static final String TAG = QueueApiModel.class.getSimpleName();

    private final static QueueService queueService;
    public static QueuePresenter queuePresenter;
    public static TokenPresenter tokenPresenter;
    public static ResponsePresenter responsePresenter;
    public static TokenAndQueuePresenter tokenAndQueuePresenter;

    static {
        queueService = RetrofitClient.getClient().create(QueueService.class);
    }

    public static void remoteScanQueueState(String did, String mail, String auth, String codeQR) {
        queueService.remoteScanQueueState(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueue> call, @NonNull Response<JsonQueue> response) {
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

    public static void remoteJoinQueue(String did, String mail, String auth, String codeQR) {
        queueService.remoteJoinQueue(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
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
}
