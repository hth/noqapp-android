package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.open.SearchBusinessStoreApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery;
import com.noqapp.android.client.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 5/7/17 12:39 PM
 */
public class SearchBusinessStoreApiCalls {
    private static final SearchBusinessStoreApiUrls searchBusinessStoreApiUrls;
    private SearchBusinessStorePresenter searchBusinessStorePresenter;
    public BizStoreElasticList bizStoreElasticList;
    private boolean responseReceived = false;

    public boolean isResponseReceived() {
        return responseReceived;
    }

    public void setResponseReceived(boolean responseReceived) {
        this.responseReceived = responseReceived;
    }

    public SearchBusinessStoreApiCalls(SearchBusinessStorePresenter searchBusinessStorePresenter) {
        this.searchBusinessStorePresenter = searchBusinessStorePresenter;
    }

    static {
        searchBusinessStoreApiUrls = RetrofitClient.getClient().create(SearchBusinessStoreApiUrls.class);
    }

    /**
     * @param did
     * @param searchStoreQuery
     */
    public void otherMerchant(String did, SearchStoreQuery searchStoreQuery) {
        searchBusinessStoreApiUrls.otherMerchant(did, DEVICE_TYPE, searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response NearMe", String.valueOf(response.body()));
                        searchBusinessStorePresenter.nearMeMerchant(response.body());
                        bizStoreElasticList = response.body();
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
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("NearMe failed", t.getLocalizedMessage(), t);
                searchBusinessStorePresenter.nearMeMerchantError();
            }
        });
    }

    /**
     * @param did
     * @param searchStoreQuery
     */
    public void canteen(String did, SearchStoreQuery searchStoreQuery) {
        searchBusinessStoreApiUrls.canteen(did, DEVICE_TYPE, searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response Canteen NearMe", String.valueOf(response.body()));
                        searchBusinessStorePresenter.nearMeCanteenResponse(response.body());
                        bizStoreElasticList = response.body();
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
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("NearMe canteen failed", t.getLocalizedMessage(), t);
                searchBusinessStorePresenter.nearMeCanteenError();
            }
        });
    }

    /**
     * @param did
     * @param searchStoreQuery
     */
    public void placeOfWorship(String did, SearchStoreQuery searchStoreQuery) {
        searchBusinessStoreApiUrls.placeOfWorship(did, DEVICE_TYPE, searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response temple NearMe", String.valueOf(response.body()));
                        searchBusinessStorePresenter.nearMeTempleResponse(response.body());
                        bizStoreElasticList = response.body();
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
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("NearMe temple failed", t.getLocalizedMessage(), t);
                searchBusinessStorePresenter.nearMeTempleError();
            }
        });
    }

    /**
     * @param did
     * @param searchStoreQuery
     */
    public void healthCare(String did, SearchStoreQuery searchStoreQuery) {
        searchBusinessStoreApiUrls.healthCare(did, DEVICE_TYPE, searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
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
        searchBusinessStoreApiUrls.search(did, DEVICE_TYPE, searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response search", String.valueOf(response.body()));
                        searchBusinessStorePresenter.nearMeMerchant(response.body());
                        bizStoreElasticList = response.body();
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
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("onFailure search", t.getLocalizedMessage(), t);
                searchBusinessStorePresenter.nearMeMerchantError();
            }
        });
    }


    public void kiosk(String did, SearchStoreQuery searchStoreQuery) {
        searchBusinessStoreApiUrls.kiosk(did, DEVICE_TYPE, searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
            @Override
            public void onResponse(@NonNull Call<BizStoreElasticList> call, @NonNull Response<BizStoreElasticList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response search", String.valueOf(response.body()));
                        searchBusinessStorePresenter.nearMeMerchant(response.body());
                        bizStoreElasticList = response.body();
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
                responseReceived = true;
            }

            @Override
            public void onFailure(@NonNull Call<BizStoreElasticList> call, @NonNull Throwable t) {
                Log.e("onFailure search", t.getLocalizedMessage(), t);
                searchBusinessStorePresenter.nearMeMerchantError();
            }
        });
    }
}
