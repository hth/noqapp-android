package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStatList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * User: hitender
 * Date: 5/15/18 3:59 PM
 */
public interface MerchantStatsApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/m/stats/healthCare")
    Call<HealthCareStatList> healthCare(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );
}
