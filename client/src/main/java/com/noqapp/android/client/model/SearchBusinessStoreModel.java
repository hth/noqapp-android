package com.noqapp.android.client.model;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

import com.noqapp.android.client.model.response.open.SearchBusinessStoreService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery;
import com.noqapp.android.client.utils.Constants;

import androidx.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 5/7/17 12:39 PM
 */

public class SearchBusinessStoreModel {
    private static final SearchBusinessStoreService SEARCH_BUSINESS_STORE_SERVICE;
    private SearchBusinessStorePresenter searchBusinessStorePresenter;

    public SearchBusinessStoreModel(SearchBusinessStorePresenter searchBusinessStorePresenter) {
        this.searchBusinessStorePresenter = searchBusinessStorePresenter;
    }

    static {
        SEARCH_BUSINESS_STORE_SERVICE = RetrofitClient.getClient().create(SearchBusinessStoreService.class);
    }

    /**
     * @param did
     * @param searchStoreQuery
     */
    public void otherMerchant(String did, SearchStoreQuery searchStoreQuery) {
        SEARCH_BUSINESS_STORE_SERVICE.otherMerchant(did, DEVICE_TYPE, searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response NearMe", String.valueOf(response.body()));
                        searchBusinessStorePresenter.nearMeResponse(response.body());
                    } else {
                        searchBusinessStorePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        searchBusinessStorePresenter.authenticationFailure();
                    } else {
                        searchBusinessStorePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("NearMe failed", t.getLocalizedMessage(), t);
                searchBusinessStorePresenter.nearMeError();
            }
        });
    }

    /**
     * @param did
     * @param searchStoreQuery
     */
    public void healthCare(String did, SearchStoreQuery searchStoreQuery) {
        SEARCH_BUSINESS_STORE_SERVICE.healthCare(did, DEVICE_TYPE, searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response NearMeHospital", String.valueOf(response.body()));
                        searchBusinessStorePresenter.nearMeHospitalResponse(response.body());
                    } else {
                        searchBusinessStorePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        searchBusinessStorePresenter.authenticationFailure();
                    } else {
                        searchBusinessStorePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("NearMeHospital failed", t.getLocalizedMessage(), t);
                searchBusinessStorePresenter.nearMeHospitalError();
            }
        });
    }

    public void search(String did, SearchStoreQuery searchStoreQuery) {
        SEARCH_BUSINESS_STORE_SERVICE.search(did, DEVICE_TYPE, searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response search", String.valueOf(response.body()));
                        searchBusinessStorePresenter.nearMeResponse(response.body());
                    } else {
                        searchBusinessStorePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        searchBusinessStorePresenter.authenticationFailure();
                    } else {
                        searchBusinessStorePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("onFailure search", t.getLocalizedMessage(), t);
                searchBusinessStorePresenter.nearMeError();
            }
        });
    }

}
