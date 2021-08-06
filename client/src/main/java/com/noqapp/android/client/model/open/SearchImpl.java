package com.noqapp.android.client.model.open;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.open.Search;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchQuery;
import com.noqapp.android.client.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 5/7/17 12:39 PM
 */
public class SearchImpl {
    private static final Search SEARCH_BUSINESS_STORE;
    private SearchBusinessStorePresenter searchBusinessStorePresenter;
    public BizStoreElasticList bizStoreElasticList;
    private boolean responseReceived = false;

    public boolean isResponseReceived() {
        return responseReceived;
    }

    public void setResponseReceived(boolean responseReceived) {
        this.responseReceived = responseReceived;
    }

    public SearchImpl(SearchBusinessStorePresenter searchBusinessStorePresenter) {
        this.searchBusinessStorePresenter = searchBusinessStorePresenter;
    }

    static {
        SEARCH_BUSINESS_STORE = RetrofitClient.getClient().create(Search.class);
    }

    public void search(String did, SearchQuery searchQuery) {
        SEARCH_BUSINESS_STORE.search(did, DEVICE_TYPE, searchQuery).enqueue(new Callback<BizStoreElasticList>() {
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

    public void kiosk(String did, SearchQuery searchQuery) {
        SEARCH_BUSINESS_STORE.kiosk(did, DEVICE_TYPE, searchQuery).enqueue(new Callback<BizStoreElasticList>() {
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
     * @param searchQuery
     */
    public void business(String did, SearchQuery searchQuery) {
        SEARCH_BUSINESS_STORE.business(did, DEVICE_TYPE, searchQuery).enqueue(new Callback<BizStoreElasticList>() {
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
                switch (searchQuery.getSearchedOnBusinessType()) {
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
