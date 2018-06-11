package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStatList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * User: hitender
 * Date: 5/15/18 3:59 PM
 */
public interface MerchantStatsService {

    @GET("api/m/stats/healthCare.json")
    Call<HealthCareStatList> doctor(
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
