package com.noqapp.client.model;

import android.util.Log;

import com.noqapp.client.model.response.QueueService;
import com.noqapp.client.network.RetrofitClient;
import com.noqapp.client.presenter.Beans.JsonQueue;
import com.noqapp.client.presenter.QueuePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: omkar
 * Date: 3/26/17 11:49 PM
 */
public final class QueueModel {

    public static QueuePresenter queuePresenter;
    private static final QueueService queueService;

    static {
        queueService = RetrofitClient.getClient(RetrofitClient.BaseURL).create(QueueService.class);
    }

    public static void getQueueInformation(String did, String dt, String qrCode) {
        queueService.getQueue(did, dt, qrCode).enqueue(new Callback<JsonQueue>() {
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
