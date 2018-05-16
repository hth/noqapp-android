package com.noqapp.android.client.model.response.api;

import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.JsonUserAddress;
import com.noqapp.android.client.presenter.beans.JsonUserAddressList;
import com.noqapp.android.client.presenter.beans.body.MigrateProfile;
import com.noqapp.android.client.presenter.beans.body.UpdateProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Registered client APIs.
 * <p>
 * User: hitender
 * Date: 3/27/17 8:05 PM
 */
public interface ProfileService {

    @GET("api/c/profile/fetch.json")
    Call<JsonProfile> fetch(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    @POST("api/c/profile/update.json")
    Call<JsonProfile> update(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            UpdateProfile updateProfile
    );

    @POST("api/c/profile/migrate.json")
    Call<JsonProfile> migrate(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            MigrateProfile migrateProfile
    );

    @GET("api/c/profile/address.json")
    Call<JsonUserAddressList> address(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth
    );

    @POST("api/c/profile/address/add.json")
    Call<JsonUserAddressList> addressAdd(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonUserAddress jsonUserAddress
    );

    @POST("api/c/profile/address/delete.json")
    Call<JsonUserAddressList> addressDelete(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Body
            JsonUserAddress jsonUserAddress
    );
}
