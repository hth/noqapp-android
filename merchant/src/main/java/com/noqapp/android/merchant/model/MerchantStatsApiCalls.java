package com.noqapp.android.merchant.model;

import android.util.Log;

import com.noqapp.android.merchant.model.response.api.MerchantStatsApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStatList;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.ChartPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 5/15/18 3:59 PM
 */
public class MerchantStatsApiCalls {
    private static final String TAG = MerchantStatsApiCalls.class.getSimpleName();

    private static final MerchantStatsApiUrls merchantStatsApiUrls;
    private ChartPresenter chartPresenter;

    public MerchantStatsApiCalls(ChartPresenter chartPresenter) {
        this.chartPresenter = chartPresenter;
    }

    static {
        merchantStatsApiUrls = RetrofitClient.getClient().create(MerchantStatsApiUrls.class);
    }

    public void healthCare(String did, String mail, String auth) {
        merchantStatsApiUrls.healthCare(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<HealthCareStatList>() {
            @Override
            public void onResponse(Call<HealthCareStatList> call, Response<HealthCareStatList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        chartPresenter.chartResponse(response.body());
                        Log.v("Chart Response ", response.body().toString());
                    } else {
                        Log.e(TAG, "Empty Chart Response ");
                        chartPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        chartPresenter.authenticationFailure();
                    } else {
                        chartPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<HealthCareStatList> call, Throwable t) {
                Log.e("Chart Response", t.getLocalizedMessage(), t);
                chartPresenter.chartError();
            }

        });
    }
}
