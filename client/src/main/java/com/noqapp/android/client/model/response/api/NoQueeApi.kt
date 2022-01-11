package com.noqapp.android.client.model.response.api

import retrofit2.http.POST
import com.noqapp.android.common.beans.body.DeviceToken
import com.noqapp.android.common.beans.DeviceRegistered
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header

/**
 * Created by hema
 */
interface NoQueeApi {

    @POST("api/c/device/register")
    suspend fun register(
        @Header("X-R-DID") did: String?,
        @Header("X-R-DT") dt: String?,
        @Header("X-R-AF") appFlavor: String?,
        @Header("X-R-MAIL") mail: String?,
        @Header("X-R-AUTH") auth: String?,
        @Body deviceToken: DeviceToken?
    ): DeviceRegistered?


    @POST("open/device/register")
    suspend  fun register(
        @Header("X-R-DT") dt: String?,
        @Header("X-R-AF") appFlavor: String?,
        @Body deviceToken: DeviceToken?
    ): DeviceRegistered?

}