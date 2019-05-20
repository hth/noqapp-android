package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.JsonFeedList;
import com.noqapp.android.common.beans.JsonAdvertisementList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * hitender
 * 2019-05-20 11:03
 */
public interface AdvertisementMobileApiUrls {

    /**
     * ERROR
     * {@link com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum#SEVERE}
     */
    @GET("open/vigyapan/all.json")
    Call<JsonAdvertisementList> getAllAdvertisements(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt
    );
}
