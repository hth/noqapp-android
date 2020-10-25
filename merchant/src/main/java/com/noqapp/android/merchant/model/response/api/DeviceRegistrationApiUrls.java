package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.DeviceToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DeviceRegistrationApiUrls {


    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_INPUT}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#DEVICE_DETAIL_MISSING}
     */
    @POST("api/m/dv/registration.json")
    Call<JsonResponse> registration(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header ("X-R-AF")
        String appFlavor,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        DeviceToken deviceToken
    );
}
