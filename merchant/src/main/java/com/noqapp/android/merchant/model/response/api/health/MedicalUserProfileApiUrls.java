package com.noqapp.android.merchant.model.response.api.health;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.merchant.presenter.beans.body.merchant.FindMedicalProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 7/25/18 5:55 PM
 */
public interface MedicalUserProfileApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE_JSON}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MEDICAL_RECORD_ENTRY_DENIED}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/m/h/medicalUserProfile/fetch")
    Call<JsonProfile> fetch(
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
}
