package com.noqapp.android.client.model.response.api.health;

import com.noqapp.android.client.presenter.beans.body.UserMedicalProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserMedicalProfileApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MEDICAL_PROFILE_DOES_NOT_EXISTS}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/c/h/medicalProfile/profile.json")
    Call<JsonMedicalProfile> profile(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            UserMedicalProfile userMedicalProfile
    );

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#ACCOUNT_INACTIVE}
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("api/c/h/medicalProfile/updateUserMedicalProfile.json")
    Call<JsonMedicalProfile> updateUserMedicalProfile(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            UserMedicalProfile userMedicalProfile
    );
}
