package com.noqapp.android.client.model.response.api;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface NotificationApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     */
    @POST("api/c/notification.json")
    Call<JsonResponse> notificationViewed(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        Notification notification
    );
}
