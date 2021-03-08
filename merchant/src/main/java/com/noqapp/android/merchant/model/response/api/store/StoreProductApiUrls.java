package com.noqapp.android.merchant.model.response.api.store;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.merchant.presenter.beans.store.JsonStore;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StoreProductApiUrls {


    @GET("api/m/s/product/store/{codeQR}")
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

    @POST("api/m/s/product/store/{codeQR}/{action}")
    Call<JsonResponse> actionOnProduct(
            @Header("X-R-MAIL")
            String mail,

            @Header("X-R-AUTH")
            String auth,

            @Path("codeQR")
            String codeQR,

            @Path("action")
            ActionTypeEnum actionType,

            @Body
            JsonStoreProduct jsonStoreProduct
    );
}
