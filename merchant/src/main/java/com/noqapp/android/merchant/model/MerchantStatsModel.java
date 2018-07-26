package com.noqapp.android.merchant.model;

import com.noqapp.android.merchant.model.response.api.MerchantStatsService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStatList;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.ChartPresenter;

import android.util.Log;
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
    private ChartPresenter chartPresenter;
    public MerchantStatsModel(ChartPresenter chartPresenter){
        this.chartPresenter = chartPresenter;
    }
    static {
        merchantStatsService = RetrofitClient.getClient().create(MerchantStatsService.class);
    }

    public void doctor(String did, String mail, String auth) {
        merchantStatsService.doctor(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<HealthCareStatList>() {
            @Override
            public void onResponse(Call<HealthCareStatList> call, Response<HealthCareStatList> response) {
                chartPresenter.chartResponse(response.body());
                Log.v("Chart Response ",response.body().toString());
            }

            @Override
            public void onFailure(Call<HealthCareStatList> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                chartPresenter.chartError();
                //TODO(Chandra) handle the errors
            }
        });
    }
}
