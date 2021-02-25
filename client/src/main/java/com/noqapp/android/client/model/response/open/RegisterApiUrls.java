package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.body.Login;
import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.common.beans.JsonProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 4/8/17 8:29 PM
 */
public interface RegisterApiUrls {

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_EXISTING}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_INPUT}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#DEVICE_DETAIL_MISSING}
     */
    @POST("open/client/registration")
    Call<JsonProfile> register(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            Registration registration
    );

    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_NOT_FOUND}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_INPUT}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#ACCOUNT_INACTIVE}
     */
    @POST("open/client/login")
    Call<JsonProfile> login(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            Login login
    );
}
