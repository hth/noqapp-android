package com.noqapp.android.merchant.model.response.api.health;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;

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
}
