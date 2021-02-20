package com.noqapp.android.client.model.response.api.health;

import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface MedicalRecordApiUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("api/c/h/medicalRecord/history")
    Call<JsonMedicalRecordList> history(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );
}
