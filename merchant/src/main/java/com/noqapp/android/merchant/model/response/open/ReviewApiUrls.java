package com.noqapp.android.merchant.model.response.open;

import com.noqapp.android.common.beans.JsonReviewList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ReviewApiUrls {

    @GET("open/review/reviews/{codeQR}")
    Call<JsonReviewList> review(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Path("codeQR")
            String codeQR
    );
}
