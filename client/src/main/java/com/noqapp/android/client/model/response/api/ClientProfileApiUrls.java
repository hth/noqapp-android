package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.body.MigrateMail;
import com.noqapp.android.client.presenter.beans.body.MigratePhone;
import com.noqapp.android.client.presenter.beans.body.mail.ChangeMailOTP;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonUserAddress;
import com.noqapp.android.common.beans.JsonUserAddressList;
import com.noqapp.android.common.beans.body.UpdateProfile;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Registered client APIs.
 * <p>
 * User: hitender
 * Date: 3/27/17 8:05 PM
 */
public interface ClientProfileApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#ACCOUNT_INACTIVE}
     */
    @GET("api/c/profile/fetch")
    Call<JsonProfile> fetch(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#ACCOUNT_INACTIVE}
     */
    @POST("api/c/profile/update")
    Call<JsonProfile> update(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            UpdateProfile updateProfile
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_EXISTING}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/c/profile/changeMail")
    Call<JsonResponse> changeMail(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            MigrateMail migrateMail
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_EXISTING}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/c/profile/migrate")
    Call<JsonProfile> migrate(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            MigratePhone migratePhone
    );

    @POST("api/c/profile/migrateMail")
    Call<JsonProfile> migrateMail(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            ChangeMailOTP changeMailOTP
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     */
    @GET("api/c/profile/address")
    Call<JsonUserAddressList> address(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("api/c/profile/address/add")
    Call<JsonUserAddressList> addressAdd(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonUserAddress jsonUserAddress
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("api/c/profile/address/primary")
    Call<JsonUserAddressList> addressPrimary(
        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        JsonUserAddress jsonUserAddress
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     */
    @POST("api/c/profile/address/delete")
    Call<JsonUserAddressList> addressDelete(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonUserAddress jsonUserAddress
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD_NO_SIZE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD_EXCEED_SIZE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD_UNSUPPORTED_FORMAT}
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @Multipart
    @POST("api/c/profile/upload")
    Call<JsonResponse> upload(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Part
            MultipartBody.Part profileImageFile,

            @Part("profileImageOfQid")
            RequestBody profileImageOfQid
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @POST("api/c/profile/remove")
    Call<JsonResponse> remove(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            UpdateProfile updateProfile
    );
}
