package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.merchant.presenter.beans.DeviceRegistered;
import com.noqapp.android.merchant.presenter.beans.body.DeviceToken;
import com.noqapp.library.beans.JsonLatestAppVersion;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: omkar
 * Date: 4/2/17 6:37 PM
 */
public interface DeviceService {

    @POST("open/device/register.json")
    Call<DeviceRegistered> register(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            DeviceToken deviceToken
    );

    @POST("open/device/version.json")
    Call<JsonLatestAppVersion> isSupportedAppVersion(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-VR")
            String versionRelease
    );
}
