package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FindCustomerApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SC_NOT_FOUND
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_NOT_FOUND
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#ACCOUNT_INACTIVE
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE
     */
    @POST("api/m/s/purchaseOrder/findCustomer.json")
    Call<JsonProfile> findCustomer(
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
