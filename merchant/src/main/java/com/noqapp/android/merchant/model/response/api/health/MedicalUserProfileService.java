package com.noqapp.android.merchant.model.response.api.health;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User: hitender
 * Date: 7/25/18 5:55 PM
 */
public interface MedicalUserProfileService {

    @POST("api/m/h/medicalUserProfile/fetch.json")
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
