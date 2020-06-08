package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.JsonInQueuePerson;
import com.noqapp.android.common.beans.JsonCouponList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface AuthenticateClientInQueueApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/c/a/inQueue/{codeQR}/{token}.json")
    Call<JsonInQueuePerson> clientInQueue(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("codeQR")
            String codeQR,

            @Path("token")
            String token
    );

}
