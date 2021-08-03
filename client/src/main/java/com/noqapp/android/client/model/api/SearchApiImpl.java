package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.SearchApi;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

public class SearchApiImpl {
    private static final SearchApi SEARCH_BUSINESS_STORE_API;
    private SearchBusinessStorePresenter searchBusinessStorePresenter;
    public BizStoreElasticList bizStoreElasticList;

    public SearchApiImpl(SearchBusinessStorePresenter searchBusinessStorePresenter) {
        this.searchBusinessStorePresenter = searchBusinessStorePresenter;
    }

    static {
        SEARCH_BUSINESS_STORE_API = RetrofitClient.getClient().create(SearchApi.class);
    }

    public void search(String did, String mail, String auth, SearchStoreQuery searchStoreQuery) {
        SEARCH_BUSINESS_STORE_API.search(did, DEVICE_TYPE, mail, auth,  searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response search auth", String.valueOf(response.body()));
                        searchBusinessStorePresenter.nearMeMerchant(response.body());
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
                searchBusinessStorePresenter.nearMeMerchantError();
            }
        });
    }

    public void business(String did, String mail, String auth, SearchStoreQuery searchStoreQuery) {
        SEARCH_BUSINESS_STORE_API.business(did, DEVICE_TYPE, mail, auth,  searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        bizStoreElasticList = response.body();
                        Log.d("Response business near", "For businessType " + bizStoreElasticList.getSearchedOnBusinessType() + " " + response.body());
                        switch (bizStoreElasticList.getSearchedOnBusinessType()) {
                            case CD:
                            case CDQ:
                                searchBusinessStorePresenter.nearMeCanteenResponse(response.body());
                                break;
                            case RS:
                            case RSQ:
                                searchBusinessStorePresenter.nearMeRestaurantsResponse(response.body());
                                break;
                            case DO:
                            case HS:
                                searchBusinessStorePresenter.nearMeHospitalResponse(response.body());
                                break;
                            case PW:
                                searchBusinessStorePresenter.nearMeTempleResponse(response.body());
                                break;
                            case ZZ:
                            default:
                                searchBusinessStorePresenter.nearMeMerchant(response.body());
                        }
                    } else {
                        if (response.body() != null)
                            searchBusinessStorePresenter.responseErrorPresenter(response.body().getError());
                        else
                            searchBusinessStorePresenter.responseErrorPresenter(response.code());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        searchBusinessStorePresenter.authenticationFailure();
                    } else {
                        if (response.body() != null)
                            searchBusinessStorePresenter.responseErrorPresenter(response.body().getError());
                        else
                            searchBusinessStorePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("Failed business near", t.getLocalizedMessage(), t);
                searchBusinessStorePresenter.responseErrorPresenter(new ErrorEncounteredJson());
            }
        });
    }
}
