package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.common.beans.body.JoinQueue;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface KioskApi {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#QUEUE_JOIN_FAILED_PAYMENT_CALL_REQUEST}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/c/kiosk/queue")
    Call<JsonToken> joinQueue(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JoinQueue joinQueue
    );
}
