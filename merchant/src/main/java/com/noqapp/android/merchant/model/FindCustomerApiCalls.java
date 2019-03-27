package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.FindCustomerPresenter;

import android.util.Log;
import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindCustomerApiCalls {
    private static final String TAG = FindCustomerApiCalls.class.getSimpleName();
    private FindCustomerPresenter findCustomerPresenter;
    private static final FindCustomerApiUrls findCustomerApiUrls;

    static {
        findCustomerApiUrls = RetrofitClient.getClient().create(FindCustomerApiUrls.class);
    }

    public void setFindCustomerPresenter(FindCustomerPresenter findCustomerPresenter) {
        this.findCustomerPresenter = findCustomerPresenter;
    }

    public void findCustomer(String did, String mail, String auth, JsonBusinessCustomerLookup jsonBusinessCustomerLookup) {
        findCustomerApiUrls.findCustomer(did, Constants.DEVICE_TYPE, mail, auth, jsonBusinessCustomerLookup).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("findCustomer", String.valueOf(response.body()));
                        findCustomerPresenter.findCustomerResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while findCustomer");
                        findCustomerPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        findCustomerPresenter.authenticationFailure();
                    } else {
                        findCustomerPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("findCustomer fail", t.getLocalizedMessage(), t);
                findCustomerPresenter.responseErrorPresenter(null);
            }
        });
    }

}
