package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CustomerPriority;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 6/17/18 6:23 PM
 */
public interface BusinessCustomerApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#BUSINESS_CUSTOMER_ID_EXISTS}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_NOT_FOUND}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
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
            JsonBusinessCustomer jsonBusinessCustomer
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#BUSINESS_CUSTOMER_ID_DOES_NOT_EXISTS}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/bc/editId.json")
    Call<JsonQueuePersonList> editId(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonBusinessCustomer jsonBusinessCustomer
    );


    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SC_NOT_FOUND
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_NOT_FOUND
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#ACCOUNT_INACTIVE
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE
     */
    @POST("api/m/bc/findCustomer.json")
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

    @POST("api/m/bc/access/action.json")
    Call<JsonResponse> accessAction(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            CustomerPriority customerPriority
    );
}
