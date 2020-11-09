package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.body.Location;
import com.noqapp.android.common.beans.JsonAdvertisementList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * hitender
 * 2019-05-20 11:03
 */
public interface AdvertisementMobileApiUrls {

    /**
     * ERROR
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @POST("open/vigyapan/near.json")
    Call<JsonAdvertisementList> getAdvertisementsByLocation(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt,

            @Body
            Location location
    );
}
