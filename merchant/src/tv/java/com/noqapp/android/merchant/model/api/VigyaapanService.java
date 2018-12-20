package com.noqapp.android.merchant.model.api;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTVList;
import com.noqapp.android.merchant.presenter.beans.body.QueueDetail;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VigyaapanService {

    @POST("api/tv/vigyaapan/tsd/{codeQR}.json")
    Call<JsonResponse> tagStoreAsDisplayed(
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


    @GET("api/tv/vigyaapan/{vt}.json")
    Call<JsonQueueTVList> getVigyaapan(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("vt")
            String vt
    );
}
