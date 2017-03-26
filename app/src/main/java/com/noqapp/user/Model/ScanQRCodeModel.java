package com.noqapp.user.Model;

import android.util.Log;

import com.noqapp.user.Model.ResponsesInterface.ScanQRCodeService;
import com.noqapp.user.NetworkUtilities.RetrofitClient;
import com.noqapp.user.Presenter.Beans.ScanQRCode;
import com.noqapp.user.Presenter.QRCodePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by omkar on 3/26/17.
 */

public class ScanQRCodeModel {

    public QRCodePresenter presenter;

    public ScanQRCodeService getScanService() {

        return RetrofitClient.getClient(RetrofitClient.BaseURL).create(ScanQRCodeService.class);

    }

    public void getQRCodeResponse(String did, String dt, String qrCode) {
        getScanService().getQRCodeResult(did, dt, qrCode).enqueue(new Callback<ScanQRCode>() {
            @Override
            public void onResponse(Call<ScanQRCode> call, Response<ScanQRCode> response) {
                Log.d("Respose", String.valueOf(response.body()));
                ScanQRCode scanQRCode = response.body();
                presenter.didQRCodeResponse(scanQRCode);
            }

            @Override
            public void onFailure(Call<ScanQRCode> call, Throwable t) {
                Log.e("Respose", t.getLocalizedMessage(), t);
                presenter.didQRCodeError();

            }
        });
    }

}
