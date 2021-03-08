package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.FavoriteElastic;
import com.noqapp.android.common.beans.JsonResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Display clients favorite APIs.
 * <p>
 * User: hitender
 * Date: 12/26/20 10:35 PM
 */
public interface FavouriteAPIUrls {

    /**
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     */
    @GET("api/c/favourite")
    Call<FavoriteElastic> favorite(
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
     * Errors
     * {@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED} - HTTP STATUS 401
     * {@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} - HTTP STATUS 404
     */
    @POST("api/c/favourite")
    Call<JsonResponse> actionOnFavorite(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Header("X-R-MAIL")
        String mail,

        @Header("X-R-AUTH")
        String auth,

        @Body
        FavoriteElastic favoriteElastic
    );
}
