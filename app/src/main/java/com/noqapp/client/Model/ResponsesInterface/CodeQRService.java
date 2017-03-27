package com.noqapp.client.Model.ResponsesInterface;

import com.noqapp.client.Presenter.Beans.JsonQueue;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * User: omkar
 * Date: 3/26/17 11:50 PM
 */
public interface CodeQRService {

    @GET("open/token/{codeQR}.json")
    Call<JsonQueue> getState(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Path("codeQR")
            String codeQR
    );
}
