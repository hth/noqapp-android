package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.DeviceToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Unregistered client APIs.
 * <p>
 * User: omkar
 * Date: 3/26/17 11:50 PM
 */
public interface TokenQueue {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("open/token/{codeQR}")
    Call<JsonQueue> getQueueState(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Path("codeQR")
            String codeQR
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("open/token/v1/{codeQR}")
    Call<BizStoreElasticList> getAllQueueState(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Path("codeQR")
            String codeQR
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("open/token/levelUp/{codeQR}")
    Call<BizStoreElasticList> getAllQueueStateLevelUp(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Path("codeQR")
            String codeQR
    );

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("open/token/queues")
    Call<JsonTokenAndQueueList> getAllJoinedQueue(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("open/token/queue/{codeQR}")
    @Deprecated
    Call<JsonToken> joinQueue(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Path("codeQR")
            String codeQR
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     *
     * QUEUE_ORDER_ABORT_EXPIRED_LIMITED_TIME will never happen in open api
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#QUEUE_ORDER_ABORT_EXPIRED_LIMITED_TIME}
     */
    @POST("open/token/abort/{codeQR}")
    Call<JsonResponse> abortQueue(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Path("codeQR")
            String codeQR
    );
}
