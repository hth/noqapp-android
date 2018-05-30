package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.medical.JsonMedicalRecordList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;


public interface MedicalRecordService {

    @GET("api/c/medicalRecord/fetch.json")
    Call<JsonMedicalRecordList> getMedicalRecord(
            @Header("X-R-MAIL")
                    String mail,

            @Header("X-R-AUTH")
                    String auth
    );

}
