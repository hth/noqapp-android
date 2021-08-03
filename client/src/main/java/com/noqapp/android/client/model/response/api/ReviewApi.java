package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.body.OrderReview;
import com.noqapp.android.client.presenter.beans.body.QueueReview;
import com.noqapp.android.common.beans.JsonResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 7/14/18 12:19 AM
 */
public interface ReviewApi {
    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @POST("api/c/review/queue")
    Call<JsonResponse> queue(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            QueueReview queueReview
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @POST("api/c/review/order")
    Call<JsonResponse> order(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            OrderReview orderReview
    );
}
