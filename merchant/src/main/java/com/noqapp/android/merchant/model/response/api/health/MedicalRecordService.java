package com.noqapp.android.merchant.model.response.api.health;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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
    @POST("api/m/h/medicalRecord/add.json")
    Call<JsonResponse> add(
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
    @POST("api/m/h/medicalRecord/fetch.json")
    Call<JsonMedicalRecordList> fetch(
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
}
