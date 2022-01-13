package com.noqapp.android.client.model.response.v3.api

import retrofit2.http.POST
import com.noqapp.android.common.beans.body.DeviceToken
import com.noqapp.android.common.beans.DeviceRegistered
import retrofit2.http.Body
import retrofit2.http.Header

/**
 * Created by hema
 */
interface NoQueueClientApi {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_INPUT}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#DEVICE_DETAIL_MISSING}
     */
    @POST("api/c/device/register")
    suspend fun register(
        @Header("X-R-DID") did: String?,
        @Header("X-R-DT") dt: String?,
        @Header("X-R-AF") appFlavor: String?,
        @Header("X-R-MAIL") mail: String?,
        @Header("X-R-AUTH") auth: String?,
        @Body deviceToken: DeviceToken?
    ): DeviceRegistered?


    /**
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_INPUT}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("open/device/register")
    suspend  fun register(
        @Header("X-R-DT") dt: String?,
        @Header("X-R-AF") appFlavor: String?,
        @Body deviceToken: DeviceToken?
    ): DeviceRegistered?

}