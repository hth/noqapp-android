package com.noqapp.client.model;

import android.util.Log;

import com.noqapp.client.model.responsesInterface.CodeQRService;
import com.noqapp.client.networkUtilities.RetrofitClient;
import com.noqapp.client.presenter.Beans.JsonQueue;
import com.noqapp.client.presenter.QueuePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: omkar
 * Date: 3/26/17 11:49 PM
 */
public class CodeQRModel {

    public QueuePresenter queuePresenter;

    CodeQRService getScanService() {
        return RetrofitClient.getClient(RetrofitClient.BaseURL).create(CodeQRService.class);
    }

    public void getQRCodeResponse(String did, String dt, String qrCode) {
        getScanService().getState(did, dt, qrCode).enqueue(new Callback<JsonQueue>() {
            @Override
            public void onResponse(Call<JsonQueue> call, Response<JsonQueue> response) {
                Log.d("Respose", String.valueOf(response.body()));
                JsonQueue jsonQueue = response.body();
                queuePresenter.didQRCodeResponse(jsonQueue);
            }

            @Override
            public void onFailure(Call<JsonQueue> call, Throwable t) {
                Log.e("Respose", t.getLocalizedMessage(), t);
                queuePresenter.didQRCodeError();

            }
        });
    }

}
