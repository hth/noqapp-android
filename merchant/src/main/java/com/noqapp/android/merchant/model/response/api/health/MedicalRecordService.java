package com.noqapp.android.merchant.model.response.api.health;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * User: hitender
 * Date: 6/13/18 11:00 AM
 */
public interface MedicalRecordService {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MEDICAL_RECORD_ENTRY_DENIED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#BUSINESS_NOT_AUTHORIZED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/h/medicalRecord/update.json")
    Call<JsonResponse> update(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonMedicalRecord jsonMedicalRecord
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MEDICAL_RECORD_ACCESS_DENIED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#BUSINESS_NOT_AUTHORIZED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/h/medicalRecord/retrieve.json")
    Call<JsonMedicalRecord> retrieve(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonMedicalRecord jsonMedicalRecord
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MEDICAL_RECORD_ENTRY_DENIED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/h/medicalRecord/historical.json")
    Call<JsonMedicalRecordList> historical(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            FindMedicalProfile findMedicalProfile
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/m/h/medicalRecord/{codeQR}/followup.json")
    Call<JsonQueuePersonList> followUp(
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
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MEDICAL_RECORD_ACCESS_DENIED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MEDICAL_RECORD_DOES_NOT_EXISTS}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#BUSINESS_NOT_AUTHORIZED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/m/h/medicalRecord/exists.json")
    Call<JsonMedicalRecord> exists(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonMedicalRecord jsonMedicalRecord
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD_NO_SIZE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD_UNSUPPORTED_FORMAT}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD_EXCEED_SIZE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_UPLOAD}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @Multipart
    @POST("api/m/h/medicalRecord/appendImage.json")
    Call<JsonResponse> appendImage(
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

            @Part("recordReferenceId")
            RequestBody recordReferenceId
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE}
     */
    @POST("api/m/h/medicalRecord/removeImage.json")
    Call<JsonResponse> removeImage(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonMedicalRecord jsonMedicalRecord
    );
}
