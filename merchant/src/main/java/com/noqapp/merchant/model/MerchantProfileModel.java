package com.noqapp.merchant.model;

import android.util.Log;

import com.noqapp.merchant.BuildConfig;
import com.noqapp.merchant.model.response.api.MerchantProfileService;
import com.noqapp.merchant.network.RetrofitClient;
import com.noqapp.merchant.presenter.beans.JsonMerchant;
import com.noqapp.merchant.views.interfaces.MerchantPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/22/17 11:53 AM
 */

public class MerchantProfileModel {
    private static final String TAG = MerchantProfileModel.class.getSimpleName();

    private static final MerchantProfileService merchantProfileService;
    public static MerchantPresenter merchantPresenter;

    static {
        merchantProfileService = RetrofitClient.getClient().create(MerchantProfileService.class);
    }

    /**
     * @param mail
     * @param auth
     */
    public static void fetch(String mail, String auth) {
        merchantProfileService.fetch(mail, auth).enqueue(new Callback<JsonMerchant>() {
            @Override
            public void onResponse(Call<JsonMerchant> call, Response<JsonMerchant> response) {
                if (response.body() != null) {
                    merchantPresenter.merchantResponse(response.body());
                    Log.d("Response", String.valueOf(response.body()));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonMerchant> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }
}
