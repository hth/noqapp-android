package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.DeviceRegistered;
import com.noqapp.android.client.presenter.beans.body.DeviceToken;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by chandra on 3/22/18.
 */

public interface NearMeService {


    @POST("open/search/nearMe.json")
    Call<BizStoreElasticList> nearMe(
            @Header("X-R-DID")
                    String did,

            @Header("X-R-DT")
                    String dt,

            @Body
                    StoreInfoParam storeInfoParam
    );
}
