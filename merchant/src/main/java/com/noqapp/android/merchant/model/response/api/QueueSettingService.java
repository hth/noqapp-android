package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.merchant.presenter.beans.body.QueueSetting;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by chandra on 7/15/17.
 */

public interface QueueSettingService {

    @POST("api/m/mq/modify.json")
    Call<QueueSetting> modify(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            QueueSetting queueSetting
    );

    @GET("api/m/mq/state/{codeQR}.json")
    Call<QueueSetting> getQueueState(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("codeQR")
            String codeQR
    );
}
