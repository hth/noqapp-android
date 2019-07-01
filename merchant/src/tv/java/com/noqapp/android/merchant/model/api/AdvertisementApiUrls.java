package com.noqapp.android.merchant.model.api;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonAdvertisementList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AdvertisementApiUrls {

    //TODO(chandra) implements this
    @POST("api/tv/vigyapan/tsd/{codeQR}.json")
    Call<JsonResponse> tagStoreAsDisplayed(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("codeQR")
            String codeQR
    );

    @GET("api/tv/vigyapan/all.json")
    Call<JsonAdvertisementList> getAllAdvertisements(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    @GET("api/tv/vigyapan/professionalProfiles.json")
    Call<JsonProfessionalProfileTVList> professionalProfiles(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );
}
