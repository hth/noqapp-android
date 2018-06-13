package com.noqapp.android.merchant.model.response.api.health;

import com.noqapp.common.beans.JsonResponse;
import com.noqapp.common.beans.medical.JsonMedicalRecord;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 6/13/18 11:00 AM
 */
public interface MedicalRecordService {

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
}
