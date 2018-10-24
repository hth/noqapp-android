package com.noqapp.android.client.model;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

import com.noqapp.android.client.model.response.open.NearMeService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.NearMePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;
import com.noqapp.android.client.utils.Constants;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 5/7/17 12:39 PM
 */

public class NearMeModel {
    private static final NearMeService nearmeService;
    private NearMePresenter nearMePresenter;

    public NearMeModel(NearMePresenter nearMePresenter) {
        this.nearMePresenter = nearMePresenter;
    }

    static {
        nearmeService = RetrofitClient.getClient().create(NearMeService.class);
    }

    /**
     * @param did
     * @param storeInfoParam
     */
    public void nearMeStore(String did, StoreInfoParam storeInfoParam) {
        nearmeService.nearMe(did, DEVICE_TYPE, storeInfoParam).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response NearMe", String.valueOf(response.body()));
                        nearMePresenter.nearMeResponse(response.body());
                    } else {
                        nearMePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        nearMePresenter.authenticationFailure();
                    } else {
                        nearMePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("NearMe failed", t.getLocalizedMessage(), t);
                nearMePresenter.nearMeError();
            }
        });
    }

    /**
     * @param did
     * @param storeInfoParam
     */
    public void nearMeHospitalAndDoctors(String did, StoreInfoParam storeInfoParam) {
        nearmeService.nearMe(did, DEVICE_TYPE, storeInfoParam).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response NearMeHospital", String.valueOf(response.body()));
                        nearMePresenter.nearMeHospitalResponse(response.body());
                    } else {
                        nearMePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        nearMePresenter.authenticationFailure();
                    } else {
                        nearMePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("NearMeHospital failed", t.getLocalizedMessage(), t);
                nearMePresenter.nearMeHospitalError();
            }
        });
    }

    public void search(String did, StoreInfoParam storeInfoParam) {
        nearmeService.search(did, DEVICE_TYPE, storeInfoParam).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response search", String.valueOf(response.body()));
                        nearMePresenter.nearMeResponse(response.body());
                    } else {
                        nearMePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        nearMePresenter.authenticationFailure();
                    } else {
                        nearMePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("onFailure search", t.getLocalizedMessage(), t);
                nearMePresenter.nearMeError();
            }
        });
    }

}
