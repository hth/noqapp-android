package com.noqapp.merchant.model.response.api;

import com.noqapp.merchant.model.MerchantProfileModel;
import com.noqapp.merchant.presenter.beans.JsonMerchant;
import com.noqapp.merchant.presenter.beans.JsonTopicList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * User: hitender
 * Date: 4/22/17 11:54 AM
 */

public interface MerchantProfileService {
    @GET("api/m/mq/queues.json")
    Call<JsonMerchant> fetch(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );
}
