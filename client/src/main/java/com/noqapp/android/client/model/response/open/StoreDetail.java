package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.common.beans.JsonHourList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by chandra on 3/23/18.
 */
public interface StoreDetail {

    /**
     * No Errors
     */
    @GET("open/store/{codeQR}")
    Call<JsonStore> getStoreDetail(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Path("codeQR")
        String codeQR
    );

    /**
     * No Errors
     */
    @GET("open/store/hours/{codeQR}")
    Call<JsonHourList> storeHours(
        @Header("X-R-DID")
        String did,

        @Header("X-R-DT")
        String dt,

        @Path("codeQR")
        String codeQR
    );
}
