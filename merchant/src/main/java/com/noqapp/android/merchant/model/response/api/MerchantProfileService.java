package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.merchant.presenter.beans.JsonMerchant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * User: hitender
 * Date: 4/22/17 11:54 AM
 */

public interface MerchantProfileService {
    @GET("api/m/profile/fetch.json")
    Call<JsonMerchant> fetch(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );
}
