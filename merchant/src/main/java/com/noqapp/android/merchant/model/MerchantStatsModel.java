package com.noqapp.android.merchant.model;

import com.noqapp.android.merchant.model.response.api.MerchantStatsService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.stats.DoctorStats;
import com.noqapp.android.merchant.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 5/15/18 3:59 PM
 */
public class MerchantStatsModel {
    private static final String TAG = MerchantStatsModel.class.getSimpleName();

    private static final MerchantStatsService merchantStatsService;

    static {
        merchantStatsService = RetrofitClient.getClient().create(MerchantStatsService.class);
    }

    public static void doctor(String did, String mail, String auth, String codeQR) {
        merchantStatsService.doctor(did, Constants.DEVICE_TYPE, mail, auth ,codeQR).enqueue(new Callback<DoctorStats>() {
            @Override
            public void onResponse(Call<DoctorStats> call, Response<DoctorStats> response) {

            }

            @Override
            public void onFailure(Call<DoctorStats> call, Throwable t) {

            }
        });
    }
}
