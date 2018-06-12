package com.noqapp.android.merchant.model.response.api;

import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.common.beans.JsonProfile;
import com.noqapp.common.beans.JsonResponse;
import com.noqapp.common.beans.body.UpdateProfile;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * User: hitender
 * Date: 4/22/17 11:54 AM
 */

public interface MerchantProfileService {
    @GET("api/m/profile/fetch.json")
    Call<JsonMerchant> fetch(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    @POST("api/m/profile/update.json")
    Call<JsonProfile> update(
            @Header("X-R-MAIL")
                    String mail,

            @Header("X-R-AUTH")
                    String auth,

            @Body
            UpdateProfile updateProfile
    );

    @Multipart
    @POST("api/m/profile/upload.json")
    Call<JsonResponse> upload(
            @Header("X-R-DID")
                    String did,

            @Header("X-R-DT")
                    String dt,

            @Header("X-R-MAIL")
                    String mail,

            @Header("X-R-AUTH")
                    String auth,

            @Part
            MultipartBody.Part file
    );
}
