package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.body.QueueReview;
import com.noqapp.android.common.beans.JsonResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 5/7/17 12:40 PM
 */
public interface ReviewService {

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @POST("open/review/queueReview.json")
    Call<JsonResponse> queueReview(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            QueueReview queueReview
    );
}
