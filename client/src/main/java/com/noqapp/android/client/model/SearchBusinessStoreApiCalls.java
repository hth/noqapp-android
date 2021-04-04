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

    /**
     * @param did
     * @param searchStoreQuery
     */
    public void business(String did, SearchStoreQuery searchStoreQuery) {
        searchBusinessStoreApiUrls.business(did, DEVICE_TYPE, searchStoreQuery).enqueue(new Callback<BizStoreElasticList>() {
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
                Log.e("Failed business near", t.getLocalizedMessage(), t);
                switch (searchStoreQuery.getSearchedOnBusinessType()) {
                    case CD:
                    case CDQ:
                        searchBusinessStorePresenter.nearMeCanteenError();
                        break;
                    case RS:
                    case RSQ:
                        searchBusinessStorePresenter.nearMeRestaurantsError();
                        break;
                    case DO:
                    case HS:
                        searchBusinessStorePresenter.nearMeHospitalError();
                        break;
                    case PW:
                        searchBusinessStorePresenter.nearMeTempleError();
                        break;
                    case ZZ:
                    default:
                        searchBusinessStorePresenter.nearMeMerchantError();
                }
            }
        });
    }
}
