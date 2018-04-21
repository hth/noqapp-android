package com.noqapp.android.client.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.client.model.response.open.NearMeService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.NearMePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 5/7/17 12:39 PM
 */

public class NearMeModel {
    private static final String TAG = NearMeModel.class.getSimpleName();

    private static final NearMeService nearmeService;
    public static NearMePresenter nearMePresenter;

    static {
        nearmeService = RetrofitClient.getClient().create(NearMeService.class);
    }

    /**
     * @param did
     * @param storeInfoParam
     */
    public static void nearMeStore(String did, StoreInfoParam storeInfoParam) {
        nearmeService.nearMe(did, DEVICE_TYPE, storeInfoParam).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.body() != null) {
                    Log.d("Response NearMe", String.valueOf(response.body()));
                    nearMePresenter.nearMeResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                nearMePresenter.nearMeError();
            }
        });
    }

}
