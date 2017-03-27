package com.noqapp.client.Model;

import android.util.Log;

import com.noqapp.client.Model.ResponsesInterface.CodeQRService;
import com.noqapp.client.NetworkUtilities.RetrofitClient;
import com.noqapp.client.Presenter.Beans.JsonQueue;
import com.noqapp.client.Presenter.QueuePresenter;

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
