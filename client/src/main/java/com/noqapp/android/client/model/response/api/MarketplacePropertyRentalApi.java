package com.noqapp.android.client.model.response.api;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.marketplace.JsonPropertyRental;
import com.noqapp.android.common.beans.marketplace.MarketplaceElastic;
import com.noqapp.android.common.beans.marketplace.MarketplaceElasticList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MarketplacePropertyRentalApi {

    /**
     * Errors
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @GET("api/c/marketplace/propertyRental")
    Call<MarketplaceElasticList> showMyPostOnMarketplace(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth
    );

    /**
     * Create new post on marketplace
     *
     * Errors
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @POST("api/c/marketplace/propertyRental")
    Call<MarketplaceElastic> postOnMarketplace(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        JsonPropertyRental jsonPropertyRental
    );

    /**
     * Needs PostId, Business Type and image to upload image.
     *
     * Errors
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @Multipart
    @POST("api/c/marketplace/propertyRental/uploadImage")
    Call<JsonResponse> uploadImage(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Part
        MultipartBody.Part multipartFile,

        @Part("postId")
        RequestBody postId,

        @Part("businessTypeAsString")
        RequestBody businessTypeAsString
    );

    /**
     * Needs PostId, ImageId(s) and Business Type to remove image.
     *
     * Errors
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @POST("api/c/marketplace/propertyRental/removeImage")
    Call<MarketplaceElastic> removeImage(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        JsonPropertyRental jsonPropertyRental
    );

    /**
     * On clicking view detail on the post.
     *
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE}
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @POST("api/c/marketplace/propertyRental/view")
    Call<JsonResponse> viewMarketplace(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        JsonPropertyRental jsonPropertyRental
    );

    /**
     * Person should be able to do it once. After that the button should be disabled.
     *
     * Errors
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#MOBILE}
     * {@link com.noqapp.android.common.beans.JsonResponse#response} is false(0) when not found
     */
    @POST("api/c/marketplace/propertyRental/initiateContact")
    Call<JsonResponse> initiateContact(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        JsonPropertyRental jsonPropertyRental
    );
}
