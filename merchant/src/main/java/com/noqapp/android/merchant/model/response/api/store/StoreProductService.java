package com.noqapp.android.merchant.model.response.api.store;

import com.noqapp.android.merchant.presenter.beans.store.JsonStore;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface StoreProductService {


    @GET("api/m/s/product/store/{codeQR}.json")
    Call<JsonStore> storeProduct(
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
}
