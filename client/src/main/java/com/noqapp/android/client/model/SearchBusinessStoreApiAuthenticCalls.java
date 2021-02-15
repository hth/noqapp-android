package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.SearchBusinessStoreApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery;
import com.noqapp.android.client.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

public class SearchBusinessStoreApiAuthenticCalls {
    private static final SearchBusinessStoreApiUrls searchBusinessStoreApiUrls;
    private SearchBusinessStorePresenter searchBusinessStorePresenter;


    public SearchBusinessStoreApiAuthenticCalls(SearchBusinessStorePresenter searchBusinessStorePresenter) {
        this.searchBusinessStorePresenter = searchBusinessStorePresenter;
    }

    static {
        searchBusinessStoreApiUrls = RetrofitClient.getClient().create(SearchBusinessStoreApiUrls.class);
    }

    public void search(String did,  String mail, String auth, SearchStoreQuery searchStoreQuery) {
        searchBusinessStoreApiUrls.search(did, DEVICE_TYPE, mail, auth,  searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
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
}
