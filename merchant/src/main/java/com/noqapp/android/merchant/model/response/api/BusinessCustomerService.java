package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 6/17/18 6:23 PM
 */
public interface BusinessCustomerService {

    @POST("api/m/bc/addId.json")
    Call<JsonQueuePersonList> addId(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonBusinessCustomerLookup jsonBusinessCustomerLookup
    );

}
