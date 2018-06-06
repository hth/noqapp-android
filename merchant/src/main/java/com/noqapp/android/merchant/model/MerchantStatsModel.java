package com.noqapp.android.merchant.model;

import android.util.Log;

import com.noqapp.android.merchant.model.response.api.MerchantStatsService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.stats.DoctorStats;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.ChartPresenter;

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
    public static ChartPresenter chartPresenter;
    static {
        merchantStatsService = RetrofitClient.getClient().create(MerchantStatsService.class);
    }

    public static void doctor(String did, String mail, String auth, String codeQR) {
        merchantStatsService.doctor(did, Constants.DEVICE_TYPE, mail, auth ,codeQR).enqueue(new Callback<DoctorStats>() {
            @Override
            public void onResponse(Call<DoctorStats> call, Response<DoctorStats> response) {
                chartPresenter.chartResponse(response.body());
                Log.v("Chart Response ",response.body().toString());
            }

            @Override
            public void onFailure(Call<DoctorStats> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                chartPresenter.chartError();
                //TODO(Chandra) handle the errors
            }
        });
    }
}
