package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.VigyaapanTypeEnum;
import com.noqapp.android.merchant.model.api.VigyaapanService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.VigyaapanPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonVigyaapanTV;
import com.noqapp.android.merchant.utils.Constants;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VigyaapanModel {
    private static final String TAG = ClientInQueueModel.class.getSimpleName();

    protected static final VigyaapanService vigyaapanService;
    private VigyaapanPresenter vigyaapanPresenter;

    public void setVigyaapanPresenter(VigyaapanPresenter vigyaapanPresenter) {
        this.vigyaapanPresenter = vigyaapanPresenter;
    }

    static {
        vigyaapanService = RetrofitClient.getClient().create(VigyaapanService.class);
    }

    /**
     * @param mail
     * @param auth
     */
    public void getVigyaapan(String did, String mail, String auth, VigyaapanTypeEnum vigyaapanType) {
        vigyaapanService.getVigyaapan(did, Constants.DEVICE_TYPE, mail, auth, vigyaapanType.getName()).enqueue(new Callback<JsonVigyaapanTV>() {
            @Override
            public void onResponse(@NonNull Call<JsonVigyaapanTV> call, @NonNull Response<JsonVigyaapanTV> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        vigyaapanPresenter.vigyaapanResponse(response.body());
                        Log.d("getVigyaapan", String.valueOf(response.body()));
                    } else {
                        Log.e(TAG, "Empty getVigyaapan");
                        vigyaapanPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        vigyaapanPresenter.authenticationFailure();
                    } else {
                        vigyaapanPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonVigyaapanTV> call, @NonNull Throwable t) {
                Log.e("onFailure getVigyaapan", t.getLocalizedMessage(), t);
                vigyaapanPresenter.responseErrorPresenter(null);
            }
        });
    }
}
