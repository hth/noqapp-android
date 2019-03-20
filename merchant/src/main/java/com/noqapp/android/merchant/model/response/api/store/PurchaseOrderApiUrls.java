package com.noqapp.android.merchant.model.response.api.store;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.body.store.OrderServed;
import com.noqapp.android.merchant.presenter.beans.body.store.LabFile;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PurchaseOrderApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/s/purchaseOrder/showOrders/{codeQR}.json")
    Call<JsonPurchaseOrderList> showOrders(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("codeQR")
            String codeQR
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/s/purchaseOrder/served.json")
    Call<JsonToken> served(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            OrderServed OrderServed
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MERCHANT_COULD_NOT_ACQUIRE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/s/purchaseOrder/acquire.json")
    Call<JsonToken> acquire(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            OrderServed OrderServed
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/s/purchaseOrder/actionOnOrder.json")
    Call<JsonPurchaseOrderList> actionOnOrder(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            OrderServed OrderServed
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

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#STORE_OFFLINE
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#STORE_DAY_CLOSED
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#STORE_TEMP_DAY_CLOSED
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#STORE_PREVENT_JOIN
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#PURCHASE_ORDER_PRICE_MISMATCH
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE
     */
    @POST("api/m/s/purchaseOrder/purchase.json")
    Call<JsonPurchaseOrderList> purchase(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonPurchaseOrder jsonPurchaseOrder
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#PURCHASE_ORDER_PRODUCT_NOT_FOUND
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE
     */
    @POST("api/m/s/purchaseOrder/modify.json")
    Call<JsonPurchaseOrder> modify(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonPurchaseOrder jsonPurchaseOrder
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#ORDER_PAYMENT_UPDATE_FAILED}
     */
    @POST("api/m/s/purchaseOrder/partialCounterPayment.json")
    Call<JsonPurchaseOrder> partialCounterPayment(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonPurchaseOrder jsonPurchaseOrder
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#ORDER_PAYMENT_UPDATE_FAILED}\
     */
    @POST("api/m/s/purchaseOrder/counterPayment.json")
    Call<JsonPurchaseOrder> counterPayment(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonPurchaseOrder jsonPurchaseOrder
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/s/purchaseOrder/cancel.json")
    Call<JsonPurchaseOrderList> cancel(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            OrderServed OrderServed
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     */
    @Multipart
    @POST("api/m/s/purchaseOrder/addAttachment.json")
    Call<JsonResponse> addAttachment(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Part
            MultipartBody.Part imageFile,

            @Part("ti")
            RequestBody transactionId
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     */
    @POST("api/m/s/purchaseOrder/removeAttachment.json")
    Call<JsonResponse> removeAttachment(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            LabFile labFile
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     */
    @POST("api/m/s/purchaseOrder/showAttachment.json")
    Call<LabFile> showAttachment(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            LabFile labFile
    );
}
