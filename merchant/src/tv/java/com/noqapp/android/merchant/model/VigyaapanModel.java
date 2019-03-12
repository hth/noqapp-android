package com.noqapp.android.merchant.model;

import com.noqapp.android.merchant.model.api.VigyaapanApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.VigyaapanPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonVigyaapanTVList;
import com.noqapp.android.merchant.utils.Constants;

import androidx.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VigyaapanModel {
    private static final String TAG = ClientInQueueModel.class.getSimpleName();

    protected static final VigyaapanApiUrls vigyaapanApiUrls;
    private VigyaapanPresenter vigyaapanPresenter;

    public void setVigyaapanPresenter(VigyaapanPresenter vigyaapanPresenter) {
        this.vigyaapanPresenter = vigyaapanPresenter;
    }

    static {
        vigyaapanApiUrls = RetrofitClient.getClient().create(VigyaapanApiUrls.class);
    }

    public void getAllVigyaapan(String did, String mail, String auth) {
        vigyaapanApiUrls.getAllVigyaapan(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonVigyaapanTVList>() {
            @Override
            public void onResponse(@NonNull Call<JsonVigyaapanTVList> call, @NonNull Response<JsonVigyaapanTVList> response) {
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
                        Log.e(TAG, ""+response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonVigyaapanTVList> call, @NonNull Throwable t) {
                Log.e("onFailure getVigyaapan", t.getLocalizedMessage(), t);
                vigyaapanPresenter.responseErrorPresenter(null);
            }
        });
    }
}
