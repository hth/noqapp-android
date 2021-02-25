package com.noqapp.android.merchant.model.response.api.health;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CodeQRDateRangeLookup;
import com.noqapp.android.merchant.presenter.beans.body.merchant.FindMedicalProfile;
import com.noqapp.android.merchant.presenter.beans.body.merchant.HospitalVisitFor;
import com.noqapp.android.merchant.presenter.beans.body.merchant.LabFile;

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
public interface MedicalRecordApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MEDICAL_RECORD_ENTRY_DENIED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MEDICAL_RECORD_POPULATED_WITH_LAB}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#BUSINESS_NOT_AUTHORIZED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/h/medicalRecord/update")
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
    @POST("api/m/h/medicalRecord/retrieve")
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
    @POST("api/m/h/medicalRecord/historical")
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
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MEDICAL_RECORD_ENTRY_DENIED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/h/medicalRecord/historical/{md}")
    Call<JsonMedicalRecordList> historicalFiltered(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("md")
            String medicalDepartment,

            @Body
            FindMedicalProfile findMedicalProfile
    );


    @POST("api/m/h/medicalRecord/hospitalVisitSchedule")
    Call<JsonHospitalVisitScheduleList> hospitalVisitSchedule(
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

    @POST("api/m/h/medicalRecord/modifyVisitingFor")
    Call<JsonHospitalVisitSchedule> modifyVisitingFor(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            HospitalVisitFor hospitalVisitFor
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/h/medicalRecord/updateObservation")
    Call<JsonResponse> updateObservation(
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
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/m/h/medicalRecord/{codeQR}/followup")
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
    @GET("api/m/h/medicalRecord/exists/{codeQR}/{recordReferenceId}")
    Call<JsonMedicalRecord> exists(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("codeQR")
            String codeQR,

            @Path("recordReferenceId")
            String recordReferenceId
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
    @POST("api/m/h/medicalRecord/appendImage")
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
    @POST("api/m/h/medicalRecord/removeImage")
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

    @POST("api/m/h/medicalRecord/workHistory")
    Call<JsonMedicalRecordList> workHistory(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            CodeQRDateRangeLookup codeQRDateRangeLookup
    );
}
