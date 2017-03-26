package com.noqapp.client.Model.ResponsesInterface;

import com.noqapp.client.Presenter.Beans.ScanQRCode;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by omkar on 3/26/17.
 */

public interface ScanQRCodeService {

    @GET("open/token/{QRCode}.json")
    Call<ScanQRCode> getQRCodeResult(@Header("X-R-DID") String did, @Header("X-R-DT") String dt, @Path("QRCode") String qrCode);
}
