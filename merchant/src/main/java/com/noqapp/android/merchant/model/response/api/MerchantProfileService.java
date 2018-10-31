package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;

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
 * User: hitender
 * Date: 4/22/17 11:54 AM
 */

public interface MerchantProfileService {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#DEVICE_DETAIL_MISSING}
     */
    @GET("api/m/profile/fetch.json")
    Call<JsonMerchant> fetch(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header ("X-R-AF")
            String appFlavor,

            @Header("X-R-VR")
            String versionRelease,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_INPUT}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/profile/update.json")
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
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#USER_INPUT}
     */
    @POST("api/m/profile/updateProfessionalProfile.json")
    Call<JsonProfessionalProfilePersonal> update(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD_NO_SIZE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD_UNSUPPORTED_FORMAT}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD_EXCEED_SIZE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD}
     */
    @Multipart
    @POST("api/m/profile/upload.json")
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
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/profile/intellisense.json")
    Call<JsonResponse> intellisense(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal
    );
}
